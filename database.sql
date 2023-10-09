create database if not exists hdscode_spring;

use hdscode_spring;

drop table users;
create table users
(
    username varchar(100) not null,
    password varchar(100) not null,
    name varchar(100) not null,
    token varchar(100),
    token_expired_At bigint,
    primary key(username),
    UNIQUE (token)
) ENGINE InnoDB;

select * from users;

describe users;

drop table contacts;
create table contacts
(
    id varchar(100) not null,
    username varchar(100) not null,
    firstname varchar(100) not null,
    lastname varchar(100),
    phone varchar(100),
    email varchar(100),
    primary key(id),
    foreign key fk_users_contacts(username) references users(username)
)ENGINE InnoDb;

select * from contacts;

desc contacts;

drop table addresses;
create table addresses
(
    id varchar(100) not null,
    contact_id varchar(100) not null,
    street varchar(100),
    city varchar(100),
    country varchar(100) not null,
    province varchar(100),
    postal_code varchar(10),
    primary key(id),
    foreign key fk_contact_adress(contact_id) references contacts(id)
)ENGINE InnoDB;

select * from addresses;

select * from users;
