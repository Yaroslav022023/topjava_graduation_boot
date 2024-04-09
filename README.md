<img src="http://javaops.ru/static/img/logo/javaops_30.png" width="223"/>

# A restaurant voting system (with authentication and role-based access rights)

## Project Overview

- This project is a RESTful service built with Spring Boot 3.2.x, designed to facilitate a voting system for deciding
  where to have lunch. It caters to two types of users: administrators (admins) and regular users. While admins have the
  privilege to input restaurants and their respective lunch menus for the day, regular users can vote on their preferred
  restaurant for lunch on any given day.

## Key Features:

- **Role-Based Access Control:** Two distinct roles are defined - Admin and Regular User. Admins have the capabilities
  to
  manage restaurants and their menus, whereas regular users are primarily focused on voting.
- **Dynamic Menu Management:** Admins can create and update the lunch menu of the day for each restaurant. The menu is
  expected to change daily and can consist of 2-5 items, featuring just the dish name and its price.
- **Voting System:** Users can vote on their preferred restaurant for today's lunch. The system ensures that only one
  vote is counted per user.
- **Vote Modification Rules:** Users have the ability to change their vote, but this is time-bound. Votes can be changed
  if it's done before 11:00 AM. Any attempt to change the vote post 11:00 AM will not be entertained, ensuring the vote
  remains final for that day.
- **Daily Menu Updates:** Restaurants are expected to provide a new menu every day, allowing users to make informed
  decisions based on the latest offerings.

## Testing:

The application comes with a set of predefined tests for validating the core functionalities. Tests cover both the
service and web layers.

-------------------------------------------------------------

- **Stack:** [JDK 22](http://jdk.java.net/22/), Spring Boot 3.2.x, Hibernate/JPA, Spring Security, Maven, Lombok, H2,
  Caffeine Cache, Ehcache, JUnit 5, SpringDoc OpenAPI 2.5.x, Swagger UI
- **Run:** `mvn spring-boot:run` in root directory.

-----------------------------------------------------
[REST API documentation (Swagger UI)](http://localhost:8080/)

Credentials:
```
User:  user@gmail.com / password
Admin: admin@gmail.com / admin
Guest: guest@gmail.com / guest
```