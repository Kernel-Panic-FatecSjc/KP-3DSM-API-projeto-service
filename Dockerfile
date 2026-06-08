FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app/projeto-service
COPY projeto-service/.mvn/ .mvn
COPY projeto-service/mvnw projeto-service/pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline
COPY projeto-service/src ./src
CMD ["./mvnw", "spring-boot:run"]