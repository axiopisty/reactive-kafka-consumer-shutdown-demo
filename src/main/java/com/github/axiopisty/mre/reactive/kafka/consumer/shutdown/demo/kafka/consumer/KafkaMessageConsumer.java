package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.kafka.consumer;

import reactor.core.publisher.Flux;

public interface KafkaMessageConsumer {
  
  Flux<Object> consume();
  
}
