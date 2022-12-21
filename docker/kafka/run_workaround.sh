#!/bin/sh

########################################################################################################################
# Turn off the check for the KAFKA_ZOOKEEPER_CONNECT parameter. Without this line, an error will be thrown:
# > Command [/usr/local/bin/dub ensure KAFKA_ZOOKEEPER_CONNECT] FAILED !
########################################################################################################################
sed -i '/KAFKA_ZOOKEEPER_CONNECT/d' /etc/confluent/docker/configure

########################################################################################################################
# Ignore the cub zk-ready. Without this command everything will end up with another exception:
# > zk-ready: error: too few arguments
########################################################################################################################
sed -i 's/cub zk-ready/echo ignore zk-ready/' /etc/confluent/docker/ensure

########################################################################################################################
# Required step for KRaft, which formats the storage directory with a new cluster identifier.
# Please keep in mind that the specified UUID value has to be 16 bytes of a base64-encoded UUID.
########################################################################################################################
echo "kafka-storage format --ignore-formatted -t NqnEdODVKkiLTfJvqd1uqQ== -c /etc/kafka/kafka.properties" >> /etc/confluent/docker/ensure
