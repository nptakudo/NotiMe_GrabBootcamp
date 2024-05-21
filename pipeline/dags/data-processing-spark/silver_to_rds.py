from pyspark.sql import SparkSession
from deltalake.writer import write_deltalake
from deltalake import DeltaTable
from pyspark.sql.functions import udf
from pyspark.sql.types import StringType, StructField, StructType
from pyspark.sql import functions as F
from pyspark.sql.functions import col, regexp_extract, current_timestamp
import os
from dotenv import load_dotenv
load_dotenv()

def run_code(spark):
    # Define the schema for the JSON data
    schema = StructType([
        StructField("src", StringType(), True),
        StructField("url", StringType(), True),
        StructField("title", StringType(), True),
        StructField("text", StringType(), True)
    ])

    # Read JSON data from S3
    # Read the JSON file with the defined schema
    df = spark.read.format("json") \
                .schema(schema) \
                .option("multiline", "true") \
                .load("s3a://silver/source_outputs.json")
    df=df.withColumnRenamed('url', 'urls')
    df = df.repartition(100)


    # Define the JDBC URL and properties
    db_host = os.getenv("DB_HOST")
    db_port = os.getenv("DB_PORT")
    db_name = os.getenv("DB_NAME")
    jdbc_url = f"jdbc:{db_host}:{db_port}/{db_name}"
    properties = {
        "user": os.getenv("DB_USER"),
        "password": os.getenv("DB_PASSWORD"),
        "driver": "org.postgresql.Driver"
    }
    # Write the DataFrame to the 'post' table in the RDS database
    write_to_rds_posts(df, jdbc_url, properties)

    # Write the DataFrame to the 'source' table in the RDS database
    write_to_rds_source(df, jdbc_url, properties)



def write_to_rds_posts(post_df, jdbc_url, properties):
    # Query to fetch source_id based on src
    source_id_df = spark.read.jdbc(url=jdbc_url, table="(SELECT id, url FROM source) AS src_table", properties=properties)
    post_df = post_df.filter(col("title").isNotNull())
    # Join the DataFrame with the source ids based on src to add the source_id to posts
    posts_df = post_df.join(source_id_df, post_df.src == source_id_df.url, "left_outer") \
                .select(
                    col("title"),
                    current_timestamp().alias("publish_date"),
                    col("urls").alias("url"),
                    col("text").alias("raw_text"),
                    col("id").alias("source_id")  # Assuming 'id' is the source_id from the 'source' table
                )

    # Show the DataFrame to verify its structure
    posts_df = posts_df.filter("length(url) <= 1000")
    posts_df.show()
    posts_df.write.jdbc(url=jdbc_url, table="post", mode="append", properties=properties)


def write_to_rds_source(src_df, jdbc_url, properties):
    src_df = extract_domain_name(src_df)

    src_df.write.jdbc(url=jdbc_url, table="source", mode="append", properties=properties)


def extract_domain_name(df):
    pattern = r'http[s]?://(?:www\.)?([^\/]+)'

    # Create the DataFrame selecting and transforming the 'src' column
    src_df = df.select(
        regexp_extract(col("src"), pattern, 1).alias("name"),
        col("src")
    ).distinct()

    return src_df



if __name__ == "__main__":
    spark = SparkSession.builder \
        .appName("S3Access") \
        .config("spark.executor.memory", "1g") \
        .config("spark.driver.memory", "1g") \
        .config("spark.memory.fraction", "0.9") \
        .config("spark.master", "spark://spark-master:7077") \
        .config("spark.eventLog.enabled", "true") \
        .config("spark.hadoop.fs.s3a.access.key", os.getenv("DATALAKE_ACCESS_KEY_ID")) \
        .config("spark.hadoop.fs.s3a.secret.key", os.getenv("DATALAKE_SECRET_ACCESS_KEY")) \
        .config("spark.hadoop.fs.s3a.endpoint", "http://host.docker.internal:9000") \
        .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false") \
        .config("spark.hadoop.fs.s3a.path.style.access", "true") \
        .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem") \
        .config('spark.hadoop.fs.s3a.aws.credentials.provider', 'org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider')\
        .config("spark.jars.packages", "io.delta:delta-core_2.12:2.3.0,org.apache.hadoop:hadoop-aws:3.2.2,com.amazonaws:aws-java-sdk:1.12.721,org.postgresql:postgresql:42.7.3") \
        .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension") \
        .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog") \
        .getOrCreate()
    spark.conf.set("spark.sql.debug.maxToStringFields", 100)
    run_code(spark)