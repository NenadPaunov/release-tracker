package ch.neon.releasetracker.common.config;

import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@TestConfiguration
@EnableJpaAuditing
public class TestJpaConfig {

  @Bean
  @Primary
  public AuditorAware<String> auditorProvider() {
    return () ->
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(Authentication::getName)
            .or(() -> Optional.of("test-admin"));
  }
}
