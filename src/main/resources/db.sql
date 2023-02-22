create table if not exists users(
  id bigint auto_increment,
  username varchar(100),
  firstname varchar(100),
  lastname varchar(100),
  user_key varchar(100),
  user_role varchar(20),
  accountLocked varchar(5),
  primary key (id)
);

create table if not exists articles(
  id bigint auto_increment,
  author_id bigint,
  title varchar(1000),
  synopsis varchar(10000),
  fulltext varchar(100000),
  status varchar(100),
  primary key (id)
);

INSERT INTO users (username, firstname, lastname, user_key, user_role, accountLocked) VALUES ('johndoe@acme.com', 'John', 'Doe', 'KEY-123456', 'Administrator', 'NO');
INSERT INTO users (username, firstname, lastname, user_key, user_role, accountLocked) VALUES ('janedoe@acme.com', 'Jane', 'Doe', 'KEY-213112', 'Publisher', 'NO');
INSERT INTO users (username, firstname, lastname, user_key, user_role, accountLocked) VALUES ('jobs@apple.com', 'Steve', 'Jobs', 'KEY-432342', 'Author', 'NO');
INSERT INTO users (username, firstname, lastname, user_key, user_role, accountLocked) VALUES ('gates@microsoft.com', 'Bill', 'Gates', 'KEY-343222', 'Author', 'NO');
INSERT INTO users (username, firstname, lastname, user_key, user_role, accountLocked) VALUES ('elonmusk@tesla.com', 'Elon', 'Musk', 'KEY-343277', 'Reviewer', 'NO');

INSERT INTO articles (author_id, title, synopsis, fulltext, status) VALUES (3, 'Introducing ChatGPT', 'ChatGPT is not your friend', '<p><strong>What is ChatGPT?</strong></p><p>ChatGPT is a chatbot launched by OpenAI in November 2022. It is built on top of OpenAI&#39;s GPT-3 family of large language models and is fine-tuned with both supervised and reinforcement learning techniques.&nbsp;<a href="https://en.wikipedia.org/wiki/ChatGPT">Wikipedia</a></p>', 'Published');
INSERT INTO articles (author_id, title, synopsis, fulltext, status) VALUES (3, 'Merative is awesome', 'A fresh take at Watson Health', '<p><span style="color:#8e44ad"><span style="font-family:Comic Sans MS,cursive"><span style="background-color:#ecf0f1">Merative</span></span></span>, formerly IBM Watson Health, is a standalone company as of 2022. Merative offers products and services that help clients facilitate medical research, clinical research, Real world evidence, and more.&nbsp;<a href="https://en.wikipedia.org/wiki/Merative">Wikipedia</a></p>', 'Draft');
INSERT INTO articles (author_id, title, synopsis, fulltext, status) VALUES (3, 'Test1', 'Test1', 'Test1', 'Rejected');
INSERT INTO articles (author_id, title, synopsis, fulltext, status) VALUES (3, 'Test2', 'Test2', 'Test1', 'Approved');
INSERT INTO articles (author_id, title, synopsis, fulltext, status) VALUES (3, 'Test3', 'Test3', 'Test1', 'ReadyForReview');
INSERT INTO articles (author_id, title, synopsis, fulltext, status) VALUES (4, 'Test4', 'Test4', 'Test1', 'Published');
INSERT INTO articles (author_id, title, synopsis, fulltext, status) VALUES (4, 'Test5', 'Test5', 'Test5', 'Approved');