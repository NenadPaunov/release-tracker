package ch.neon.releasetracker.release.domain;

import ch.neon.releasetracker.common.audit.AuditableEntity;
import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Audited
@SQLDelete(
    sql =
        "UPDATE release SET deleted = true, deleted_at = NOW(), version = version + 1 WHERE id=? AND version = ?")
@SQLRestriction("deleted = false")
@NoArgsConstructor
public class Release extends AuditableEntity {
  @Id
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column(nullable = false)
  private String name;

  @NotAudited private String description;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(columnDefinition = "release_status")
  private ReleaseStatus status;

  @Column(name = "release_date", nullable = false)
  private LocalDateTime releaseDate;

  @NotAudited
  @Column(nullable = false)
  private boolean deleted = false;

  @NotAudited
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Version private Long version;

  @PrePersist
  private void ensureUuid() {
    if (id == null) {
      id = UuidCreator.getTimeOrderedEpoch();
    }
  }
}
