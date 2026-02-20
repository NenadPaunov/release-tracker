package ch.neon.releasetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ReleaseTrackerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReleaseTrackerApplication.class, args);
  }
}
