package ch.neon.releasetracker.release.infrastructure;

import ch.neon.releasetracker.release.domain.Release;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseRepository
    extends JpaRepository<Release, UUID>, JpaSpecificationExecutor<Release> {

  boolean existsByName(String name);
}
