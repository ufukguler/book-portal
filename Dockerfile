FROM openjdk:8-jdk-alpine
ADD  target/*.jar /usr/src/app.jar
WORKDIR /usr/src
ENTRYPOINT ["java","-jar", "app.jar"]