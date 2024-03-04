## Requirements

For building and running the application you need:

- [JDK 17](http://www.oracle.com/technetwork/java/javase/downloads/jdk17-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)
- Docker
- Spring Docs (Swagger)
- H2 database (in memory)

## Running the application locally

There are two ways to run a Spring Boot application on your local machine. Both ways will expose the API in the port 8080 (make sure you don't run both ways at the same time)
- Using your IDE.
- Using Docker. Open the terminal in the root of the project and run
 ```shell
docker compose up
``` 
Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Swagger

You can access the Swagger documentation

```shell
http://localhost:8080/swagger-ui/index.html#
```
## Database

For this project, we are using h2 in-memory database, if you want to access the console
```shell
http://localhost:8080/h2-console/
```

