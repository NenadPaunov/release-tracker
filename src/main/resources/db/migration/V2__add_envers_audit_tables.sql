CREATE TABLE revinfo (
    rev BIGINT NOT NULL,
    revtstmp BIGINT,
    user_id TEXT,
    PRIMARY KEY (rev)
);

CREATE SEQUENCE revinfo_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE release_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    name VARCHAR(100),
    status VARCHAR(255),
    release_date TIMESTAMP,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_release_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);