-------------------------建立库脚本
--删除数据库
drop database if exists onlineexam;
--创建数据库
create database if not exists onlineexam default character set utf8;
--切换数据库
use onlineexam;

-------------------------创建表脚本

--创建学生表
create table student(
   	stu_id int not null auto_increment,
    sno varchar(255),
    name varchar(255),
    password varchar(255),
    className varchar(255),
    humanId varchar(255),
    email varchar(255),
    address varchar(255),
    phone varchar(255),
    primary key (stu_id)
);

--创建考试报告表
create table examreport(
    id int not null auto_increment,
    score int,
    term varchar(255),
    stid int,
    primary key (id)
);

--创建考试表
create table exampaper(
    id int not null auto_increment,
    answer varchar(255),
    qid int,
    erid int,
    primary key (id)
);
    
--创建考试题目表
create table table_question(
    id int not null auto_increment,
    context varchar(255),
    answer1 varchar(255),
    answer2 varchar(255),
    answer3 varchar(255),
    answer4 varchar(255),
    answer varchar(255),
    primary key (id)
);  

