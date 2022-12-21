package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo.javafx;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginController {
  
  @FXML
  public TextField user;
  
  @FXML
  public TextField password;
  
  @FXML
  public Button loginButton;
  
  @PostConstruct
  public void postConstruct() {
    log.debug("LoginController.postConstruct - This is where we would create non-javafx resources if needed");
  }

  @PreDestroy
  public void preDestroy() {
    log.debug("LoginController.preDestroy - This is where we would clean up resources if needed.");
  }
  
  @FXML
  private void initialize() {
    log.debug("LoginController.initialize");
    log.debug("");
  }
  
}
