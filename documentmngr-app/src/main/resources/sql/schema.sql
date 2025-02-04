create table users (
  user_id bigint not null,
  user_name_tx varchar(30),
  user_desc_tx varchar(30),
  user_curr_cd varchar(3),
  user_imag_tx varchar(40)
);
create unique index pk_users on users(user_id);
alter table users add constraint pk_users primary key (user_id);

create table categories (
  cate_id bigint not null,
  cate_name_tx varchar(30),
  cate_type_tx varchar(30)
);
create unique index pk_categories on categories(cate_id);
alter table categories add constraint pk_categories primary key (cate_id);

create table items (
  item_id bigint not null,
  item_name_tx varchar(40),
  cate_id bigint
);
create unique index pk_items on items(item_id);
alter table items add constraint pk_items primary key (item_id);
alter table items add constraint fk_items_cate_id foreign key(cate_id) references categories (cate_id);
