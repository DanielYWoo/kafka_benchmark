# kafka_benchmark

a micro benchmark on Macbook Air M1 2021
16G RAM, 256 SSD

3 kafka nodes, 1 zookeeper node

The test shows that, don't use kafkaJS (without producer buffer) in any cases.

# Java

```
cd java
mvn clean package
cd target
java -cp kafka_test-1.0-SNAPSHOT-jar-with-dependencies.jar  TestProducer 127.0.0.1:9092 test-topic <loops>
java -cp kafka_test-1.0-SNAPSHOT-jar-with-dependencies.jar  TestConsumer 127.0.0.1:9092 test-topic <group id> <duration in seconds>

```
Sending: TPS=1.3M Latency=0.0008ms
Consuming: TPS=945K Bytes-in=92MB/s

# KafkaJS
```
cd kafkajs

npm install

change the line:
  brokers: ['localhost:9092', 'localhost:9093']

node send.js
```

Sending: TPS=12K Latency=0.08ms
