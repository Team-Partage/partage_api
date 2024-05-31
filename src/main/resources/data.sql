-- TB_USER_ROLE 테이블 DML
insert into tb_user_role(role_id, role_name) values('R0000', 'ROLE_SUPER_ADMIN');
insert into tb_user_role(role_id, role_name) values('R0100', 'ROLE_ADMIN');
insert into tb_user_role(role_id, role_name) values('R0200', 'ROLE_USER');


-- TB_CHANNEL_ROLE 테이블 DML
insert into tb_channel_role(role_id, role_name) values('C0000', 'ROLE_OWNER');
insert into tb_channel_role(role_id, role_name) values('C0100', 'ROLE_MODERATOR');
insert into tb_channel_role(role_id, role_name) values('C0200', 'ROLE_VIEWER');

-- alter column charset utf8mb4
ALTER TABLE tb_playlist MODIFY title VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE tb_channel MODIFY name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE tb_user MODIFY nickname VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;