package ch.neon.releasetracker.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "revinfo")
@RevisionEntity(UserRevisionListener.class)
@Getter
@Setter
public class UserRevisionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo_seq")
  @SequenceGenerator(name = "revinfo_seq", sequenceName = "revinfo_seq", allocationSize = 1)
  @RevisionNumber
  @Column(name = "rev")
  private Long id;

  @RevisionTimestamp private long revtstmp;

  @Column(name = "user_id")
  private String userId;
}
