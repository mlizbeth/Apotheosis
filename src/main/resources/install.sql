create schema apotheosis;
create table items(item_id bigint(20) not null auto_increment primary key, item_code bigint(20) not null, item_name varchar(64) not null, item_type varchar(64) not null);
#dont need item_quantity in final DB
create table shifts(id bigint(20) not null auto_increment primary key, shift_start datetime not null, shift_end datetime not null, shift_position varchar(10) not null, shift_person varchar(20) not null);
#do we need a primary key? Can we use a Person object instead
#Patron table
#Users table
