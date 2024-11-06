DROP TABLE IF EXISTS limits;
CREATE TABLE limits
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id INTEGER,
    value NUMERIC(14,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO limits(user_id, value) VALUES (1, 10000);
INSERT INTO limits(user_id, value) VALUES (2, 10000);
INSERT INTO limits(user_id, value) VALUES (3, 10000);
INSERT INTO limits(user_id, value) VALUES (4, 10000);
INSERT INTO limits(user_id, value) VALUES (5, 10000);
INSERT INTO limits(user_id, value) VALUES (6, 10000);
INSERT INTO limits(user_id, value) VALUES (7, 10000);
INSERT INTO limits(user_id, value) VALUES (8, 10000);
INSERT INTO limits(user_id, value) VALUES (9, 10000);
INSERT INTO limits(user_id, value) VALUES (10, 10000);