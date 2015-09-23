# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table app_user (
  id                        integer auto_increment not null,
  firstname                 varchar(255),
  lastname                  varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  phone_number              varchar(255),
  user_access_level         integer,
  constraint uq_app_user_email unique (email),
  constraint pk_app_user primary key (id))
;

create table comment (
  id                        integer auto_increment not null,
  user_id                   integer,
  hotel_id                  integer,
  title                     varchar(255),
  content                   varchar(255),
  rating                    integer,
  constraint pk_comment primary key (id))
;

create table feature (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_feature primary key (id))
;

create table hotel (
  id                        integer auto_increment not null,
  name                      varchar(255),
  location                  varchar(255),
  description               varchar(255),
  city                      varchar(255),
  country                   varchar(255),
  coordinate_x              varchar(255),
  coordinate_y              varchar(255),
  seller_id                 integer,
  constraint pk_hotel primary key (id))
;

create table image (
  id                        integer auto_increment not null,
  public_id                 varchar(255),
  image_url                 varchar(255),
  secret_image_url          varchar(255),
  hotel_id                  integer,
  user_id                   integer,
  feature_id                integer,
  constraint uq_image_user_id unique (user_id),
  constraint uq_image_feature_id unique (feature_id),
  constraint pk_image primary key (id))
;

create table price (
  id                        integer auto_increment not null,
  check_in                  datetime,
  check_out                 datetime,
  cost                      decimal(38),
  room_id                   integer,
  constraint pk_price primary key (id))
;

create table room (
  id                        integer auto_increment not null,
  description               varchar(255),
  number_of_beds            integer,
  name                      varchar(255),
  hotel_id                  integer,
  constraint pk_room primary key (id))
;


create table hotel_feature (
  hotel_id                       integer not null,
  feature_id                     integer not null,
  constraint pk_hotel_feature primary key (hotel_id, feature_id))
;

create table room_feature (
  room_id                        integer not null,
  feature_id                     integer not null,
  constraint pk_room_feature primary key (room_id, feature_id))
;
alter table comment add constraint fk_comment_user_1 foreign key (user_id) references app_user (id) on delete restrict on update restrict;
create index ix_comment_user_1 on comment (user_id);
alter table comment add constraint fk_comment_hotel_2 foreign key (hotel_id) references hotel (id) on delete restrict on update restrict;
create index ix_comment_hotel_2 on comment (hotel_id);
alter table image add constraint fk_image_hotel_3 foreign key (hotel_id) references hotel (id) on delete restrict on update restrict;
create index ix_image_hotel_3 on image (hotel_id);
alter table image add constraint fk_image_user_4 foreign key (user_id) references app_user (id) on delete restrict on update restrict;
create index ix_image_user_4 on image (user_id);
alter table image add constraint fk_image_feature_5 foreign key (feature_id) references feature (id) on delete restrict on update restrict;
create index ix_image_feature_5 on image (feature_id);
alter table price add constraint fk_price_room_6 foreign key (room_id) references room (id) on delete restrict on update restrict;
create index ix_price_room_6 on price (room_id);
alter table room add constraint fk_room_hotel_7 foreign key (hotel_id) references hotel (id) on delete restrict on update restrict;
create index ix_room_hotel_7 on room (hotel_id);



alter table hotel_feature add constraint fk_hotel_feature_hotel_01 foreign key (hotel_id) references hotel (id) on delete restrict on update restrict;

alter table hotel_feature add constraint fk_hotel_feature_feature_02 foreign key (feature_id) references feature (id) on delete restrict on update restrict;

alter table room_feature add constraint fk_room_feature_room_01 foreign key (room_id) references room (id) on delete restrict on update restrict;

alter table room_feature add constraint fk_room_feature_feature_02 foreign key (feature_id) references feature (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table app_user;

drop table comment;

drop table feature;

drop table hotel_feature;

drop table hotel;

drop table image;

drop table price;

drop table room;

drop table room_feature;

SET FOREIGN_KEY_CHECKS=1;

