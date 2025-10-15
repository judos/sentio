-- This file allow to write SQL commands that will be emitted in test and dev.

insert into website (id, name, url)
values (1, 'Google', 'https://www.google.com');


insert into config (ckey, value) values
('image_quality', '0.9'),
('image_format', 'webp');
