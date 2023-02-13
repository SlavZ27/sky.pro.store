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
    price       NUMERIC   NOT NULL,
    title       TEXT    NOT NULL,
    description TEXT    NOT NULL
);



