package org.devshred;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LuceneApplication {

  public static void main(String[] args) {
    SpringApplication.run(LuceneApplication.class, args);
  }
}
