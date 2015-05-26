# --- !Ups

CREATE TABLE list (
  id                     BIGINT(20)  NOT NULL AUTO_INCREMENT,
  ownerId                BIGINT(20) NOT NULL,
  name                   VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO list (ownerId, name) VALUES (1, 'Personal');
INSERT INTO list (ownerId, name) VALUES (1, 'Work');

# --- !Downs
DROP TABLE list
