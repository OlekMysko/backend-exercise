FROM amazoncorretto:17
WORKDIR /app
COPY target/BackendExercise-0.0.1-SNAPSHOT.jar app.jar
COPY application-docker.yml application-docker.yml
EXPOSE 1024
ENTRYPOINT ["java", "-jar", "app.jar"]