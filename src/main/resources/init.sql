#This it not the final schema!
#This is for testing joining and constraints
#To hopefully implement an easy to use, bug free
#Method of tracking Who has What and When it's due.

create table item (
  barcode bigint not null auto_increment primary key,
  name varchar(20) not null,
  type varchar(20) not null,
  is_available boolean not null,
  is_late boolean null,
  notes varchar(255) null,
  check_out_date datetime null,
  due_date datetime null
  #create index idx_barcode (barcode)
);
  
create table patron (
    trinity_id bigint not null primary key,
    name varchar(30) not null,
    email varchar(20) not null,
    owes_fines boolean not null,
    fines_owed int null
    #create index idx_trinity_id (trinity_id)
);
   
create table checked_out_items (
  ref_id bigint primary key auto_increment not null,
  patron_id bigint not null,
  item_id bigint not null,
  item_available boolean not null,
  item_check_out_date datetime null,
  item_due_date datetime null);
  
alter table checked_out_items
add constraint fk_patron_id
foreign key (patron_id) references patron(trinity_id),
add constraint fk_item_id
foreign key (item_id) references item(barcode)
#add constraint fk_item_available
#add constraint fk_check_out_date
#add constraint fk_due_date
#foreign key (item_available references item(is_available)
#foreign key (item_check_out_date) references item(check_out_date)
#foreign key (item_due_date) references item(due_date)
on update cascade
on delete cascade;     
    
    
insert into patron values(0834184,'Madeline Kotara','mkotara@trinity.edu',0,null);
insert into item values(1,'Chromebook','Laptop',0,null,null,null,null);
insert into item values(2,'Dell Latitude','Laptop',0,null,null,null,null);
insert into item values(3,'Optiplex','AIO',0,null,null,null,null);
insert into item values(4,'iMac','Desktop',0,null,null,null,null);

insert into checked_out_items(patron_id,item_id,item_available,item_check_out_date,item_due_date)
select patron.trinity_id,item.barcode,item.is_available,item.check_out_date,item.due_date
from patron
inner join item;

select * from item;
select * from patron;
select * from checked_out_items;
