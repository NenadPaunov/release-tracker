package ch.neon.releasetracker.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

  @NotAudited
  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @NotAudited
  @CreatedBy
  @Column(nullable = false, updatable = false)
  private String createdBy;

  @NotAudited
  @LastModifiedDate
  @Column(insertable = false)
  private LocalDateTime lastUpdateAt;

  @NotAudited
  @LastModifiedBy
  @Column(insertable = false)
  private String updatedBy;
}
