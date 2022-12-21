package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.config;

import com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.kafka.consumer.KafkaMessageConsumer;
import com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.kafka.consumer.impl.ReactiveKafkaMessageConsumer;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

@Slf4j
@Configuration
public class KafkaConsumerConfig {
  
  final String kafkaTopicName = "demo-topic";
  
  @Bean
  public ReceiverOptions<String, Object> receiverOptions(
    KafkaProperties kafkaProperties
  ) {
    final Map<String, Object> props = kafkaProperties.buildConsumerProperties();
    ReceiverOptions<String, Object> basicReceiverOptions = ReceiverOptions.create(
      props
    );
    return basicReceiverOptions.subscription(Collections.singletonList(kafkaTopicName));
  }
  
  @Bean
  public ReactiveKafkaConsumerTemplate<String, Object> reactiveKafkaConsumerTemplate(
    ReceiverOptions<String, Object> receiverOptions
  ) {
    return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
  }
  
  @Bean
  public KafkaMessageConsumer kafkeMessageConsumer(
    ReactiveKafkaConsumerTemplate<String, Object> template
  ) {
    log.debug("Creating KafkaMessageConsumer for topic={}", kafkaTopicName);
    return new ReactiveKafkaMessageConsumer(template);
  }

}
