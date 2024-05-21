from pyspark.sql import SparkSession
from deltalake.writer import write_deltalake
from deltalake import DeltaTable
from pyspark.sql.functions import udf
from pyspark.sql.types import StringType, StructField, StructType
from pyspark.sql import functions as F

def create_schema(spark):
    # Ensure the schema exists
    spark.sql("CREATE SCHEMA IF NOT EXISTS gold;")
    spark.sql("USE gold;")
    # Drop the table if it exists
    spark.sql("DROP TABLE IF EXISTS blogs_list;")

    # Create a new Delta table
    spark.sql("""
        CREATE TABLE blogs_list (
            src STRING,
            blog STRING
        ) USING DELTA
        LOCATION 's3a://gold/blogs_list'
    """)

def run_code(spark):
    #Create schema if not exists
    create_schema(spark)

    # Define the schema for the JSON data
    schema = StructType([
        StructField("src", StringType(), True),
        StructField("url", StringType(), True),
        StructField("title", StringType(), True),
        StructField("text", StringType(), True)
    ])

    # Read JSON data from S3
    df = spark.read.format("json") \
                   .schema(schema) \
                   .option("multiline", "true") \
                   .load("s3a://silver/silver_outputs.json")

    # Write to Delta Lake, partitioned 
    (df.write.format("delta")
       .mode("append")
       .option("maxRecordsPerFile", "100000")
       .save('/s3a://gold/blogs_list'))



if __name__ == "__main__":
    spark = SparkSession.builder \
        .appName("S3Access") \
        .config("spark.executor.memory", "1g") \
        .config("spark.driver.memory", "1g") \
        .config("spark.memory.fraction", "0.9") \
        .config("spark.master", "spark://spark-master:7077") \
        .config("spark.eventLog.enabled", "true") \
        .config("spark.hadoop.fs.s3a.access.key", "7Vatx4rSRWLj73ArsqAS") \
        .config("spark.hadoop.fs.s3a.secret.key", "9v1Z718T3zUvvLDV2h2xZXZwjMJaOSScM6AHOSSq") \
        .config("spark.hadoop.fs.s3a.endpoint", "http://host.docker.internal:9000") \
        .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false") \
        .config("spark.hadoop.fs.s3a.path.style.access", "true") \
        .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem") \
        .config('spark.hadoop.fs.s3a.aws.credentials.provider', 'org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider')\
        .config("spark.jars.packages", "io.delta:delta-core_2.12:2.3.0,org.apache.hadoop:hadoop-aws:3.2.2,com.amazonaws:aws-java-sdk:1.12.721") \
        .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension") \
        .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog") \
        .getOrCreate()
    spark.conf.set("spark.sql.debug.maxToStringFields", 100)
    run_code(spark)