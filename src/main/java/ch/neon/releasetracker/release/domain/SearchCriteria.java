package ch.neon.releasetracker.release.domain;

import ch.neon.releasetracker.release.domain.enums.ReleaseSearchElement;

public record SearchCriteria(ReleaseSearchElement element, Object value) {}
