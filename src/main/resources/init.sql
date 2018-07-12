#This it not the final schema!
#This is for testing joining and constraints
#To hopefully implement an easy to use, bug free
#Method of tracking Who has What and When it's due.

create table item (
  i_barcode bigint not null auto_increment primary key,
  i_name varchar(20) not null,
  i_type varchar(20) not null,
  i_is_available boolean not null,
  i_is_late boolean null,
  i_notes varchar(255) null,
  i_check_out_date datetime null,
  i_due_date datetime null
  #create index idx_barcode (barcode)
);
  
create table patron (
    p_id bigint not null primary key,
    p_name varchar(30) not null,
    p_email varchar(20) not null,
    p_owes_fines boolean not null,
    p_fines_owed int null
    #create index idx_trinity_id (trinity_id)
);
   
#create primary key as combination of patron id/item id
create table checked_out_items (
  ref_id bigint primary key auto_increment not null,
  patron_id bigint not null,
  item_id bigint not null,
  item_available boolean not null,
  item_check_out_date datetime null,
  item_due_date datetime null);
  
alter table checked_out_items
add constraint fk_patron_id
foreign key (patron_id) references patron(p_id),
add constraint fk_item_id
foreign key (item_id) references item(i_barcode)
#add constraint fk_item_available
#add constraint fk_check_out_date
#add constraint fk_due_date
#foreign key (item_available references item(i_is_available)
#foreign key (item_check_out_date) references item(i_check_out_date)
#foreign key (item_due_date) references item(i_due_date)
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


#checked out items history table -> copy checked_out_items, combine against patron ID and index


