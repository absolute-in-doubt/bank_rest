CREATE DATABASE bank_rest;

\c bank_rest;

CREATE SEQUENCE user_sequence START 1 INCREMENT 20;
CREATE SEQUENCE card_sequence START 1 INCREMENT 20;

CREATE TABLE "users" (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(25),
    last_name VARCHAR(25),
    username VARCHAR(35),
    password CHAR(68),
    status VARCHAR(7),
    roles JSONB,
    version int
);

CREATE TABLE "cards" (
    id BIGINT PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    cardNumber CHAR(16) UNIQUE,
    status VARCHAR(7),
    balance DECIMAL(15,4),
    version INT
)