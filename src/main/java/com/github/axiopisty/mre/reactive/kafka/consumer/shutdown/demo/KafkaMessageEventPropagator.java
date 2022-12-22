package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo;

import com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.kafka.consumer.KafkaMessageConsumer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;

@Slf4j
@Service
public class KafkaMessageEventPropagator {
  
  private final KafkaMessageConsumer consumer;
  
  private final ApplicationEventPublisher publisher;
  
  private Optional<Disposable> disposable;
  
  public KafkaMessageEventPropagator(KafkaMessageConsumer consumer, ApplicationEventPublisher publisher) {
    this.consumer = consumer;
    this.publisher = publisher;
  }
  
  @PreDestroy
  public void cleanup() {
    log.debug("KafkaMessageEventPropagator.cleanup - disposing of subscription");
    disposable.ifPresent(x -> x.dispose());
  }
  
  @PostConstruct
  public void postConstruct() {
    log.debug("KafkaMessageEventPropagator.postConstruct - created");
    disposable = Optional.of(
      consumer
      .consume()
      .doOnNext(x -> {
        log.info("Consumed Kafka Message. Propagating it into the Spring ApplicationEventPublisher: {}", x);
        publisher.publishEvent(x);
      })
      .doOnError(t -> log.warn("Unable to consume from Kafka because : {}", t.getMessage()))
      .subscribe()
    );
  }
  
}
