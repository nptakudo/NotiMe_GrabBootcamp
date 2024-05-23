from pyspark.sql import SparkSession
from deltalake.writer import write_deltalake
from deltalake import DeltaTable
from pyspark.sql.functions import  broadcast
from pyspark.sql.types import StringType, StructField, StructType
from pyspark.sql import functions as F
import os
from dotenv import load_dotenv
load_dotenv()

def run_code(spark):
    # Define the schema for the JSON data
    schema = StructType([
        StructField("src", StringType(), True),
        StructField("url", StringType(), True)
    ])

    # Read JSON data from S3
    df = spark.read.format("json") \
                   .schema(schema) \
                   .option("multiline", "true") \
                   .load("s3a://raw/source_outputs.json")
    df=df.withColumnRenamed('url', 'urls')
    df = df.repartition(100)
    df = broadcast(df)
    # Set to temp view
    df.createOrReplaceTempView("blogs_list_broadcast")

    deltaTable = spark.read.format("delta").load("s3a://gold/blogs_list")
    deltaTable = deltaTable.repartition(500)
    # Register the Delta table as a temporary view
    deltaTable.createOrReplaceTempView("blogs_list")

    # Execute the SQL query with the broadcast
    result = spark.sql("""
    SELECT blogs_list.src, blogs_list.url
    FROM blogs_list
    JOIN temp_view ON blogs_list_broadcast.url = temp_view.url
    """)

    # Write to silver parquet
    result.write.format("parquet").mode("overwrite").save("s3a://silver/blogs_list")

if __name__ == "__main__":
    spark = SparkSession.builder \
            .appName("S3Access") \
            .config("spark.executor.memory", "4g") \
            .config("spark.driver.memory", "4g") \
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