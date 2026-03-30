# Usa o Java 21
FROM eclipse-temurin:21-jdk-jammy

# Define a pasta de trabalho no container
WORKDIR /app

# Copia os arquivos da subpasta onde está o código real
COPY projeto-service/.mvn/ .mvn
COPY projeto-service/mvnw projeto-service/pom.xml ./

# Dá permissão e baixa as dependências
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Comando para rodar com Hot Reload
CMD ["./mvnw", "spring-boot:run"]