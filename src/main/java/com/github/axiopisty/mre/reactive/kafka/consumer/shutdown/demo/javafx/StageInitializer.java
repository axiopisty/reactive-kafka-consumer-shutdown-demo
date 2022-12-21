package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.javafx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent>, ApplicationContextAware {
  
  private final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.getDefault());
  private final ClassLoader classLoader = getClass().getClassLoader();
  
  private FXMLLoader loader;
  
  private static Stage stage;
  
  private ApplicationContext applicationContext;
  
  @Override
  public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
    log.debug("StageInitializer.onApplicationEvent - received StageReadyEvent.");
    Platform.runLater(() -> {
      log.debug("StageInitializer.onApplicationEvent - Showing GUI.");
      stage = stageReadyEvent.getStage();
      stage.setOnCloseRequest(event -> {
        log.debug("");
        log.debug("stage.onCloseRequest event received.");
      });
      stage.setTitle(messages.getString("application.title"));
      loader = new FXMLLoader();
      final String path = "/fxml/login.fxml";
      final URL location = getClass().getResource(path);
      final InputStream in = getClass().getResourceAsStream(path);
      loader.setLocation(location);
      loader.setResources(messages);
      loader.setControllerFactory(applicationContext::getBean);
      loader.setClassLoader(classLoader);
      loader.setCharset(StandardCharsets.UTF_8);
      Optional<Scene> root;
      try {
        root = Optional.of(new Scene(loader.load(in), 382, 145));
      } catch (IOException e) {
        log.error(String.format("Could not show view: [%s] because: [%s]", path, e.getMessage()), e);
        root = Optional.empty();
      }
      root.ifPresent(stage::setScene);
      
      stage.show();
    });
  }
  
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    log.debug("StageInitializer.setApplicationContext");
    this.applicationContext = applicationContext;
  }
  
}
