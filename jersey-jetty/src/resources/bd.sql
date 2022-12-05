CREATE DATABASE IF NOT EXISTS netflixpp;
Use netflixpp;

Create table user(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    password VARCHAR(80) NOT NULL,
    fullname VARCHAR(120)
);

Create table movies (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR (80) NOT NULL,
    path VARCHAR (225) NOT NULL UNIQUE,
    poster VARCHAR (225) NOT NULL UNIQUE,
    year INT,
    director VARCHAR(80),
    totaltime time,
    genre VARCHAR(20),
    --link para bucket
    uploadedBy  VARCHAR(80) NOT NULL,
    Foreign Key (uploadedBy) references user(username)
);

--Create table uploads (
  --  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    --movieID INT NOT NULL,
     --mudei chave para nome do utilizador
    --Foreign Key (movieID) references movies(id),
--);

 --insert in to movies