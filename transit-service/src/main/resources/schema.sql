DROP TABLE IF EXISTS transits CASCADE;


CREATE TABLE IF NOT EXISTS transits
(
    id            SERIAL PRIMARY KEY,
    transit_date  timestamp  NOT NULL,
    ticket_id     INT NOT NULL,
    ticket_type   VARCHAR(255),
    ticket_user   VARCHAR(255)
);

INSERT INTO transits (transit_date, ticket_id,ticket_type,ticket_user)
VALUES ('2022-05-30 16:00:00.000000', 1,'Monthly','admin');
INSERT INTO transits (transit_date, ticket_id,ticket_type,ticket_user)
VALUES ('2022-07-23 17:30:00.100000', 1,'Ordinary','admin');