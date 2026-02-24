CREATE TYPE release_status AS ENUM (
    'CREATED',
    'IN_DEVELOPMENT',
    'ON_DEV',
    'QA_DONE_ON_DEV',
    'ON_STAGING',
    'QA_DONE_ON_STAGING',
    'ON_PROD',
    'DONE'
);

CREATE TABLE release (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_update_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    name TEXT NOT NULL,
    description TEXT,
    status release_status NOT NULL DEFAULT 'CREATED',
    release_date TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    version BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX idx_release_name_unique ON release(name) WHERE deleted = false;

CREATE INDEX idx_release_status ON release(status);
CREATE INDEX idx_release_date ON release(release_date);
CREATE INDEX idx_release_deleted ON release(deleted);
CREATE INDEX idx_release_version ON release(version);