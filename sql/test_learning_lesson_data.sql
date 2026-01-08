-- 学习课表测试数据
-- 创建课程表
CREATE TABLE IF NOT EXISTS tb_course (
    id bigint auto_increment comment '主键' primary key,
    name varchar(255) not null comment '课程名称',
    cover_url varchar(500) comment '课程封面',
    total_sections int default 0 comment '课程总小节数',
    status tinyint(1) default 1 comment '课程状态（0-下架，1-上架）',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '课程表';

-- 创建课程小节表
CREATE TABLE IF NOT EXISTS tb_course_section (
    id bigint auto_increment comment '主键' primary key,
    name varchar(255) not null comment '小节名称',
    course_id bigint not null comment '课程ID',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '课程小节表';

-- 创建学习课表
CREATE TABLE IF NOT EXISTS tb_learning_lesson (
    id bigint auto_increment comment '主键' primary key,
    user_id bigint not null comment '学员id',
    course_id bigint not null comment '课程id',
    status tinyint(1) default 0 comment '课程状态（0-未学习，1-学习中，2-已学完，3-已失效）',
    week_freq tinyint(1) comment '每周学习频率，每周3天，每天2节，则频率为6',
    plan_status tinyint(1) default 0 comment '学习计划状态（0-没有计划，1-计划进行中）',
    learned_sections int default 0 comment '已学习小节数量',
    latest_section_id bigint comment '最近一次学习的小节id',
    latest_learn_time datetime comment '最近一次学习的时间',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    expire_time datetime not null comment '过期时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint idx_user_id_course_id unique (user_id, course_id)
) comment '学生课表';

-- 插入测试课程数据
INSERT INTO tb_course (id, name, cover_url, total_sections, status, create_time, update_time) VALUES
(1, 'Java基础教程', 'https://example.com/java-cover.jpg', 20, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Spring Boot实战', 'https://example.com/spring-cover.jpg', 15, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'MySQL数据库设计', 'https://example.com/mysql-cover.jpg', 10, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试课程小节数据
INSERT INTO tb_course_section (id, name, course_id, create_time, update_time) VALUES
(1, 'Java环境搭建', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Java语法基础', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Spring Boot入门', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Spring Boot配置', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'MySQL安装', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入学习课表测试数据
INSERT INTO tb_learning_lesson (user_id, course_id, status, week_freq, plan_status, learned_sections, latest_section_id, latest_learn_time, expire_time, create_time, update_time) VALUES
(1, 1, 0, 0, 0, 1, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 1, 6, 5, 2, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 2, 1, 3, 8, 3, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 2, 2, 0, 15, 4, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 3, 3, 0, 10, 5, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 YEAR), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);