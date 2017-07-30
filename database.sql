DROP DATABASE if exists ajwheele_hw4;

CREATE DATABASE ajwheele_hw4;

USE ajwheele_hw4;

CREATE TABLE Users(
	username VARCHAR(12) PRIMARY KEY,
    pass VARCHAR(12)
);

CREATE TABLE Collaborators(
	collabID int auto_increment PRIMARY KEY,
	username VARCHAR(12),
    fileName VarCHAR(50),
    collabName VARCHAR(12)
);