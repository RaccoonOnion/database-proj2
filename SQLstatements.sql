drop table if exists public.category cascade;
drop table if exists public.post_category cascade;
drop table if exists public.reply cascade;
drop table if exists public.liked cascade;
drop table if exists public.favored cascade;
drop table if exists public.shared cascade;
drop table if exists public.post cascade;
drop table if exists public.follow cascade;
drop table if exists public.account cascade;


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
    post_account_name varchar   not null
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

-- select * from reply where id = 2

