package ch.neon.releasetracker.release.infrastructure;

import ch.neon.releasetracker.release.domain.Release;
import ch.neon.releasetracker.release.domain.SearchCriteria;
import ch.neon.releasetracker.release.domain.enums.ReleaseStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@Slf4j
public class ReleaseSpecification implements Specification<Release> {
  private static final String NAME = "name";
  private static final String DESCRIPTION = "description";
  private static final String STATUS = "status";
  private static final String RELEASE_DATE = "releaseDate";
  private final SearchCriteria criteria;

  @Override
  public Predicate toPredicate(
      @NonNull Root<Release> root,
      @NonNull CriteriaQuery<?> query,
      @NonNull CriteriaBuilder criteriaBuilder) {

    return switch (criteria.element()) {
      case SEARCH_TERM -> buildForSearchTerm(root, criteriaBuilder);
      case DATE_FROM -> buildForDateFrom(root, criteriaBuilder);
      case DATE_TO -> buildForDateTo(root, criteriaBuilder);
      case STATUSES -> buildForStatuses(root, criteriaBuilder);
    };
  }

  private Predicate buildForSearchTerm(Root<Release> root, CriteriaBuilder criteriaBuilder) {
    String searchTerm =
        criteria.value() != null ? criteria.value().toString().toLowerCase() : StringUtils.EMPTY;

    Predicate namePredicate =
        criteriaBuilder.like(criteriaBuilder.lower(root.get(NAME)), "%" + searchTerm + "%");

    Predicate descriptionPredicate =
        criteriaBuilder.like(criteriaBuilder.lower(root.get(DESCRIPTION)), "%" + searchTerm + "%");

    return criteriaBuilder.or(namePredicate, descriptionPredicate);
  }

  private Predicate buildForDateFrom(Root<Release> root, CriteriaBuilder criteriaBuilder) {
    if (criteria.value() != null) {
      String date = criteria.value().toString();
      LocalDateTime startOfDate =
          LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MIN);
      return criteriaBuilder.greaterThanOrEqualTo(root.get(RELEASE_DATE), startOfDate);
    }
    return criteriaBuilder.conjunction();
  }

  private Predicate buildForDateTo(Root<Release> root, CriteriaBuilder criteriaBuilder) {
    if (criteria.value() != null) {
      String date = criteria.value().toString();
      LocalDateTime endOfDate =
          LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
      return criteriaBuilder.lessThanOrEqualTo(root.get(RELEASE_DATE), endOfDate);
    }
    return criteriaBuilder.conjunction();
  }

  private Predicate buildForStatuses(Root<Release> root, CriteriaBuilder criteriaBuilder) {
    if (!(criteria.value() instanceof List<?> statuses) || statuses.isEmpty()) {
      return criteriaBuilder.conjunction();
    }

    try {

      List<ReleaseStatus> enumValues =
          statuses.stream()
              .map(
                  obj -> {
                    if (obj instanceof ReleaseStatus rs) return rs;
                    return ReleaseStatus.valueOf(obj.toString().toUpperCase().replace(" ", "_"));
                  })
              .toList();

      return root.get(STATUS).in(enumValues);

    } catch (IllegalArgumentException e) {
      log.error("Invalid status value provided in search criteria: {}", criteria.value());

      return criteriaBuilder.disjunction();
    }
  }
}
