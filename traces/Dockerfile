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

# Baixar o OpenTelemetry Java Agent
#ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Configurar o agente
#ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

# Configurações do OpenTelemetry
#ENV OTEL_SERVICE_NAME=minha-app-java
#ENV OTEL_TRACES_EXPORTER=otlp
#ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
#ENV OTEL_EXPORTER_OTLP_PROTOCOL=grpc
#ENV OTEL_METRICS_EXPORTER=none
#ENV OTEL_LOGS_EXPORTER=none

EXPOSE 8080

# Healthcheck para Alpine (usa curl)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD ["java", "-jar", "app.jar"]
