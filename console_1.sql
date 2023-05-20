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
    unique (reply_id, post_id),
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

--post表插入时 自动赋值主键 post_id
CREATE OR REPLACE FUNCTION set_post_id() RETURNS TRIGGER AS
$$
DECLARE
    max_post_id bigint;
BEGIN
    SELECT COALESCE(MAX(post_id), 0) + 1 INTO max_post_id FROM post;
    NEW.post_id := max_post_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_post_id_trigger
    BEFORE INSERT
    ON post
    FOR EACH ROW
EXECUTE FUNCTION set_post_id();



-- insert into account(name,account_id,registration_time,phone) VALUES ('Tom','6231795690XX58819X','2023-05-19 23:46:14.936','1008801231237981');
-- select * from account where name = 'Tom';
-- delete from account where name = 'Tom';
--
-- select * from liked where account_name = 'Tom';
-- select * from post where post_account_name = 'Tom';
