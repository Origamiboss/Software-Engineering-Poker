drop table users;

create table users(
username varchar(30),
password varbinary(16),
wealth integer);

alter table users
	add constraint users_username_pk primary key(username);

