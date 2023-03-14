-- liquibase formatted sql

-- changeSet nadillustrator:1
CREATE TABLE avatar
(
    id        SERIAL  NOT NULL PRIMARY KEY,
    id_author INTEGER NOT NULL,
    path      TEXT    NOT NULL
);

CREATE TABLE image
(
    id     SERIAL  NOT NULL PRIMARY KEY,
    id_ads INTEGER NOT NULL,
    path   TEXT    NOT NULL
);

CREATE TABLE comment
(
    id        SERIAL    NOT NULL PRIMARY KEY,
    id_author INTEGER   NOT NULL,
    text      TEXT      NOT NULL,
    date_time TIMESTAMP NOT NULL
);

CREATE TABLE users
(
    id         SERIAL NOT NULL PRIMARY KEY,
    email      TEXT,
    first_name TEXT,
    last_name  TEXT,
    phone      TEXT,
    reg_date   DATE   NOT NULL,
    id_avatar  INTEGER,
    password   TEXT,
    username   TEXT,
    role       TEXT
);

CREATE TABLE ads
(
    id          SERIAL  NOT NULL PRIMARY KEY,
    id_author   INTEGER NOT NULL,
    price       NUMERIC NOT NULL,
    title       TEXT    NOT NULL,
    description TEXT    NOT NULL
);

-- changeSet nadillustrator:2
DROP TABLE comment;
CREATE TABLE comment
(
    id        SERIAL    NOT NULL PRIMARY KEY,
    id_author INTEGER   NOT NULL,
    id_ads    INTEGER   NOT NULL,
    text      TEXT      NOT NULL,
    date_time TIMESTAMP NOT NULL
);
-- changeSet evnag:3
ALTER TABLE ads
    ALTER COLUMN price TYPE INTEGER;
ALTER TABLE ads
    ALTER COLUMN price SET NOT NULL;

-- changeSet nadillustrator:4
ALTER TABLE avatar
    DROP COLUMN id_author;

-- changeSet nadillustrator:5
ALTER TABLE image
    DROP COLUMN id_ads;
ALTER TABLE ads
    ADD COLUMN id_image INTEGER;

-- changeSet zaytsev:6
alter table comment
    drop constraint comment_pkey;
alter table comment
    add primary key (id, id_ads);

-- changeSet nadillustrator:7
ALTER TABLE ads
    ADD COLUMN date_time TIMESTAMP;

-- changeSet zaytsev:8
alter table users
    add column enabled boolean default true;

-- changeSet zaytsev:9
create table authorities
(
    id        SERIAL PRIMARY KEY,
    username  varchar(30) not null ,
    authority varchar(30) not null
);
create index users_username_index
    on users (username);
alter table users
    add constraint users_pk
        unique (username);
-- changeSet zaytsev:10
alter table ads
    ADD CONSTRAINT ads_id_author FOREIGN KEY (id_author) REFERENCES users (id);
alter table ads
    ADD CONSTRAINT ads_id_image FOREIGN KEY (id_image) REFERENCES image (id);
alter table comment
    ADD CONSTRAINT comment_id_author FOREIGN KEY (id_author) REFERENCES users (id);
alter table users
    ADD CONSTRAINT users_id_avatar FOREIGN KEY (id_avatar) REFERENCES avatar (id);
alter table authorities
    ADD CONSTRAINT authorities_username FOREIGN KEY (username) REFERENCES users (username);
-- changeSet zaytsev:11
create index authorities_username_index
    on authorities (username);
alter table authorities
    add constraint authorities_uk
        unique (username, authority);
-- changeSet zaytsev:12
alter table users
    drop column role;
-- changeSet zaytsev:13
alter table users
alter column reg_date set default now();