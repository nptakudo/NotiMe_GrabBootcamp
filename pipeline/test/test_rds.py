import pytest
from sqlalchemy import create_engine
from sqlalchemy.exc import SQLAlchemyError
from utils.config import get_warehouse_creds  # Import the function from your module

def test_postgresql_connection():
    # Get database credentials
    db_creds = get_warehouse_creds()

    # Construct the connection URL
    connection_url = f"postgresql://{db_creds.user}:{db_creds.password}@{db_creds.host}:{db_creds.port}/{db_creds.db}"

    # Attempt to create an engine and connect to the database
    try:
        engine = create_engine(connection_url)
        # Try to connect to the database
        with engine.connect() as connection:
            assert connection  # Simple assertion to check if connection object is not None
            # Optionally, perform a simple retrieval operation
            result = connection.execute("SELECT 1")
            assert list(result) == [(1,)], "Database should return 1"
    except SQLAlchemyError as e:
        pytest.fail(f"Database connection failed: {str(e)}")
