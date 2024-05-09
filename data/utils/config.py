import os
from typing import Dict
from utils.data_storage import *
from dotenv import load_dotenv
load_dotenv()

def print_sth():
    print("Hello")

def get_datalake_creds() -> DataLakeConnection:
    return DataLakeConnection(
        endpoint_url=os.getenv("DATALAKE_ENDPOINT_URL", "http://localhost:9000/"),
        aws_access_key_id=os.getenv("DATALAKE_ACCESS_KEY_ID", ""),
        aws_secret_access_key=os.getenv("DATALAKE_SECRET_ACCESS_KEY", ""),
        region_name=os.getenv("DATALAKE_REGION_NAME", "us-east-1"),
    )

def get_warehouse_creds() -> DBConnection:
    return DBConnection(
        user=os.getenv("WAREHOUSE_USER", ""),
        password=os.getenv("WAREHOUSE_PASSWORD", ""),
        db=os.getenv("WAREHOUSE_DB", ""),
        host=os.getenv("WAREHOUSE_HOST", ""),
        port=int(os.getenv("WAREHOUSE_PORT", 5432)),
    )


def get_customer_db_creds() -> DBConnection:
    return DBConnection(
        user=os.getenv("CUSTOMER_DB_USER", ""),
        password=os.getenv("CUSTOMER_DB_PASSWORD", ""),
        db=os.getenv("CUSTOMER_DB_DB", ""),
        host=os.getenv("CUSTOMER_DB_HOST", ""),
        port=5432,
    )


def get_aws_creds() -> Dict[str, str]:
    return {
        "endpoint_url": os.getenv("AWS_ENDPOINT_URL", ""),
        "aws_access_key_id": os.getenv("AWS_ACCESS_KEY_ID", ""),
        "aws_secret_access_key": os.getenv("AWS_SECRET_ACCESS_KEY", ""),
        "region_name": os.getenv("AWS_REGION_NAME", ""),
    }

#test the function get_datalake_creds
# print(get_datalake_creds())
