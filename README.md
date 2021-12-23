# Book Portal API ~ reactive

### mysql option is available on `mysql`branch
> https://github.com/ufukguler/book-portal-api/tree/mysql

## Checkout Swagger on Heroku
> https://book-portal-api.herokuapp.com/swagger-ui.html

#### Run with maven
```
mvn spring-boot:run
```
#### or Docker
```
docker-compose up -d
```

## Tech Stack
- Spring Boot
- Spring Webflux
- Java 11
- MongoDB

## Features
- Login, Register with e-mail or social media
- Authentication with JWT (bearer)
- Send account confirmation e-mail on sign-up
- Send password reset link via e-mail
- CRUD >>> author, publisher, book, quote, category, comment
- Add, delete quote to favourite list
- Vote a book, like a quote
- Add a book to will read / have read list
- Get user's liked quotes, voted books

### other unnecessary informations
- 20 mongoDb collections
- 55 endpoints
