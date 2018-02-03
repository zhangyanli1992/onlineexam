-------------------------������ű�
--ɾ�����ݿ�
drop database if exists onlineexam;
--�������ݿ�
create database if not exists onlineexam default character set utf8;
--�л����ݿ�
use onlineexam;

-------------------------������ű�

--����ѧ����
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

--�������Ա����
create table examreport(
    id int not null auto_increment,
    score int,
    term varchar(255),
    stid int,
    primary key (id)
);

--�������Ա�
create table exampaper(
    id int not null auto_increment,
    answer varchar(255),
    qid int,
    erid int,
    primary key (id)
);
    
--����������Ŀ��
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

