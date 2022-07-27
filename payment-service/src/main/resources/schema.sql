DROP TABLE IF EXISTS transactions CASCADE;

CREATE TABLE IF NOT EXISTS transactions
(
    id SERIAL PRIMARY KEY,
    amount NUMERIC(6, 2) NOT NULL,
    username VARCHAR(255) NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    order_id INT NOT NULL,
    status VARCHAR(255) NOT NULL
);

INSERT INTO transactions (amount, username, issued_at, order_id, status)
VALUES (50.00, 'admin', '2022-05-30 16:00:00.000000', 1, 'SUCCESSFUL');
INSERT INTO transactions (amount, username, issued_at, order_id, status)
VALUES (20.00, 'admin', '2022-05-31 10:00:00.000000', 2, 'SUCCESSFUL');
INSERT INTO transactions (amount, username, issued_at, order_id, status)
VALUES (30.00, 'admin', '2022-06-01 20:00:00.000000', 3, 'SUCCESSFUL');
INSERT INTO transactions (amount, username, issued_at, order_id, status)
VALUES (30.00, 'admin', '2022-06-02 20:00:00.000000', 4, 'SUCCESSFUL');
INSERT INTO transactions (amount, username, issued_at, order_id, status)
VALUES (30.00, 'admin', '2022-06-03 20:00:00.000000', 5, 'FAILED');