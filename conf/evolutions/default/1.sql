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
  profile_img_id            integer,
  constraint uq_app_user_email unique (email),
  constraint uq_app_user_profile_img_id unique (profile_img_id),
  constraint pk_app_user primary key (id))
;

create table comment (
  id                        integer auto_increment not null,
  user_id                   integer,
  hotel_id                  integer,
  title                     varchar(255),
  content                   varchar(255),
  rating                    double,
  constraint pk_comment primary key (id))
;

create table feature (
  id                        integer auto_increment not null,
  name                      varchar(255),
  icon_id                   integer,
  constraint uq_feature_name unique (name),
  constraint uq_feature_icon_id unique (icon_id),
  constraint pk_feature primary key (id))
;

create table hotel (
  id                        integer auto_increment not null,
  name                      varchar(255),
  location                  varchar(255),
  description               TEXT,
  city                      varchar(255),
  country                   varchar(255),
  coordinate_x              varchar(255),
  coordinate_y              varchar(255),
  seller_id                 integer,
  rating                    double,
  constraint pk_hotel primary key (id))
;

create table image (
  id                        integer auto_increment not null,
  public_id                 varchar(255),
  image_url                 varchar(255),
  secret_image_url          varchar(255),
  hotel_id                  integer,
  room_id                   integer,
  feature_id                integer,
  constraint uq_image_feature_id unique (feature_id),
  constraint pk_image primary key (id))
;

create table price (
  id                        integer auto_increment not null,
  date_from                 datetime,
  date_to                   datetime,
  cost                      decimal(38),
  room_id                   integer,
  constraint pk_price primary key (id))
;

create table reservation (
  id                        integer auto_increment not null,
  cost                      decimal(38),
  check_in                  datetime,
  check_out                 datetime,
  status                    integer,
  room_id                   integer,
  user_id                   integer,
  constraint pk_reservation primary key (id))
;

create table room (
  id                        integer auto_increment not null,
  description               TEXT,
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
alter table app_user add constraint fk_app_user_profileImg_1 foreign key (profile_img_id) references image (id) on delete restrict on update restrict;
create index ix_app_user_profileImg_1 on app_user (profile_img_id);
alter table comment add constraint fk_comment_user_2 foreign key (user_id) references app_user (id) on delete restrict on update restrict;
create index ix_comment_user_2 on comment (user_id);
alter table comment add constraint fk_comment_hotel_3 foreign key (hotel_id) references hotel (id) on delete restrict on update restrict;
create index ix_comment_hotel_3 on comment (hotel_id);
alter table feature add constraint fk_feature_icon_4 foreign key (icon_id) references image (id) on delete restrict on update restrict;
create index ix_feature_icon_4 on feature (icon_id);
alter table image add constraint fk_image_hotel_5 foreign key (hotel_id) references hotel (id) on delete restrict on update restrict;
create index ix_image_hotel_5 on image (hotel_id);
alter table image add constraint fk_image_room_6 foreign key (room_id) references room (id) on delete restrict on update restrict;
create index ix_image_room_6 on image (room_id);
alter table image add constraint fk_image_feature_7 foreign key (feature_id) references feature (id) on delete restrict on update restrict;
create index ix_image_feature_7 on image (feature_id);
alter table price add constraint fk_price_room_8 foreign key (room_id) references room (id) on delete restrict on update restrict;
create index ix_price_room_8 on price (room_id);
alter table reservation add constraint fk_reservation_room_9 foreign key (room_id) references room (id) on delete restrict on update restrict;
create index ix_reservation_room_9 on reservation (room_id);
alter table reservation add constraint fk_reservation_user_10 foreign key (user_id) references app_user (id) on delete restrict on update restrict;
create index ix_reservation_user_10 on reservation (user_id);
alter table room add constraint fk_room_hotel_11 foreign key (hotel_id) references hotel (id) on delete restrict on update restrict;
create index ix_room_hotel_11 on room (hotel_id);



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

drop table reservation;

drop table room;

drop table room_feature;

SET FOREIGN_KEY_CHECKS=1;

