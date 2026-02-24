package ch.neon.releasetracker.release.adapter.out;

import ch.neon.releasetracker.common.audit.UserRevisionEntity;
import ch.neon.releasetracker.release.application.response.ReleaseAuditResponse;
import ch.neon.releasetracker.release.domain.Release;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReleaseAuditService {

  private final EntityManager entityManager;

  @Transactional(readOnly = true)
  public List<ReleaseAuditResponse> getReleaseHistory(UUID releaseId) {
    AuditReader auditReader = AuditReaderFactory.get(entityManager);

    return ((List<?>)
            auditReader
                .createQuery()
                .forRevisionsOfEntity(Release.class, false, true)
                .add(AuditEntity.id().eq(releaseId))
                .getResultList())
        .stream()
            .map(
                rowObj -> {
                  Object[] row = (Object[]) rowObj;
                  Release entity = (Release) row[0];
                  UserRevisionEntity userRevisionEntity = (UserRevisionEntity) row[1];
                  RevisionType revisionType = (RevisionType) row[2];

                  return new ReleaseAuditResponse(
                      userRevisionEntity.getId(),
                      revisionType.name(),
                      userRevisionEntity.getUserId(),
                      LocalDateTime.ofInstant(
                          Instant.ofEpochMilli(userRevisionEntity.getRevtstmp()),
                          ZoneId.systemDefault()),
                      Map.of("name", entity.getName(), "status", entity.getStatus()));
                })
            .toList();
  }
}
