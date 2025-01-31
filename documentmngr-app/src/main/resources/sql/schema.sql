create table users (
  user_id bigint not null,
  user_name_tx varchar(30),
  user_desc_tx varchar(30),
  user_curr_cd varchar(3),
  user_imag_tx varchar(40)
);

create table categories (
  cate_id bigint not null,
  cate_name_tx varchar(30),
  cate_type_tx varchar(30)
);

create table items (
  item_id bigint not null,
  item_name_tx varchar(40),
  cate_id bigint
);