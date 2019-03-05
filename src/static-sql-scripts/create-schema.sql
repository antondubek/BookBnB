-- use this DB
use dag8_RickDB;

-- create tables
CREATE TABLE Users ( 
	id INT NOT NULL AUTO_INCREMENT, 
	name VARCHAR(100) NOT NULL, 
	email VARCHAR(100) NOT NULL, 
	city VARCHAR(100) NOT NULL, 
	PRIMARY KEY (id) 
	);
	
CREATE TABLE Users_password (
	Users_id INT NOT NULL,
	password VARCHAR(100) NOT NULL,
	PRIMARY KEY (Users_id, password),
	FOREIGN KEY (Users_id) REFERENCES Users (id) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE
	);

CREATE TABLE Users_friends (
	Users_id INT NOT NULL,
	friend INT NOT NULL,
	PRIMARY KEY (Users_id, friend),
	FOREIGN KEY (Users_id) REFERENCES Users (id) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE,
	FOREIGN KEY (friend) REFERENCES Users (id) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE
	);

CREATE TABLE Users_block (
	Users_id INT NOT NULL,
	block_user INT NOT NULL,
	PRIMARY KEY (Users_id, block_user),
	FOREIGN KEY (Users_id) REFERENCES Users (id) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE,
	FOREIGN KEY (block_user) REFERENCES Users (id) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE
	);

CREATE TABLE Book ( 
	ISBN VARCHAR(13) NOT NULL, 
	name VARCHAR(100) NOT NULL, 
	author VARCHAR(100) NOT NULL, 
	book_version VARCHAR(100) NOT NULL, 
	PRIMARY KEY (ISBN) 
	);

CREATE TABLE Book_genre (
	Book_ISBN VARCHAR(13) NOT NULL,
	genre VARCHAR(100) NOT NULL,
	PRIMARY KEY (Book_ISBN, genre),
	FOREIGN KEY (Book_ISBN) REFERENCES Book (ISBN) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE
	);

CREATE TABLE Users_book (
	copy_id INT NOT NULL AUTO_INCREMENT,	
	Users_id INT NOT NULL,
	Book_ISBN VARCHAR(13) NOT NULL,
	available BOOL NOT NULL DEFAULT (FALSE),
	loan_to INT,
	loan_length INT,
	loan_start DATE,
	loan_end DATE,
	PRIMARY KEY (copy_id, Users_id, Book_ISBN),
	FOREIGN KEY (Users_id) REFERENCES Users (id) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE,
	FOREIGN KEY (Book_ISBN) REFERENCES Book (ISBN) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE,
	FOREIGN KEY (loan_to) REFERENCES Users (id) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE
	);

CREATE TABLE Users_rating (
	Users_id INT NOT NULL,
	Book_ISBN VARCHAR(13) NOT NULL,
	rating INT NOT NULL, 
	review VARCHAR(500) NOT NULL,
	review_date DATE NOT NULL,
	PRIMARY KEY (Users_id, Book_ISBN),
	FOREIGN KEY (Users_id) REFERENCES Users (id) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE,
	FOREIGN KEY (Book_ISBN) REFERENCES Book (ISBN) 
	ON UPDATE CASCADE 
	ON DELETE CASCADE
	);

ALTER TABLE Users_rating ADD CONSTRAINT one CHECK (rating > 0);
ALTER TABLE Users_rating ADD CONSTRAINT five CHECK (rating < 6);

Alter TABLE Users_block RENAME TO Block;


-- Granting Privileges
-- Edwin's username is 'eb'

-- Anthony
grant select, update, delete, insert, alter on dag8_RickDB.Users TO 'acm35'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_password TO 'acm35'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_friends TO 'acm35'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Block TO 'acm35'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Book TO 'acm35'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Book_genre TO 'acm35'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_book TO 'acm35'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_rating TO 'acm35'@'%';

-- Riad
grant select, update, delete, insert, alter on dag8_RickDB.Users TO 'ri31'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_password TO 'ri31'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_friends TO 'ri31'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Block TO 'ri31'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Book TO 'ri31'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Book_genre TO 'ri31'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_book TO 'ri31'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_rating TO 'ri31'@'%';

-- Evaristo
grant select, update, delete, insert, alter on dag8_RickDB.Users TO 'er205'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_password TO 'er205'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_friends TO 'er205'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Block TO 'er205'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Book TO 'er205'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Book_genre TO 'er205'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_book TO 'er205'@'%';
grant select, update, delete, insert, alter on dag8_RickDB.Users_rating TO 'er205'@'%';

-- Adding Sample Data

-- Create accounts
INSERT INTO Users VALUES (1, 'Garnizzle', 'garnizzle@garnizzle.com', 'St. Andrews');
INSERT INTO Users VALUES (2, 'Rick Sanchez', 'rick@pickle.com', 'Hull');
INSERT INTO Users VALUES (3, 'Morty Smith', 'morty@thechosenmorty.com', 'Hull');

-- add passwords
INSERT INTO Users_password VALUES
	(1, 'garnizzle'),
	(2, 'rick'),
	(3, 'andmorty');

-- add friends
INSERT INTO Users_friends VALUES
	(1, 2),
	(1, 3),
	(2, 3);

-- add books
INSERT INTO Book VALUES
	('9781781102459', 'Harry Potter and the Philosopher\'s Stone', 'J. K. Rowling', '1st'),
	('9788129702227', 'Software Architecture in Practice', 'Len Bass, Paul Clements, Rick Kazman', '2nd'),
	('9788550800370', 'Italian For Dummies', 'Francesc Romana Onofri, Karen Antje Moller', '5th');

-- assign books to owners
INSERT INTO Users_book VALUES (1, 1, '9781781102459', TRUE, NULL, NULL, NULL, NULL);
INSERT INTO Users_book VALUES(2, 2, '9788129702227', FALSE, NULL, NULL, NULL, NULL);
INSERT INTO Users_book VALUES
	(3, 3, '9788550800370', FALSE, 2, 7, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY));

-- assign genres
INSERT INTO Book_genre VALUES
	('9781781102459', 'Fantasy'),
	('9788129702227', 'Computers'),
	('9788550800370', 'Foreign Languages');

-- add ratings & reviews
INSERT INTO Users_rating VALUES
	(1, '9781781102459', 5, 'This book is amazing, it changed my life', CURRENT_DATE),
	(2, '9788129702227', 2, 'This book is extremely boring, I do not recommend', '2019-01-10'),
	(3, '9788550800370', 3, 'I like this book, but I enjoyed learning Spanish more than learning Italian. Author is knowledgable.', CURRENT_DATE);
	


	




 








