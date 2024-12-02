drop table player;

create table player(
username varchar(30),
password varbinary(16),
wealth integer);

alter table player
	add constraint users_username_pk primary key(username);

