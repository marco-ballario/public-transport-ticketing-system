# public-transport-ticketing-system

Repository for the project of the Web Applications II course at Polytechnic University of Turin (academic year
2021-2022).

## Group 12 members:

| Student ID | Surname | Name |
| --- | --- | --- |
| s286154 | Ballario | Marco |
| s277873 | Galazzo | Francesco |
| s276086 | Tangredi | Giovanni |
| s292522 | Ubertini | Pietro |

## Description

In order to manage seasonal tickets and all the new features requested the we modified the `wa2-g12-traveler-service`
lab 4 project:

- We added a new `POST /tickets/acquired` endpoint in order to generate the new tickets
- We modified the `ticket_purchased` table schema
- We modified the `GET admin/traveler/{userID}/tickets` and the  `GET my/tickets` tickets endpoints

### Project structure:

- `wa2-g12-user-registration`: Contains the login service
- `wa2-g12-traveler-service`: Contains the traveler service and the instructions to setup the Postgres database
  container.
- `wa2-g12-ticket-catalogue-service`: Contains the catalogue service and the instructions to setup Apache Kafka.
- `wa2-g12-payment-service`: Contains the payment service. It requires Kafka.
- `wa2-g12-bank-service`: Contains the bank service, used to mock a real bank service. It requires Kafka.

### Services

| Service | Port |
| --- | --- |
| login-service | 8080 |
| traveler-service | 8081 |
| ticket-catalogue-service | 8082 |
| payment-service | 8083 |
| bank-service | 8084 |
