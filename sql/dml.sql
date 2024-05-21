-- TB_USER_ROLE 테이블 DML
insert into tb_user_role(role_id, role_name) values('R0000', 'SUPER_ADMIN');
insert into tb_user_role(role_id, role_name) values('R0100', 'ADMIN');
insert into tb_user_role(role_id, role_name) values('R0200', 'USER');


-- TB_CHANNEL_ROLE 테이블 DML
insert into tb_channel_role(role_id, role_name) values('C0100', 'ROLE_VIEWER');
insert into tb_channel_role(role_id, role_name) values('C0200', 'ROLE_MODERATOR');
insert into tb_channel_role(role_id, role_name) values('C0300', 'ROLE_OWNER');


-- TB_CHANNEL_PERMISSION 테이블 DML
insert into tb_channel_permission(permission_id, permission_name) values('C0101', 'VIEWER_ADD');
insert into tb_channel_permission(permission_id, permission_name) values('C0102', 'VIEWER_REMOVE');
insert into tb_channel_permission(permission_id, permission_name) values('C0103', 'VIEWER_MOVE');
insert into tb_channel_permission(permission_id, permission_name) values('C0104', 'VIEWER_PLAYANDPAUSE');
insert into tb_channel_permission(permission_id, permission_name) values('C0105', 'VIEWER_SEEK');
insert into tb_channel_permission(permission_id, permission_name) values('C0106', 'VIEWER_SKIP');
insert into tb_channel_permission(permission_id, permission_name) values('C0107', 'VIEWER_CHATSEND');
insert into tb_channel_permission(permission_id, permission_name) values('C0108', 'VIEWER_CHATDELETE');
insert into tb_channel_permission(permission_id, permission_name) values('C0109', 'VIEWER_BAN');
insert into tb_channel_permission(permission_id, permission_name) values('C0201', 'MODERATOR_ADD');
insert into tb_channel_permission(permission_id, permission_name) values('C0202', 'MODERATOR_REMOVE');
insert into tb_channel_permission(permission_id, permission_name) values('C0203', 'MODERATOR_MOVE');
insert into tb_channel_permission(permission_id, permission_name) values('C0204', 'MODERATOR_PLAYANDPAUSE');
insert into tb_channel_permission(permission_id, permission_name) values('C0205', 'MODERATOR_SEEK');
insert into tb_channel_permission(permission_id, permission_name) values('C0206', 'MODERATOR_SKIP');
insert into tb_channel_permission(permission_id, permission_name) values('C0207', 'MODERATOR_CHATSEND');
insert into tb_channel_permission(permission_id, permission_name) values('C0208', 'MODERATOR_CHATDELETE');
insert into tb_channel_permission(permission_id, permission_name) values('C0209', 'MODERATOR_BAN');
insert into tb_channel_permission(permission_id, permission_name) values('C0301', 'OWNER_ADD');
insert into tb_channel_permission(permission_id, permission_name) values('C0302', 'OWNER_REMOVE');
insert into tb_channel_permission(permission_id, permission_name) values('C0303', 'OWNER_MOVE');
insert into tb_channel_permission(permission_id, permission_name) values('C0304', 'OWNER_PLAYANDPAUSE');
insert into tb_channel_permission(permission_id, permission_name) values('C0305', 'OWNER_SEEK');
insert into tb_channel_permission(permission_id, permission_name) values('C0306', 'OWNER_SKIP');
insert into tb_channel_permission(permission_id, permission_name) values('C0307', 'OWNER_CHATSEND');
insert into tb_channel_permission(permission_id, permission_name) values('C0308', 'OWNER_CHATDELETE');
insert into tb_channel_permission(permission_id, permission_name) values('C0309', 'OWNER_BAN');




