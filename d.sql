CREATE DATABASE IF NOT EXISTS netflixpp;
Use netflixpp;

Create table user(
    id INT NOT NULL PRIMARY KEY AUTOINCREMENT, 
    username VARCHAR(80) NOT NULL UNIQUE, 
    password VARCHAR(80) NOT NULL UNIQUE, 
    fullname VARCHAR(120)
);

Create table movies (
    id INT NOT NULL PRIMARY KEY AUTOINCREMENT, 
    name VARCHAR (80) NOT NULL UNIQUE, 
    path VARCHAR (225) NOT NULL UNIQUE, 
    poster VARCHAR (225) NOT NULL UNIQUE, 
    year INT,
    director VARCHAR(80), 
    totaltime time NOT NULL, 
    genre VARCHAR(20), 
    publisher VARCHAR(80)
);

Create table uploads (
    id INT NOT NULL PRIMARY KEY AUTOINCREMENT, 
    movieID INT NOT NULL, 
    userID INT NOT NULL,
    ForeignKey (movieID) references movies (id), 
    ForeignKey (userID) references users(id),
);

/* add movie material but only once they are all*/