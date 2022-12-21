package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo;

import com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.javafx.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class JavaFxApplication extends Application {
  
  private ConfigurableApplicationContext applicationContext;
  
  @Override
  public void init() {
    Platform.setImplicitExit(true);
    final String[] args = getParameters().getRaw().toArray(String[]::new);
    log.debug("JavaFxApplication.init - Launching Spring application context");
    applicationContext = new SpringApplicationBuilder(Main.class)
      .headless(false)
      .run(args);
  }
  
  @Override
  public void start(Stage stage) {
    log.debug("JavaFxApplication.start");
    applicationContext.publishEvent(new StageReadyEvent(stage));
  }
  
  @Override
  public void stop() {
    log.debug("JavaFxApplication.stop - Stopping Spring application context");
    applicationContext.close();
    log.debug("JavaFxApplication.stop - Invoking Platform.exit()");
    Platform.exit();
  }
  
}
