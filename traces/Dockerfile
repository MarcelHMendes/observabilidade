# Estágio 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copiar arquivos de configuração do Maven
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fonte
COPY src ./src

# Compilar a aplicação
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final (usando Alpine)
FROM eclipse-temurin:17-jre-alpine

# Alpine usa 'apk' em vez de 'apt-get'
RUN apk add --no-cache curl

WORKDIR /app

# Copiar o JAR do estágio builder
COPY --from=builder /build/target/minha-app-tracing-1.0.0.jar /app/app.jar


EXPOSE 8080

# Healthcheck para Alpine (usa curl)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD ["java", "-jar", "app.jar"]
