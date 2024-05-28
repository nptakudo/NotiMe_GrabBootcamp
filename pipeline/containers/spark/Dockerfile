# For windows users
# FROM deltaio/delta-docker:latest   

# For mac users
FROM deltaio/delta-docker:latest_arm64


USER root

COPY ./requirements.txt ./
RUN pip install -r requirements.txt

# # Copy SQL DDL and DML to SPARK_HOME dir
# COPY ./setup.sql ./
# COPY ./count.sql ./

# # Copy tpch data generator into the container
# COPY ./tpch-dbgen ./tpch-dbgen/

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    rsync && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
# Download hadoop-aws library
RUN curl -L https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-aws/3.3.2 -o /opt/spark/jars/hadoop-aws-3.3.2.jar
COPY ./conf/spark-defaults.conf "$SPARK_HOME/conf/spark-defaults.conf"
COPY ./aws-java-sdk-1.12.721.jar "$SPARK_HOME/jars/aws-java-sdk-1.12.721.jar"
# COPY ./conf/metrics.properties "$SPARK_HOME/conf/metrics.properties"
ENV SPARK_CONF_DIR="$SPARK_HOME/config"
ENV SPARK_MASTER="spark://spark-master:7077"
ENV SPARK_MASTER_HOST spark-master
ENV SPARK_MASTER_PORT 7077
ENV PYSPARK_PYTHON python3

# Create and event logging directory to store job logs
RUN mkdir /tmp/spark-events

RUN chmod u+x /opt/spark/sbin/* && \
    chmod u+x /opt/spark/bin/*

ENV PYTHONPATH=$SPARK_HOME/python/:$PYTHONPATH

COPY entrypoint.sh .

ENTRYPOINT ["./entrypoint.sh"]