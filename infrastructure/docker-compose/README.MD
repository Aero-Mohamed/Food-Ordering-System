# 1. Volumes folder structure
Create a folder called `volumes` inside the `infrastructure/docker-compose` folder with the following structure:

- volumes/
  - zookeeper/
    - data
    - transactions
  - kafka/ 
    - broker-1
    - broker-2
    - broker-3

# 2. Docker Commands
- `docker-compose -f common.yml -f zookeeper.yml up`
- `docker-compose -f common.yml -f kafka_cluster.yml up`
- Run one-time to create the topics: 
  - `docker-compose -f common.yml -f init_kafka.yml up`