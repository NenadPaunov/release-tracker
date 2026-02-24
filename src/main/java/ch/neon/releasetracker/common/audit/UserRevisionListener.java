package ch.neon.releasetracker.common.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserRevisionListener implements RevisionListener {

  @Override
  public void newRevision(Object revisionEntity) {
    UserRevisionEntity userRevisionEntity = (UserRevisionEntity) revisionEntity;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      userRevisionEntity.setUserId(authentication.getName());
    } else {
      userRevisionEntity.setUserId("SYSTEM");
    }
  }
}
