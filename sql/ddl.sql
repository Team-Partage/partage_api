-- TB_USER: 사용자 테이블
create table if not exists `tb_user` (
    `user_no` bigint not null auto_increment,
    `email` varchar(100) not null unique,
    `password` varchar(255) not null,
    `username` varchar(50) not null,
    `nickname` varchar(50) not null unique,
    `profile_color` varchar(20) null,
    `profile_image` varchar(255) null,
    `create_at` timestamp not null default current_timestamp,
    `update_at` timestamp not null default current_timestamp on update current_timestamp,
    `is_active` bit not null default 1,
    primary key (`user_no`),
    index `idx_user_email` (`email`),
    index `idx_user_nickname` (`nickname`)
) engine = innodb;

-- TB_USER_ROLE: 사용자 권한 테이블
create table if not exists `tb_user_role` (
    `role_id` char(5) not null,
    `role_name` varchar(20) not null,
    primary key (`role_id`)
) engine = innodb;

-- TB_USER_ROLE_MAPPING: 사용자 권한 매핑 테이블
create table if not exists `tb_user_role_mapping` (
    `role_id` char(5) not null,
    `user_no` bigint not null,
    primary key (`role_id`, `user_no`)
) engine = innodb;

-- TB_FOLLOW: 팔로우 테이블
create table if not exists `tb_follow` (
    `follow_no` bigint not null auto_increment,
    `from_user` bigint not null,
    `to_user` bigint not null,
    `create_at` timestamp not null default current_timestamp,
    `is_active` bit not null default 1,
    primary key (`follow_no`)
) engine = innodb;

-- TB_CHANNEL: 채널 테이블
create table if not exists `tb_channel` (
    `channel_no` bigint not null auto_increment,
    `name` varchar(255) not null,
    `type` enum('PUBLIC', 'PRIVATE') not null,
    `hashtag` varchar(255) not null,
    `channel_url` varchar(255) not null,
    `channel_color` varchar(20) null,
    `channel_image` varchar(255) null,
    `create_at` timestamp not null default current_timestamp,
    `update_at` timestamp not null default current_timestamp on update current_timestamp,
    `is_active` bit not null default 1,
    primary key (`channel_no`)
) engine = innodb;

-- TB_CHANNEL_ROLE: 채널 권한 테이블
create table if not exists `tb_channel_role` (
    `role_id` char(5) not null,
    `role_name` varchar(20) not null,
    primary key (`role_id`)
) engine = innodb;

-- TB_CHANNEL_ROLE_MAPPING: 채널 권한 매핑 테이블
create table if not exists `tb_channel_role_mapping` (
    `role_id` char(5) not null,
    `channel_no` bigint not null,
    `user_no` bigint not null,
    primary key (
        `role_id`,
        `channel_no`,
        `user_no`
    )
) engine = innodb;

-- TB_CHATTING: 채팅 테이블
create table if not exists `tb_chatting` (
    `chatting_no` bigint not null auto_increment,
    `channel_no` bigint not null,
    `user_no` bigint not null,
    `message` varchar(1000) not null,
    `create_at` timestamp not null default current_timestamp,
    `is_active` bit not null default 1,
    primary key (`chatting_no`)
) engine = innodb;

-- TB_CHANNEL_BAN_LIST: 채널 차단 리스트 테이블
create table if not exists `tb_channel_ban_list` (
    `ban_no` bigint not null auto_increment,
    `channel_no` bigint not null,
    `banned_user_no` bigint not null,
    `banned_by_user_no` bigint not null,
    `create_at` timestamp not null default current_timestamp,
    `is_active` bit not null default 1,
    primary key (`ban_no`)
) engine = innodb;

-- TB_PLAYLIST: 플레이리스트 테이블
create table if not exists `tb_playlist` (
    `playlist_no` bigint not null auto_increment,
    `channel_no` bigint not null,
    `sequence` int not null,
    `name` varchar(255) not null,
    `url` varchar(255) not null,
    `create_at` timestamp not null default current_timestamp,
    `is_active` bit not null default 1,
    primary key (`playlist_no`)
) engine = innodb;

-- TB_USER_ROLE_MAPPING 테이블 제약조건
alter table `tb_user_role_mapping`
add constraint `FK_tb_user_TO_tb_user_role_mapping` foreign key (`user_no`) references `tb_user` (`user_no`) on delete cascade;

alter table `tb_user_role_mapping`
add constraint `FK_tb_user_role_TO_tb_user_role_mapping` foreign key (`role_id`) references `tb_user_role` (`role_id`);

-- TB_FOLLOW 테이블 제약조건
alter table `tb_follow`
add constraint `FK_tb_user_TO_tb_follow_from` foreign key (`from_user`) references `tb_user` (`user_no`) on delete cascade;

alter table `tb_follow`
add constraint `FK_tb_user_TO_tb_follow_to` foreign key (`to_user`) references `tb_user` (`user_no`) on delete cascade;

-- TB_CHANNEL_ROLE_MAPPING 테이블 제약조건
alter table `tb_channel_role_mapping`
add constraint `FK_tb_channel_role_TO_tb_channel_role_mapping` foreign key (`role_id`) references `tb_channel_role` (`role_id`);

alter table `tb_channel_role_mapping`
add constraint `FK_tb_channel_TO_tb_channel_role_mapping` foreign key (`channel_no`) references `tb_channel` (`channel_no`);

alter table `tb_channel_role_mapping`
add constraint `FK_tb_user_TO_tb_channel_role_mapping` foreign key (`user_no`) references `tb_user` (`user_no`);

-- TB_CHATTING 테이블 제약조건
alter table `tb_chatting`
add constraint `FK_tb_channel_TO_tb_chatting` foreign key (`channel_no`) references `tb_channel` (`channel_no`);

alter table `tb_chatting`
add constraint `FK_tb_user_TO_tb_chatting` foreign key (`user_no`) references `tb_user` (`user_no`);

-- TB_CHANNEL_BAN_LIST 테이블 제약조건
alter table `tb_channel_ban_list`
add constraint `FK_tb_channel_TO_tb_channel_ban_list` foreign key (`channel_no`) references `tb_channel` (`channel_no`);

alter table `tb_channel_ban_list`
add constraint `FK_tb_user_TO_tb_channel_ban_list_banned` foreign key (`banned_user_no`) references `tb_user` (`user_no`);

alter table `tb_channel_ban_list`
add constraint `FK_tb_user_TO_tb_channel_ban_list_banned_by` foreign key (`banned_by_user_no`) references `tb_user` (`user_no`);

-- TB_PLAYLIST 테이블 제약조건
alter table `tb_playlist`
add constraint `FK_tb_channel_TO_tb_playlist` foreign key (`channel_no`) references `tb_channel` (`channel_no`);