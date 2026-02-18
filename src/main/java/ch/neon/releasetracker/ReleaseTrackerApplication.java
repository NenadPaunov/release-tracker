package ch.neon.releasetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
public class ReleaseTrackerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReleaseTrackerApplication.class, args);
  }
}
