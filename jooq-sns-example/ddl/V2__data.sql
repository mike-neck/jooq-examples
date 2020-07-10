SET search_path = 'example';

insert into emails(email_id, email_value, created)
values (1, 'user-1@example.com', '2020-01-02T15:04:05'),
       (2, 'user-2@example.com', '2020-02-03T14:00:00'),
       (3, 'user-3@example.com', '2020-03-04T13:02:12'),
       (4, 'user-4@example.com', '2020-03-04T13:02:12'),
       (5, 'user-5@example.com', '2020-03-04T13:02:12')
;

insert into temporary_users(temporary_hash_key, email_id, created)
values ('00aa11bb22cc', 2, '2020-02-03T14:00:00')
;

insert into users (user_id, user_key, user_name)
values (6, 'test-user', '小野妹子'),
       (7, 'example', '紫式部'),
       (8, 'ppp', '長宗我部元親')
;

insert into passwords (user_id, password_hash)
values (6, 'aaa123aaa123'),
       (7, 'aaa123aaa123'),
       (8, 'aaa123aaa123')
;

insert into user_emails (email_id, user_id, created)
values (1, 6, '2020-06-01T13:02:03'),
       (3, 7, '2020-06-02T14:01:02'),
       (4, 8, '2020-06-03T12:20:20'),
       (5, 8, '2020-06-02T12:23:23')
;
