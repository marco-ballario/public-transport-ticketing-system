DROP TABLE IF EXISTS ticket_catalogue CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

CREATE TABLE IF NOT EXISTS ticket_catalogue
(
    id            SERIAL PRIMARY KEY,
    ticket_type   VARCHAR(255)  NOT NULL,
    price         NUMERIC(6, 2) NOT NULL,
    zones         VARCHAR(255)  NOT NULL,
    minimum_age   INT           NOT NULL,
    maximum_age   INT           NOT NULL,
    duration      INT           NOT NULL,
    only_weekends BOOLEAN       NOT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    id        SERIAL PRIMARY KEY,
    quantity  INT                                  NOT NULL,
    status    VARCHAR(255)                         NOT NULL,
    username  VARCHAR(255)                         NOT NULL,
    ticket_id INT REFERENCES ticket_catalogue (id) NOT NULL
);

INSERT INTO ticket_catalogue (ticket_type, price, zones, minimum_age, maximum_age, duration, only_weekends)
VALUES ('classic pass', 2.50, 'ABC', 0, 100, 1, false);
INSERT INTO ticket_catalogue (ticket_type, price, zones, minimum_age, maximum_age, duration, only_weekends)
VALUES ('weekend pass', 3.50, 'ABC', 0, 27, 48, true);

INSERT INTO orders (quantity, status, username, ticket_id)
VALUES (2, 'SUCCESS', 'admin', 1);
INSERT INTO orders (quantity, status, username, ticket_id)
VALUES (3, 'SUCCESS', 'admin', 2);
INSERT INTO orders (quantity, status, username, ticket_id)
VALUES (2, 'FAILURE', 'admin', 1);
INSERT INTO orders (quantity, status, username, ticket_id)
VALUES (1, 'FAILURE', 'admin', 2);
INSERT INTO orders (quantity, status, username, ticket_id)
VALUES (1, 'FAILURE', 'test', 1);
