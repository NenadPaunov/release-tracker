package ch.neon.releasetracker.common.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Configuration
public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.of("SYSTEM");
    }

    if (authentication.getPrincipal() instanceof Jwt jwt) {
      String username = jwt.getClaimAsString("preferred_username");
      return Optional.ofNullable(username != null ? username : jwt.getSubject());
    }

    return Optional.of(authentication.getName());
  }
}
