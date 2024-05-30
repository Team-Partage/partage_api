-- TB_USER_ROLE 테이블 DML
insert into tb_user_role(role_id, role_name) values('R0000', 'ROLE_SUPER_ADMIN');
insert into tb_user_role(role_id, role_name) values('R0100', 'ROLE_ADMIN');
insert into tb_user_role(role_id, role_name) values('R0200', 'ROLE_USER');


-- TB_CHANNEL_ROLE 테이블 DML
insert into tb_channel_role(role_id, role_name) values('C0000', 'ROLE_OWNER');
insert into tb_channel_role(role_id, role_name) values('C0100', 'ROLE_MODERATOR');
insert into tb_channel_role(role_id, role_name) values('C0200', 'ROLE_VIEWER');
