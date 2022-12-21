package com.github.axiopisty.mre.reactive.kafka.consumer.shutdown.demo;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

@Slf4j
public abstract class ThreadAnalyzer {
  
  public static boolean nonDaemonThreadsAreRunning() {
    final boolean nonDaemonThreadsIsEmpty = Thread
      .getAllStackTraces()
      .keySet()
      .stream()
      .filter(x -> !x.isDaemon() && !x.getName().equals("main"))
      .toList()
      .isEmpty();
    return !nonDaemonThreadsIsEmpty;
  }
  
  public static void dump(boolean forceQuit) {
    final List<Thread> threads = Thread
      .getAllStackTraces()
      .keySet()
      .stream()
      .filter(x -> !x.isDaemon() && !x.getName().equals("main"))
      .toList();
    
    if (forceQuit) {
      log.info(
        "\tThreadAnalyzer.dump - non-daemon threads that need to be killed because they didn't already shutdown: {}",
        threads.size()
      );
    } else {
      log.info(
        "\tThreadAnalyzer.dump - non-daemon threads: {}",
        threads.size()
      );
    }

    threads
      .forEach(thread -> {
        log.debug(format(
          "\t\t[%-62s] daemon=%-5s alive=%-5s interrupted=%-5s",
          thread.getName(),
          thread.isDaemon(),
          thread.isAlive(),
          thread.isInterrupted()
        ));
        if (forceQuit) {
          log.debug("\tThreadAnalyzer.dump - Trying to interrupt/kill thread");
          thread.interrupt();
        }
      });
  }
  
}
