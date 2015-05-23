# --- !Ups

CREATE TABLE usertable (
  id                     BIGINT(20)  NOT NULL AUTO_INCREMENT,
  email                  VARCHAR(100) NOT NULL,
  name                   VARCHAR(255) NOT NULL,
  password               VARCHAR(50) NOT NULL,
  createdate             DATE DEFAULT GETDATE() NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY IDX_ID (email)
);

INSERT INTO usertable (email, name, password) VALUES ('kevin.devrou@gmail.com', 'Kevin DeVrou', 'secret123');

# --- !Downs
DROP TABLE usertable
