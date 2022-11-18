CREATE DATABASE IF NOT EXISTS  netflixpp;
Use netflixpp;

Create table videos(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL,
    path VARCHAR(160) NOT NULL UNIQUE,
    thumbnail VARCHAR(160) NOT NULL UNIQUE
);

Create table users(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL UNIQUE,
    pass VARCHAR(160) NOT NULL UNIQUE
);

Create table uploads(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    videoId INT NOT NULL,
    userId INT NOT NULL,
    Foreign Key (videoId) references videos(id),
    Foreign Key (userId) references users(id)
);
