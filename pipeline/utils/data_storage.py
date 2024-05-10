from contextlib import contextmanager
from dataclasses import dataclass

# import psycopg2

@dataclass
class DataLakeConnection:
    """
    Dataclass for DataLake Connection
    
    Args:
    endpoint_url (str): The endpoint url of the DataLake (e.g. http://localhost:9000/)
    aws_access_key_id (str): The access key account for accessing Minio/S3
    aws_secret_access_key: The secret access key for accessing Minio/S3
    region_name (str): The region name (default is us-east-1)
    """
    
    endpoint_url: str
    aws_access_key_id: str
    aws_secret_access_key: str
    region_name: str = "us-east-1"

@dataclass
class DBConnection:
    db: str
    user: str
    password: str
    host: str
    port: int = 5432


# class WarehouseConnection:
#     def __init__(self, db_conn: DBConnection):
#         self.conn_url = (
#             f"postgresql://{db_conn.user}:{db_conn.password}@"
#             f"{db_conn.host}:{db_conn.port}/{db_conn.db}"
#         )

#     @contextmanager
#     def managed_cursor(self, cursor_factory=None):
#         self.conn = psycopg2.connect(self.conn_url)
#         self.conn.autocommit = True
#         self.curr = self.conn.cursor(cursor_factory=cursor_factory)
#         try:
#             yield self.curr
#         finally:
#             self.curr.close()
#             self.conn.close()