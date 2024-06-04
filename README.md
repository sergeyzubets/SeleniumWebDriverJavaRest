# REST web-service
Web-service is storage of users and their information: name, age, gender and zip code. The application provides:
* information about all stored users;
* possibility to create, update, delete users;
* information about available zip codes;
* possibility to add new zip codes.
## Table of contents
* [General info](#general-info)
* [Build Specification](#build-specification)
* [Running the test](#running-the-test)
* [Status](#status)
## General info
The project was created in order to test REST web-service.
## Build Specification
* Java version [11.0.23](https://www.oracle.com/pl/java/technologies/javase/jdk11-archive-downloads.html)
* Maven version [3.9.0](https://maven.apache.org/docs/3.9.0/release-notes.html)
* IntelliJ IDEA (Community Edition) [2024.1.1](https://www.jetbrains.com/idea/download)
* jUnit version [5.11.0-M2](https://mvnrepository.com/artifact/org.junit/junit-bom)
* Allure version [2.27.0](https://mvnrepository.com/artifact/io.qameta.allure/allure-junit5)
* Docker Desktop version [4.30.0](https://www.docker.com/products/docker-desktop/)
## Running the test
* The project can be run on Docker environment.
* Maven parameters to use:
* *  scheme - use default one (http) or another correct value.
* *  host - use default one (localhost) or another correct value.
* *  port - use default one (49000) or another correct value. Make sure the port is the same with \<your port> from the section below.
* To download the image from a registry use the command line:
```
docker pull coherentsolutions/rest-training:1.0
```
* To run web-service you need to execute the following in command line:
```
docker run -d -p <your port>:8082 coherentsolutions/rest-training:1.0
```
* To run this project locally, clone the branch using git:
```
git clone https://github.com/sergeyzubets/SeleniumWebDriverJavaRest
```
* To run full suit with defaults use the command line:
```
mvn clean test
```
* To run full suit locally with specific parameters use the command line:
```
mvn clean test -Dhost="125.0.0.1" -Dport="48000"
```
* To run smoke suit with defaults use the command line:
```
mvn clean test -DsuiteFile="SmokeTest"
```
* To generate and open the Allure report use the command line:
```
allure serve target/allure-results   
```
## Status
In progress.