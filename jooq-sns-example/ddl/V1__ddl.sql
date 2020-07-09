CREATE SCHEMA example;
ALTER SCHEMA example OWNER TO postgres;


CREATE TABLE example.users (
  user_id int primary key not null,
  user_key varchar (32) not null unique ,
  user_name varchar(80) not null
);

CREATE TABLE example.passwords (
  user_id int primary key not null ,
  password_hash varchar (512) not null ,
  constraint fk_user_id_users foreign key (user_id) references example.users(user_id)
);

CREATE TABLE example.emails (
  email_id int primary key not null ,
  email_value varchar(200) not null ,
  created timestamp not null
);

CREATE TABLE example.user_emails (
  email_id int primary key not null ,
  user_id int not null ,
  created timestamp  not null ,
  constraint fk_user_emails_email_id_emails foreign key (email_id) references example.emails(email_id),
  constraint fk_user_emails_user_id_users foreign key (user_id) references example.users(user_id)
);

CREATE TABLE example.temporary_users (
  temporary_hash_key varchar (200) primary key not null ,
  email_id int not null ,
  created timestamp not null ,
  constraint fk_temporary_users_email_id_emails foreign key (email_id) references example.emails(email_id)
);
