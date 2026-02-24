package ch.neon.releasetracker.release.application.mapper;

import ch.neon.releasetracker.common.mapper.ReleaseMapperConfig;
import ch.neon.releasetracker.release.application.request.ReleaseRequest;
import ch.neon.releasetracker.release.application.response.ReleaseResponse;
import ch.neon.releasetracker.release.domain.Release;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = ReleaseMapperConfig.class)
public interface ReleaseMapper {
  ReleaseResponse map(Release release);

  List<ReleaseResponse> map(List<Release> releases);

  Release map(ReleaseRequest request);
}
