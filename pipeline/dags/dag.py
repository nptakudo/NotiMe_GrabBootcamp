from datetime import datetime
from airflow import DAG
from airflow.decorators import task
from airflow.operators.bash import BashOperator
from airflow.operators.python import PythonOperator
from airflow.operators.dummy import DummyOperator
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
# from webscrape.webscrape.spiders.testdag import hehe
from hehe import huhu

# A DAG represents a workflow, a collection of tasks
with DAG(dag_id="daily", start_date=datetime(2024, 5, 7), schedule="0 0 * * *") as dag:
    # Tasks are represented as operators
    get_rds_src_list = PythonOperator(task_id='get_rds_src_list', 
                                      python_callable=get_rds_src_list)
    
    scrape_link = BashOperator(
        task_id='scrape_link',
        bash_command="python /opt/airflow/dags/webscrape/webscrape/spiders/daily_scrape.py",
    )

    remove_existed_link = SparkSubmitOperator(
        task_id="remove_existed_link",
        application="/opt/airflow/dags/data-processing-spark/remove_existed_link.py",
        conn_id="spark_default"
    )
    enforce_title_content = BashOperator(
        task_id='enforce_title_content', 
        bash_command="python /opt/airflow/dags/webscrape/webscrape/spiders/schema_enforce.py",
    )
    ingest_to_rds = SparkSubmitOperator(
        task_id='insert_to_rds', 
        application="/opt/airflow/dags/data-processing-spark/silver_to_rds.py",
        conn_id="spark_default"
    )
    ingest_to_gold = SparkSubmitOperator(
        task_id="ingest_to_gold",
        application="/opt/airflow/dags/data-processing-spark/silver_to_gold.py",
        conn_id="spark_default"
    )

    # Set dependencies between tasks
    get_rds_src_list>> scrape_link >> remove_existed_link>>enforce_title_content\
    >>[ingest_to_gold, ingest_to_rds]
