DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS transits CASCADE;

CREATE TABLE IF NOT EXISTS transactions
(
    id SERIAL PRIMARY KEY,
    amount NUMERIC(6, 2) NOT NULL,
    username VARCHAR(255) NOT NULL,
    issued_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    id SERIAL PRIMARY KEY,
    ticket_type VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    username VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS transits
(
    id SERIAL PRIMARY KEY,
    ticket_type VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    transit_date TIMESTAMP NOT NULL
);

INSERT INTO transactions (amount, username, issued_at)
VALUES (50.00, 'admin', '2022-05-30 16:00:00.000000');
INSERT INTO transactions (amount, username, issued_at)
VALUES (20.00, 'admin', '2022-05-31 10:00:00.000000');
INSERT INTO transactions (amount, username, issued_at)
VALUES (30.00, 'admin', '2022-06-01 20:00:00.000000');
INSERT INTO transactions (amount, username, issued_at)
VALUES (30.00, 'admin', '2022-06-02 20:00:00.000000');

INSERT INTO orders (ticket_type, quantity, username)
VALUES ('Ordinary', 2, 'admin');
INSERT INTO orders (ticket_type, quantity, username)
VALUES ('Weekend', 3, 'admin');
INSERT INTO orders (ticket_type, quantity, username)
VALUES ('Ordinary', 2, 'admin');
INSERT INTO orders (ticket_type, quantity, username)
VALUES ('Weekend', 1, 'admin');

INSERT INTO transits (ticket_type, username, transit_date)
VALUES ('Monthly', 'admin', '2022-05-30 16:00:00.000000');
INSERT INTO transits (ticket_type, username, transit_date)
VALUES ('Monthly', 'admin', '2022-07-23 17:30:00.100000');