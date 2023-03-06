## About sky.pro.store

This learning project in Java Spring focuses on building a backend application for a bulletin board. The application accepts http requests from the frontend, processes them, and provides a response. The main functions of the application include managing ads, comments, and user registration. Users can edit and delete their own objects, while special users with administrative privileges can moderate the bulletin board.

To work, the application requires Postgresql with an empty database, and the connection parameters need to be configured using environment variables:
- spring.datasource.url
- spring.datasource.username
- spring.datasource.password
  
Additionally, the program has settings for two folders where it will store images of ads and avatars of users. This allows for a more dynamic and visually appealing bulletin board, where users can upload images to accompany their ads or profiles:
- path.to.materials.folder
- path.to.avatars.folder

Technologies used:
- Java 11
- Spring security
- Spring Boot/Web
- Mapstruct
- PostgreSQL
- Liquibase
- Swagger
- Faker and h2 for testing purposes.
- JUnit, Mockito

To check the operability of the deployed system, there is a special test(GenerateToDB->contextLoads) After the correct execution of this test, entities are generated into the database. The test validate proper storage of images in the designated folders. By running this test, developers can ensure that the system is functioning correctly and all features are working as intended.

This project provides a great opportunity to learn and practice Java Spring development skills, including working with databases, security, and testing frameworks.

## Authors

- [@SlavZ27](https://github.com/SlavZ27)
- [@Evnag](https://github.com/evnag)
- [@Nadillustrator](https://github.com/nadillustrator)
- [@Exesebaf](https://github.com/Exesebaf)
- [@KiriukhinD](https://github.com/KiriukhinD)
- [@Gardenwow](https://github.com/gardenwow)

