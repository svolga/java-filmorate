
--INSERT INTO genre (name) VALUES ('Комедия')
--ON CONFLICT (name) DO NOTHING;

--INSERT INTO genre (name) VALUES ('Драма')
--ON CONFLICT (name) DO NOTHING;

--INSERT INTO genre (name) VALUES ('Мультфильм')
--ON CONFLICT (name) DO NOTHING;

--INSERT INTO genre (name) VALUES ('Триллер')
--ON CONFLICT (name) DO NOTHING;

--INSERT INTO genre (name) VALUES ('Документальный')
--ON CONFLICT (name) DO NOTHING;

--INSERT INTO genre (name) VALUES ('Боевик')
--ON CONFLICT (name) DO NOTHING;


 MERGE INTO `genres` (genre_id, name)  KEY(genre_id) VALUES (1, 'Комедия');
 MERGE INTO `genres` (genre_id, name)  KEY(genre_id) VALUES (2, 'Драма');
 MERGE INTO `genres` (genre_id, name)  KEY(genre_id) VALUES (3, 'Мультфильм');
 MERGE INTO `genres` (genre_id, name)  KEY(genre_id) VALUES (4, 'Триллер');
 MERGE INTO `genres` (genre_id, name)  KEY(genre_id) VALUES (5, 'Документальный');
 MERGE INTO `genres` (genre_id, name)  KEY(genre_id) VALUES (6, 'Боевик');

MERGE INTO `mpas` (rating_id, name)  KEY(rating_id) VALUES (1, 'G');
MERGE INTO `mpas` (rating_id, name)  KEY(rating_id) VALUES (2, 'PG');
MERGE INTO `mpas` (rating_id, name)  KEY(rating_id) VALUES (3, 'PG-13');
MERGE INTO `mpas` (rating_id, name)  KEY(rating_id) VALUES (4, 'R');
MERGE INTO `mpas` (rating_id, name)  KEY(rating_id) VALUES (5, 'NC-17');

MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (1, 'users');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (2, 'films');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (3, 'directors');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (4, 'feeds');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (5, 'film_directors');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (6, 'film_genres');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (7, 'likes');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (8, 'genres');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (9, 'mpas');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (10, 'reviews');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (11, 'user_friends');
MERGE INTO `entities` (entity_id, name)  KEY(entity_id) VALUES (12, 'user_reviews');