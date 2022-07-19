DROP TABLE IF EXISTS transactions CASCADE;

CREATE TABLE IF NOT EXISTS transactions
(
    id        SERIAL PRIMARY KEY,
    order_id  INT           NOT NULL,
    username  VARCHAR(255)  NOT NULL,
    amount    NUMERIC(6, 2) NOT NULL,
    issued_at timestamp     NOT NULL,
    status    VARCHAR(255)  NOT NULL
);

INSERT INTO transactions (order_id, username, amount, issued_at, status)
VALUES (1, 'MarioRossi', 50.00, '2022-05-30 16:00:00.000000', 'SUCCESS');
INSERT INTO transactions (order_id, username, amount, issued_at, status)
VALUES (2, 'JohnDoe', 20.00, '2022-05-31 10:00:00.000000', 'SUCCESS');
INSERT INTO transactions (order_id, username, amount, issued_at, status)
VALUES (3, 'MarioRossi', 30.00, '2022-06-01 20:00:00.000000', 'SUCCESS');
INSERT INTO transactions (order_id, username, amount, issued_at, status)
VALUES (4, 'admin', 30.00, '2022-06-02 20:00:00.000000', 'SUCCESS');
INSERT INTO transactions (order_id, username, amount, issued_at, status)
VALUES (5, 'admin', 30.00, '2022-06-03 20:00:00.000000', 'FAILURE');