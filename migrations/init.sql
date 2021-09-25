CREATE TABLE IF NOT EXISTS cloaca
( 
    id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title varchar(255),
    author varchar(100),
    ip varchar(20),
    date datetime,
    text text
);

CREATE TABLE IF NOT EXISTS speluncae
( 
    id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    url varchar(255),
    author varchar(100),
    ip varchar(20),
    date datetime,
    note text,
    visning datetime NULL,
    visited boolean
);

CREATE TABLE IF NOT EXISTS metrics_in
(
    date datetime,
    temperature varchar(20),
    humidity varchar(20)
);
