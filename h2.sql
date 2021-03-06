DROP TABLE SOURCE;
CREATE TABLE SOURCE(ID INT PRIMARY KEY, NAME VARCHAR(20), URL VARCHAR(255));
INSERT INTO SOURCE VALUES(1, 'source', 'http://127.0.0.1:9000/sources');
INSERT INTO SOURCE VALUES(2, 'website', 'http://127.0.0.1:9000/websites');
INSERT INTO SOURCE VALUES(3, 'rule', 'http://127.0.0.1:9000/rules');
INSERT INTO SOURCE VALUES(4, 'article', 'http://127.0.0.1:9000/articles');

DROP TABLE RULE;
CREATE TABLE RULE(ID INT PRIMARY KEY, HOST VARCHAR(255),  URI VARCHAR(255), CONTENT VARCHAR(255), PRIORITY INT);
INSERT INTO RULE VALUES (1, '', '/index.html', 'index', 0);
INSERT INTO RULE VALUES (2, '', '/([a-z-_]*).html', '{0}', 1);
INSERT INTO RULE VALUES (3, '', '/([a-z-_]*)/([a-z-_]*).html', '{0}{1}', 2);

DROP TABLE AGGREGATIONRULE;
CREATE TABLE AGGREGATIONRULE(ID INT PRIMARY KEY, CONTENT VARCHAR(255),  SOURCE VARCHAR(255), TARGET VARCHAR(255));
INSERT INTO AGGREGATIONRULE VALUES (1, 'index', 'article', 'article');
INSERT INTO AGGREGATIONRULE VALUES (2, 'index', 'article', 'article2');
INSERT INTO AGGREGATIONRULE VALUES (3, 'blog', 'article', 'articles');


DROP TABLE ARTICLE;
CREATE TABLE ARTICLE(ID INT PRIMARY KEY, AUTHOR VARCHAR(255),  CONTENT VARCHAR(255));
INSERT INTO ARTICLE VALUES (1, 'jean-luc', 'this is an article');
INSERT INTO ARTICLE VALUES (2, 'frank', 'this is another article');

