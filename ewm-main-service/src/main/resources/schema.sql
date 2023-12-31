drop table if exists users cascade;
drop table if exists categories cascade;
drop table if exists locations cascade;
drop table if exists events cascade;
drop table if exists participations cascade;
drop table if exists compilations cascade;
drop table if exists compilation_events cascade;
drop table if exists comments cascade;

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email VARCHAR(512) NOT NULL,
    "username" VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    "category_name" VARCHAR(255) UNIQUE                 NOT NULL,
    PRIMARY KEY (category_id)
);

CREATE TABLE IF NOT EXISTS locations
(
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat         real                                    NOT NULL,
    lon         real                                    NOT NULL,
    PRiMARY KEY (location_id)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY UNIQUE            NOT NULL,
    annotation         VARCHAR(2000)                                             NOT NULL,
    category_id        BIGINT REFERENCES categories (category_id)                NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE                               NOT NULL,
    description        VARCHAR(7000)                                             NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE                               NOT NULL,
    initiator_id       BIGINT REFERENCES users (user_id),
    location_id        BIGINT REFERENCES locations (location_id),
    paid               boolean                                                   NOT NULL,
    participant_limit  int                                                       NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE                                       ,
    request_moderation boolean                                                   NOT NULL,
    state              varchar(125)                                              NOT NULL,
    title              VARCHAR(120)                                              NOT NULL,
    PRIMARY KEY (event_id)
);

CREATE TABLE IF NOT EXISTS participations
(
    participation_id BIGINT GENERATED BY DEFAULT AS IDENTITY                  NOT NULL,
    created          TIMESTAMP WITHOUT TIME ZONE                              NOT NULL,
    event_id         BIGINT REFERENCES events (event_id)                      NOT NULL,
    requester_id     BIGINT REFERENCES users (user_id)                        NOT NULL,
    state            varchar(64)                                              NOT NULL,
    PRIMARY KEY (participation_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY UNIQUE NOT NULL,
    pinned         boolean                                        NOT NULL,
    title          VARCHAR(512)                                   NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id BIGINT NOT NULL REFERENCES compilations (compilation_id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES events (event_id)                   ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY                  NOT NULL,
    text  varchar(1024)                                                 NOT NULL,
    owner_id BIGINT REFERENCES users (user_id)                          NOT NULL,
    event_id BIGINT REFERENCES events (event_id)                        NOT NULL,
    date TIMESTAMP WITHOUT TIME ZONE                                    NOT NULL,
    PRIMARY KEY (comment_id)
);