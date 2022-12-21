package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo;

import java.time.Duration;
import java.time.Instant;
import javafx.application.Application;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@EnableAsync
public class Main {
  
  public static void main(String[] args) {
    boolean forceShutdown = args.length == 1 && "force-shutdown".equals(args[0]);
    log.debug("Main.main - Launching JavaFx Application");
    if(!forceShutdown) {
      log.info("Main.main - PASS THE 'force-shutdown' COMMAND LINE ARGUMENT IF YOU WANT THE APPLICATION TO SHUT DOWN FORCEFULLY");
    }
    Application.launch(JavaFxApplication.class, args);
    log.debug("Main.main - Both the JavaFx and SpringBoot applications have been told to shutdown.");
    log.debug("Main.main - The JRE will now exit once all non-daemon threads have completed.");
    if(forceShutdown) {
      forceShutdownAfterWaiting(Duration.ofSeconds(5));
    }
  }
  
  @SneakyThrows
  private static void forceShutdownAfterWaiting(Duration duration) {
    log.debug("");
    log.debug("");
    Instant start = Instant.now();
    final long maxSecondsToWait = duration.toSeconds();
    Duration ellapsedDuration;
    do {
      Thread.sleep(1000);
      ellapsedDuration = Duration.between(start, Instant.now());
      log.debug("Main.forceShutdownAfterWaiting [{} of {}] second(s)", ellapsedDuration.toSeconds(), maxSecondsToWait);
      ThreadAnalyzer.dump(false);
    } while (ThreadAnalyzer.nonDaemonThreadsAreRunning() && ellapsedDuration.compareTo(duration) < 0);
    
    if(ThreadAnalyzer.nonDaemonThreadsAreRunning()) {
      log.debug("Application has not stopped gracefully. Forcing shutdown.");
      ThreadAnalyzer.dump(true);
    } else {
      log.debug("Application has stopped gracefully!");
    }
    

  }
  
}
