FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} emr.jar
ENTRYPOINT ["java","-jar","/emr.jar"]
EXPOSE 8080
