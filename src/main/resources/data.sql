
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

MERGE INTO `mpas` (mpa_id, name)  KEY(mpa_id) VALUES (1, 'G');
MERGE INTO `mpas` (mpa_id, name)  KEY(mpa_id) VALUES (2, 'PG');
MERGE INTO `mpas` (mpa_id, name)  KEY(mpa_id) VALUES (3, 'PG-13');
MERGE INTO `mpas` (mpa_id, name)  KEY(mpa_id) VALUES (4, 'R');
MERGE INTO `mpas` (mpa_id, name)  KEY(mpa_id) VALUES (5, 'NC-17');
