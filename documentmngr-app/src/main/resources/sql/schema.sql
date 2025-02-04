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

create table expenses (
  expe_id bigint not null,
  oper_dt date not null,
  user_id bigint not null,
  item_id bigint not null,
  expe_item_cn decimal(10,3) not null,
  expe_pric_am decimal(10,2) not null,
  expe_comm_tx varchar(255),
  crea_dt timestamp,
  updt_dt timestamp,
  vers_nm integer
);
create sequence expe_seq start with 1 increment by 1;
alter table expenses add constraint fk_expenses_user_id foreign key(user_id) references users (user_id);
alter table expenses add constraint fk_expenses_item_id foreign key(item_id) references items (item_id);

create table incomes (
  inco_id bigint not null,
  oper_dt date not null,
  user_id bigint not null,
  item_id bigint not null,
  inco_item_cn decimal(10,3) not null,
  inco_pric_am decimal(10,2) not null,
  inco_comm_tx varchar(255),
  crea_dt timestamp,
  updt_dt timestamp,
  vers_nm integer
);
create sequence inco_seq start with 1 increment by 1;
alter table incomes add constraint fk_incomes_user_id foreign key(user_id) references users (user_id);
alter table incomes add constraint fk_incomes_item_id foreign key(item_id) references items (item_id);

create table documents (
  docu_id bigint not null,
  docu_dt date not null,
  docu_type_tx varchar(10) not null,
  docu_name_tx varchar(30) not null,
  docu_desc_tx varchar(255),
  paym_meth_tx varchar(10) not null,
  invo_numb_tx varchar(30),
  cprt_id bigint,
  acco_id bigint not null,
  curr_cd varchar(3) not null,
  curr_exch_am decimal(10,6) not null,
  crea_dt timestamp,
  updt_dt timestamp,
  vers_nm integer
);
create unique index docu_pk on documents(docu_id);
alter table documents add constraint pk_documents primary key (docu_id);
alter table documents add constraint fk_documents_user_id foreign key(acco_id) references users (user_id);
create sequence docu_seq start with 1 increment by 1;

create table document_items (
  docu_id bigint not null,
  sequ_nm integer not null,
  docu_item_type_tx varchar(3) not null,
  expe_id bigint,
  inco_id bigint,
  crea_dt timestamp,
  updt_dt timestamp,
  vers_nm integer
);
create unique index docu_item_pk on document_items(docu_id, sequ_nm);
alter table document_items add constraint pk_document_items primary key (docu_id, sequ_nm);
alter table document_items add constraint fk_document_items_docu_id foreign key(docu_id) references documents (docu_id);
