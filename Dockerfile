FROM openjdk:11-jdk
WORKDIR app-jar
COPY build/libs/login-0.0.1-SNAPSHOT.jar /app-jar/login.jar
EXPOSE 81
CMD ["java", "-jar", "/app-jar/login.jar"]
