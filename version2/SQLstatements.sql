drop table if exists public.category cascade;
drop table if exists public.post_category cascade;
drop table if exists public.reply cascade;
drop table if exists public.liked cascade;
drop table if exists public.favored cascade;
drop table if exists public.shared cascade;
drop table if exists public.post cascade;
drop table if exists public.follow cascade;
drop table if exists public.account cascade;
drop table if exists public.block cascade;
drop table if exists public.searchcount cascade;
drop table if exists public.shielding cascade;
drop table if exists public.block cascade ;

-- Schema account
create table account
(
    name              varchar primary key,
    account_id        varchar,
    registration_time timestamp,
    phone             varchar,
    password          varchar
);

-- Schema categories
create table category
(
    category_name varchar primary key
);

-- Schema post
create table post
(
    post_id           bigint primary key,
    title             varchar   not null,
    content           varchar,
    datetime          timestamp not null,
    city              varchar,
    post_account_name varchar   not null,
    Anonymous         boolean
);

-- Schema post_category
create table post_category
(
    category_name varchar,
    post_id       bigint,
    primary key (category_name, post_id),
    constraint category_name_fk foreign key (category_name)
        references category (category_name),
    constraint post_id_fk foreign key (post_id)
        references post (post_id)
);

-- Schema reply
create table reply
(
    id                  bigserial primary key,
    reply_id            bigint, -- if this reply is not secondary reply then this field is null
    content             varchar,
    stars               bigint  not null,
    post_id             bigint  not null,
    author_account_name varchar not null,
    Anonymous           boolean,
    unique (reply_id, post_id, content), -- reduce replication
    constraint post_id_fk foreign key (post_id)
        references post (post_id),
    constraint author_account_name_fk foreign key (author_account_name)
        references account (name)
);

-- Schema liked
create table liked
(
    account_name varchar,
    post_id      bigint,
    primary key (account_name, post_id),
    constraint post_id_fk foreign key (post_id)
        references post (post_id),
    constraint account_name_fk foreign key (account_name)
        references account (name)
);

-- Schema favored
create table favored
(
    account_name varchar,
    post_id      bigint,
    primary key (account_name, post_id),
    constraint post_id_fk foreign key (post_id)
        references post (post_id),
    constraint account_name_fk foreign key (account_name)
        references account (name)
);

-- Schema shared
create table shared
(
    account_name varchar,
    post_id      bigint,
    primary key (account_name, post_id),
    constraint post_id_fk foreign key (post_id)
        references post (post_id),
    constraint account_name_fk foreign key (account_name)
        references account (name)
);

-- Schema follow
create table follow
(
    follower_name varchar,
    followee_name varchar,
    primary key (follower_name, followee_name),
    constraint follower_name_fk foreign key (follower_name)
        references account (name),
    constraint followee_name_fk foreign key (followee_name)
        references account (name)
);

--Schema block
create table block
(
    blocked_name varchar,
    blocker_name varchar,
    primary key (blocker_name, blocked_name),
    constraint follower_name_fk foreign key (blocker_name)
        references account (name),
    constraint followee_name_fk foreign key (blocked_name)
        references account (name)
);

--Schema block
create table Shielding
(
    shielded_name varchar,
    shielder_name varchar,
    primary key (shielder_name, shielded_name),
    constraint follower_name_fk foreign key (shielder_name)
        references account (name),
    constraint followee_name_fk foreign key (shielded_name)
        references account (name)
);

--Schema searchCount
create table searchCount
(
    numOfSearched bigint,
    post_id       bigint primary key ,
    constraint post_id_fk foreign key (post_id)
        references post (post_id)
);


--检测插入post_category的数据是否存在category表中的trigger

CREATE OR REPLACE FUNCTION insert_category_if_not_exists()
RETURNS TRIGGER AS $$
BEGIN
    -- 检查是否存在对应的 category
    IF NOT EXISTS (SELECT 1 FROM category WHERE category_name = NEW.category_name) THEN
        -- 在 category 表中插入新的 category 信息
        INSERT INTO category (category_name) VALUES (NEW.category_name);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器
CREATE TRIGGER insert_category_trigger
BEFORE INSERT ON post_category
FOR EACH ROW
EXECUTE FUNCTION insert_category_if_not_exists();

CREATE OR REPLACE FUNCTION set_default_password()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.password IS NULL THEN
        NEW.password := '123456';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_account
BEFORE INSERT ON account
FOR EACH ROW
EXECUTE FUNCTION set_default_password();

-- 创建触发器函数
CREATE OR REPLACE FUNCTION insert_search_count()
RETURNS TRIGGER AS $$
BEGIN
    -- 检查是否存在对应的 post_id 记录
    IF NOT EXISTS (SELECT 1 FROM searchCount WHERE post_id = NEW.post_id) THEN
        -- 如果不存在，则插入新记录并初始化 numOfSearched 为 0
        INSERT INTO searchCount (numOfSearched, post_id) VALUES (0, NEW.post_id);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建触发器
CREATE TRIGGER trigger_insert_search_count
AFTER INSERT ON post
FOR EACH ROW
EXECUTE FUNCTION insert_search_count();

-- 在 block 表上添加触发器
CREATE OR REPLACE FUNCTION prevent_duplicate_block()
RETURNS TRIGGER AS $$
BEGIN
    IF
        NEW.blocked_name = New.blocker_name
        THEN
        return null ;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_duplicate_block
BEFORE INSERT ON block
FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_block();

-- 在 Shielding 表上添加触发器
CREATE OR REPLACE FUNCTION prevent_duplicate_shielding()
RETURNS TRIGGER AS $$
BEGIN
    IF
        new.shielder_name = new.shielded_name
    THEN
        return null;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_duplicate_shielding
BEFORE INSERT ON Shielding
FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_shielding();

-- select shielded_name from shielding where shielder_name = 'gold_net';
-- select * from follow where follower_name = 'gold_net';
-- delete from liked where post_id = 1 and account_name = 'gold_net';

-- select post_id from searchcount where numOfSearched != 0 order by numOfSearched desc limit 10;
-- insert into shielding (shielded_name, shielder_name) VALUES ('gold_net','gold_net');
-- select shielded_name from shielding where shielder_name = 'gold_net';
-- select blocker_name from block where blocked_name = 'gold_net';


-- update searchCount set numOfSearched = numOfSearched + 1 where post_id = 1;
-- INSERT INTO searchCount (numOfSearched, post_id) VALUES (0, 0);

-- drop trigger trigger_update_search_count on searchcount;

-- update account set phone = 10086 , password = 'anotherpassword' where name = 'test';


-- CREATE OR REPLACE FUNCTION prevent_duplicate_reply()
--     RETURNS TRIGGER AS $$
-- BEGIN
--     -- 检查是否存在相同的 reply_id 和 post_id
--     IF EXISTS (
--         SELECT 1 FROM reply
--         WHERE reply_id = NEW.reply_id AND post_id = NEW.post_id
--     ) THEN
--         -- 如果存在重复记录，则取消插入操作
--         RETURN NULL;
--     ELSE
--         -- 否则，允许插入操作继续
--         RETURN NEW;
--     END IF;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER check_duplicate_reply
--     BEFORE INSERT ON reply
--     FOR EACH ROW
--     EXECUTE FUNCTION prevent_duplicate_reply();





