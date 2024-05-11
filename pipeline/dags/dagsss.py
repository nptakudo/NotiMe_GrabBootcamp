from datetime import datetime

from airflow import DAG
from airflow.decorators import task
from airflow.operators.bash import BashOperator
from airflow.operators.python import PythonOperator
# from webscrape.webscrape.spiders.testdag import hehe
from hehe import huhu



# A DAG represents a workflow, a collection of tasks
with DAG(dag_id="demo", start_date=datetime(2024, 5, 7), schedule="0 0 * * *") as dag:
    # Tasks are represented as operators
    hello = BashOperator(task_id="hello", bash_command="echo hello")

    scrapy_task = PythonOperator(
        task_id='scrapy_task',
        python_callable=huhu,
    )
    scrape_audiophile_data = BashOperator(
        task_id="scrape_audiophile_data",
        bash_command="python /opt/airflow/dags/hehe.py",
    )
    @task()
    def airflow():
        print("ahiahiahosfasdfsaf")

    # Set dependencies between tasks
    hello>> scrapy_task >> scrape_audiophile_data  >> airflow()