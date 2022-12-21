package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.kafka.consumer.impl;

import com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.kafka.consumer.KafkaMessageConsumer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ReactiveKafkaMessageConsumer implements KafkaMessageConsumer {
  
  private final ReactiveKafkaConsumerTemplate<String, Object> template;
  
  public ReactiveKafkaMessageConsumer(ReactiveKafkaConsumerTemplate<String, Object> template) {
    this.template = template;
  }
  
  @PreDestroy
  public void shutdown() {
    log.warn("ReactiveKafkaMessageConsumer shutdown invoked - how do we shut down the consumer gracefully?");
    template
      .assignment()
      .doOnNext(tp -> log.debug(
        "ReactiveKafkaConsumerTemplate pause topic={}, partition={}",
        tp.topic(),
        tp.partition()
      ))
      .flatMap(template::pause) /* Shouldn't there be a shutdown method? */
      .subscribe();
  }
  
  @Override
  public Flux<Object> consume() {
    return template
      .receiveAutoAck()
      .publishOn(Schedulers.boundedElastic())
      .doOnError(e -> log.warn(e.getMessage(), e))
      .doOnNext(x -> log.info(
        "received key={}, value={} from topic={}, offset={}",
        x.key(),
        x.value(),
        x.topic(),
        x.offset()
      ))
      .map(ConsumerRecord::value);
  }
  
}
