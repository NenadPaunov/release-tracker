package ch.neon.releasetracker.release.infrastructure.specification;

import ch.neon.releasetracker.release.domain.Release;
import ch.neon.releasetracker.release.domain.ReleaseSearchParams;
import ch.neon.releasetracker.release.domain.SearchCriteria;
import ch.neon.releasetracker.release.domain.enums.ReleaseSearchElement;
import ch.neon.releasetracker.release.infrastructure.ReleaseSpecification;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class ReleaseSearchParamUtil {

  public static Specification<Release> toSpecification(ReleaseSearchParams params) {
    List<SearchCriteria> searchCriteriaList = getSearchCriteriaList(params);

    if (searchCriteriaList.isEmpty()) {
      return null;
    }

    return searchCriteriaList.stream()
        .map(criteria -> (Specification<Release>) new ReleaseSpecification(criteria))
        .reduce(Specification::and)
        .orElse(null);
  }

  private static List<SearchCriteria> getSearchCriteriaList(ReleaseSearchParams searchParams) {
    List<SearchCriteria> criteriaList = new ArrayList<>();

    for (Field field : searchParams.getClass().getDeclaredFields()) {
      Optional.ofNullable(ReleaseSearchElement.getFromParamName(field.getName()))
          .ifPresent(
              element -> {
                try {
                  field.setAccessible(true);
                  Object fieldValue = field.get(searchParams);
                  if (!isEmpty(field.getType(), fieldValue)) {
                    criteriaList.add(new SearchCriteria(element, fieldValue));
                  }
                } catch (IllegalAccessException e) {
                  log.warn("Failed to access field '{}': {}", field.getName(), e.getMessage());
                }
              });
    }

    return criteriaList;
  }

  static boolean isEmpty(Class<?> c, Object value) {
    if (value == null) return true;

    if (c == String.class) {
      return StringUtils.isBlank((String) value);
    }

    if (c == List.class) {
      return ((List<?>) value).isEmpty();
    }

    if (c == LocalDate.class || c == LocalDateTime.class || c.isEnum()) {
      return false;
    }

    return false;
  }
}
