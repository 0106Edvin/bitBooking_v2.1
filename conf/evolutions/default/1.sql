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

create table app_user (
  id                        integer auto_increment not null,
  firstname                 varchar(255),
  lastname                  varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  phone_number              varchar(255),
  user_type_id              integer,
  constraint uq_app_user_email unique (email),
  constraint pk_app_user primary key (id))
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
  constraint pk_hotel primary key (id))
;


create table hotel_feature (
  hotel_id                       integer not null,
  feature_id                     integer not null,
  constraint pk_hotel_feature primary key (hotel_id, feature_id))
;



alter table hotel_feature add constraint fk_hotel_feature_hotel_01 foreign key (hotel_id) references hotel (id) on delete restrict on update restrict;

alter table hotel_feature add constraint fk_hotel_feature_feature_02 foreign key (feature_id) references feature (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table app_user;

drop table feature;

drop table hotel_feature;

drop table hotel;

SET FOREIGN_KEY_CHECKS=1;

