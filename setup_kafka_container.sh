#!/bin/bash

set -e

KAFKA_CONTAINER_NAME="chess-evaluations-kafka"

# Start Kafka container using KRaft mode (no Zookeeper)
if ! docker ps --format '{{.Names}}' | grep -q "^$KAFKA_CONTAINER_NAME$"; then
  echo "🚀 Starting Kafka container..."
  docker run -d --rm \
    --name chess-evaluations-kafka \
    --hostname kafka \
    -p 9092:9092 \
    -e KAFKA_KRAFT_CLUSTER_ID=chess-dojo-cluster \
    -e KAFKA_CFG_NODE_ID=1 \
    -e KAFKA_CFG_PROCESS_ROLES=broker,controller \
    -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
    -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
    -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
    -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
    -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@kafka:9093 \
    -e KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true \
    bitnami/kafka:latest

else
  echo "✅ Kafka container already running."
fi

# Wait for Kafka to be ready
echo "⏳ Waiting for Kafka to be ready on port 9092..."
until nc -z localhost 9092; do sleep 1; done
echo "✅ Kafka is ready."

echo "🧠 Kafka is now configured to auto-create topics."
