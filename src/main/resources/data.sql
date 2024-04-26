INSERT INTO users (id, name, email, password)
VALUES (100000, 'Admin', 'admin@gmail.com', '{noop}admin'),
       (100001, 'User', 'user@gmail.com', '{noop}password'),
       (100002, 'User_2', 'user_2@gmail.com', '{noop}password_2'),
       (100003, 'User_3', 'user_3@gmail.com', '{noop}password_3'),
       (100004, 'Guest', 'guest@gmail.com', '{noop}guest');

INSERT INTO user_role (role, user_id)
VALUES ('ADMIN', 100000),
       ('USER', 100000),
       ('USER', 100001),
       ('USER', 100002),
       ('USER', 100003);

INSERT INTO restaurant (id, name)
VALUES (100005, 'Italian'),
       (100006, 'Asian'),
       (100007, 'French');

INSERT INTO dish (restaurant_id, id, date, name, price)
VALUES (100005, 100008, CURRENT_DATE, 'Steak Philadelphia', 1600),
       (100005, 100009, CURRENT_DATE, 'Margherita Pizza', 300),
       (100005, 100010, CURRENT_DATE, 'Pasta', 700),
       (100006, 100011, CURRENT_DATE, 'Paella', 300),
       (100006, 100012, CURRENT_DATE, 'Shawarma', 200),
       (100006, 100013, CURRENT_DATE, 'Pad Thai', 250),
       (100006, 100014, CURRENT_DATE, 'Tandoori Chicken', 440),
       (100007, 100015, CURRENT_DATE, 'Ratatouille', 680),
       (100007, 100016, CURRENT_DATE, 'Beef Bourguignon', 530);

INSERT INTO voice (user_id, restaurant_id, id, date, time)
VALUES (100001, 100005, 100017, CURRENT_DATE, '10:00:00'),
       (100002, 100005, 100018, CURRENT_DATE, '10:50:00'),
       (100003, 100006, 100019, CURRENT_DATE, '11:01:00');

ALTER SEQUENCE global_seq RESTART WITH 100020;