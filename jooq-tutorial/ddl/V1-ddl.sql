CREATE SCHEMA IF NOT EXISTS example;
ALTER SCHEMA example OWNER TO postgres;

create table if not exists example.author (
  id int primary key ,
  first_name varchar(80) not null ,
  last_name varchar(80) not null
);
