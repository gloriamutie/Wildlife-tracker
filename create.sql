CREATE DATABASE wildlife_tracker;
\c wildlife_tracker;
CREATE TABLE animals(id serial PRIMARY KEY, name VARCHAR, type VARCHAR, health VARCHAR, age VARCHAR);
CREATE TABLE sightings (id serial PRIMARY KEY, location VARCHAR, rangerName VARCHAR, animalId INTEGER, createdAt TIMESTAMP);
CREATE DATABASE wildlife_tracker_test WITH TEMPLATE wildlife_tracker;