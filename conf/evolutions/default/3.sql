# --- !Ups

CREATE TABLE task (
  id                     BIGINT(20)  NOT NULL AUTO_INCREMENT,
  listId                 BIGINT(20) NOT NULL,
  description            VARCHAR(255) NOT NULL,
  completedDate          DATE NULL,
  createdDate            DATE DEFAULT GETDATE() NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO task (listId, description) VALUES (1, 'Take out trash');
INSERT INTO task (listId, description) VALUES (2, 'Create todo list');
INSERT INTO task (listId, description) VALUES (2, 'Do more awesome stuff');

# --- !Downs
DROP TABLE task
