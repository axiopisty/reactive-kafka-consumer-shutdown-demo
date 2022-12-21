package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo;

import com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.kafka.consumer.KafkaMessageConsumer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessageEventPropagator {
  
  private final KafkaMessageConsumer consumer;
  
  private final ApplicationEventPublisher publisher;
  
  public KafkaMessageEventPropagator(KafkaMessageConsumer consumer, ApplicationEventPublisher publisher) {
    this.consumer = consumer;
    this.publisher = publisher;
  }
  
  @PostConstruct
  public void postConstruct() {
    log.debug("KafkaMessageEventPropagator - created");
    consumer
      .consume()
      .doOnNext(x -> {
        log.info("Consumed Kafka Message. Propagating it into the Spring ApplicationEventPublisher: {}", x);
        publisher.publishEvent(x);
      })
      .doOnError(t -> log.warn("Unable to consume from Kafka because : {}", t.getMessage()))
      .subscribe();
  }
  
}
