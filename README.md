# Problem Statement

A standalone reactive kafka consumer application using `spring-kafka` and `reactor-kafka` does not shut down gracefully.

# MCRE explanation

I found this question [Reactor Kafka how to gracefully shutdown?](https://stackoverflow.com/q/74497869/328275). One of
the comments on that thread asked for a [MCRE](https://stackoverflow.com/help/minimal-reproducible-example), so I created 
this small application to demonstrate that there is no graceful way of shutting down a standalone application that is 
using `spring-kafka` and `reactor-kafka`. This demo uses JavaFX, Spring Boot, Docker, and Maven.

It would be nice, if this example is used by the project maintainers to provide the means whereby graceful shutdown is 
accomplished, if they would provide a pull request to this repository to demonstrate what (if anything) needs to be
changed in order to gracefully shut down the consumer.

# Build Environment

```bash
$ docker --version
Docker version 20.10.12, build 20.10.12-0ubuntu2~20.04.1


$ docker-compose --version
docker-compose version 1.25.0, build unknown


$ mvn --version
Apache Maven 3.8.5 (3599d3414f046de2324203b78ddcf9b5e4388aa0)
Maven home: ~/.sdkman/candidates/maven/current
Java version: 17.0.1, vendor: Azul Systems, Inc., runtime: ~/.sdkman/candidates/java/17.0.1.fx-zulu
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-135-generic", arch: "amd64", family: "unix"
```

# How to run the demo and see the problem

1. Run an instance of Kafka in a docker container

```bash
$ docker-compose up -d --build
```

2. Build the maven project

```bash
$ mvn clean install
```

3. Run the standalone application *(**WITHOUT** forceful shut down upon closing the GUI)*

```bash
$ java -jar target/reactive-kafka-consumer-shutdown-demo-0.0.1-SNAPSHOT.jar
```

This will launch a simple JavaFx GUI (a login form that does nothing). Once
the GUI is displayed all you need to do is click the close button to shut
down the application. Notice the log messages which print to the console
show lifecycle events. This enables you to see what happens during the setup
and teardown of each of the application components (ie: 
[Main](src/main/java/com/github/axiopisty/mre/reactive/kafka/consumer/shutdown/demo/Main.java), 
[JavaFxApplication](src/main/java/com/github/axiopisty/mre/reactive/kafka/consumer/shutdown/demo/JavaFxApplication.java),
[StageInitializer](src/main/java/com/github/axiopisty/mre/reactive/kafka/consumer/shutdown/demo/javafx/StageInitializer.java),
[LoginController](src/main/java/com/github/axiopisty/mre/reactive/kafka/consumer/shutdown/demo/javafx/LoginController.java),
[ReactiveKafkaMessageConsumer](src/main/java/com/github/axiopisty/mre/reactive/kafka/consumer/shutdown/demo/kafka/consumer/impl/ReactiveKafkaMessageConsumer.java),
[KafkaMessageEventPropagator](src/main/java/com/github/axiopisty/mre/reactive/kafka/consumer/shutdown/demo/KafkaMessageEventPropagator.java)).

Specifically, you'll be able to see log messages that indicate that both the Spring Framework and the JavaFxApplication lifecycle methods have been properly invoked to, under normal circumstances, shut down the application gracefully. However, you'll also notice that the JRE does not shut down. It just sits there waiting for all the non-daemon threads to stop so the JRE can exit. But this never happens. You'll need to kill the process (by using CTRL+C) in order for the application to stop. Then, you can see the exit code from the killed application. In my case, I get code 130.


```bash
^C $ echo "Application exit code: $?"
Application exit code: 130
```

4. Run the standalone application *(**WITH** forceful shut down upon closing the GUI)*

Next, lets run the application again, but this time, tell the application to forcefully kill itself once the GUI is closed. Everything is just like in the first run except after Spring Boot and JavaFX closes and execution resumes in [Main](src/main/java/com/github/axiopisty/mre/reactive/kafka/consumer/shutdown/demo/Main.java), We will wait up to 5 seconds for the non-daemon thread created by the Kafka Consumer to stop by itself. Once it does not stop after 5 seconds, the application will interrupt the thread. This will force the thread to stop. You can inspect the console logs for more details.

```bash
$ java -jar target/reactive-kafka-consumer-shutdown-demo-0.0.1-SNAPSHOT.jar force-shutdown && echo "Application exit code: $?"
```

This time the application launches, and you can inspect the logs again. The difference will be in the log messages, and the application will shut down by itself after interrupting the thread. Hopefully, seeing that the non-daemon thread created by the Reactive Kafka Consumer is preventing the application from shutting down gracefully will be helpful to come up with a solution to this problem.

Please notice I created the method [ReactiveKafkaMessageConsumer.shutdown](src/main/java/com/github/axiopisty/mre/reactive/kafka/consumer/shutdown/demo/kafka/consumer/impl/ReactiveKafkaMessageConsumer.java). On line 30, you can see that I'm trying to "pause" the `ReactiveKafkaConsumerTemplate`. I commented that line. Shouldn't there be another method that allows us to `shutdown` the `ReactiveKafkaConsumerTemplate` gracefully?

5. Don't forget to shut down the docker containers once you're done experimenting:

```bash
$ docker-compose down
```

# Expected Behavior

Once the GUI is closed and the Spring application and the JavaFx application lifecycles have completed (execution is returned to the `Main.main` method), the JRE should exit gracefully without needing to manually interrupt the Kafka Consumer thread.