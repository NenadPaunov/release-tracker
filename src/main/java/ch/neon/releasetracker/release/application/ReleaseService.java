package ch.neon.releasetracker.release.application;

import ch.neon.releasetracker.common.exception.ReleaseNameAlreadyExistsException;
import ch.neon.releasetracker.common.exception.ReleaseNotFoundException;
import ch.neon.releasetracker.release.application.mapper.ReleaseMapper;
import ch.neon.releasetracker.release.application.request.ReleaseRequest;
import ch.neon.releasetracker.release.application.response.ReleaseResponse;
import ch.neon.releasetracker.release.domain.Release;
import ch.neon.releasetracker.release.domain.ReleaseSearchParams;
import ch.neon.releasetracker.release.infrastructure.ReleaseRepository;
import ch.neon.releasetracker.release.infrastructure.specification.ReleaseSearchParamUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseService {
  private final ReleaseRepository releaseRepository;
  private final ReleaseMapper releaseMapper;
  private final ReleaseValidator releaseValidator;

  public Page<ReleaseResponse> getReleases(ReleaseSearchParams params, Pageable pageable) {
    Specification<Release> spec = ReleaseSearchParamUtil.toSpecification(params);
    return releaseRepository.findAll(spec, pageable).map(releaseMapper::map);
  }

  public ReleaseResponse getReleaseById(UUID id) {
    return releaseRepository
        .findById(id)
        .map(releaseMapper::map)
        .orElseThrow(
            () -> {
              log.warn("Release search failed: id {} not found", id);
              return new ReleaseNotFoundException(id);
            });
  }

  @Transactional
  public ReleaseResponse createRelease(ReleaseRequest request) {
    Release release = releaseMapper.map(request);
    releaseValidator.validateDate(release.getReleaseDate());
    if (releaseRepository.existsByName(request.name())) {
      throw new ReleaseNameAlreadyExistsException(request.name());
    }

    if (release.getReleaseDate() == null) {
      release.setReleaseDate(LocalDateTime.now());
    }

    Release savedRelease = releaseRepository.save(release);
    log.info("Successfully created release with id: {}", savedRelease.getId());
    return releaseMapper.map(savedRelease);
  }

  @Transactional
  public ReleaseResponse updateRelease(UUID id, ReleaseRequest request) {
    Release existingRelease =
        releaseRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn("Update failed: Release {} not found", id);
                  return new ReleaseNotFoundException(id);
                });

    releaseValidator.validateForUpdate(existingRelease, request.releaseDate());

    if (!existingRelease.getName().equals(request.name())
        && releaseRepository.existsByName(request.name())) {
      throw new ReleaseNameAlreadyExistsException(request.name());
    }

    existingRelease.setName(request.name());
    existingRelease.setDescription(request.description());
    existingRelease.setReleaseDate(request.releaseDate());
    existingRelease.setStatus(request.status());

    Release updated = releaseRepository.save(existingRelease);
    log.info("Successfully updated release with id: {}", id);
    return releaseMapper.map(updated);
  }

  @Transactional
  public void deleteRelease(UUID id) {
    if (!releaseRepository.existsById(id)) {
      log.warn("Delete failed: Release {} does not exist", id);
      throw new ReleaseNotFoundException(id);
    }
    releaseRepository.deleteById(id);
    log.info("Successfully deleted release with id: {}", id);
  }
}
