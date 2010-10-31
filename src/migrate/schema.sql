DROP TABLE IF EXISTS users, articles, comments;

CREATE TABLE users
(
  id       int                   NOT NULL,
  username character varying(32) NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE INDEX users_id_idx ON users(id);

CREATE TABLE articles
(
  id        serial                      NOT NULL,
  title     text                        NOT NULL,
  teaser    text                        NOT NULL,
  body      text                        NOT NULL,
  sticky    boolean                     NOT NULL,
  hits      int                         NOT NULL,
  createdat timestamp without time zone NOT NULL,
  updatedat timestamp without time zone NOT NULL,
  userid    int                         NOT NULL,
  userids   text                        NOT NULL,
  CONSTRAINT articles_pkey PRIMARY KEY (id)
);

CREATE INDEX articles_updatedat_idx ON articles(updatedat);

CREATE TABLE article_versions
(
  id        serial                      NOT NULL,
  title     text                        NOT NULL,
  teaser    text                        NOT NULL,
  body      text                        NOT NULL,
  sticky    boolean                     NOT NULL,
  hits      int                         NOT NULL,
  createdat timestamp without time zone NOT NULL,
  updatedat timestamp without time zone NOT NULL,
  userid    int                         NOT NULL,

  articleid int                         NOT NULL,

  CONSTRAINT articles_pkey PRIMARY KEY (id)
);

CREATE TABLE comments
(
  id        serial                      NOT NULL,
  body      text                        NOT NULL,
  createdat timestamp without time zone NOT NULL,
  updatedat timestamp without time zone NOT NULL,
  userid    int                         NOT NULL,
  articleid int                         NOT NULL,
  CONSTRAINT comments_pkey PRIMARY KEY (id)
);

CREATE INDEX comments_created_idx ON comments(createdat);
