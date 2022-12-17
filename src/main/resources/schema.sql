CREATE TABLE IF NOT EXISTS MPA
(
    MPA_ID   integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    MPA_NAME varchar(30)
);

CREATE TABLE IF NOT EXISTS FILM
(
    FILM_ID      integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    FILM_NAME    varchar(200) NOT NULL,
    DESCRIPTION  varchar(200) NOT NULL,
    RELEASE_DATE date         NOT NULL,
    DURATION     integer      NOT NULL,
    MPA_ID       integer REFERENCES MPA (MPA_ID)
);

CREATE TABLE IF NOT EXISTS GENRE
(
    GENRE_ID   integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    GENRE_NAME varchar(30)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE
(
    FILM_GENRE_ID integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    FILM_ID       integer REFERENCES FILM (FILM_ID) ON DELETE CASCADE,
    GENRE_ID      integer REFERENCES GENRE (GENRE_ID) ON DELETE CASCADE,
    UNIQUE (GENRE_ID, FILM_ID)
);

CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID   integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL     varchar(30)  NOT NULL,
    LOGIN     varchar(30)  NOT NULL,
    USER_NAME varchar(200) NOT NULL,
    BIRTHDAY  date         NOT NULL
);

CREATE TABLE IF NOT EXISTS LIKES
(
    LIKE_ID integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    FILM_ID integer REFERENCES FILM (FILM_ID) ON DELETE CASCADE,
    USER_ID integer REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    UNIQUE (FILM_ID, USER_ID)
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP
(
    FRIENDSHIP_ID integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    USER1_ID      integer REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    USER2_ID      integer REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    STATUS        varchar(30),
    UNIQUE (USER1_ID, USER2_ID)
);