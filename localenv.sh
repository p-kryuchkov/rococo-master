#!/bin/bash

echo '### Stop and remove containers ###'
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

echo '### Run network ###'
docker network inspect rococo-network >/dev/null 2>&1 || \
docker network create rococo-network

echo '### Run databases ###'
docker run --name rococo-all-db \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD=secret \
-v rococo:/var/lib/mysql \
-d mysql:8.4.7 \

echo '### Run  Zookeeper ###'
docker run --name zookeeper \
  --network rococo-network \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  -p 2181:2181 \
  -d confluentinc/cp-zookeeper:7.3.2

echo '### Run  Kafka ###'
docker run --name kafka \
  --network rococo-network \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -p 9092:9092 \
  -d confluentinc/cp-kafka:7.3.2

echo "✅ Postgres, Zookeeper, Kafka up  rococo-network"