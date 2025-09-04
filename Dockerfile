# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim

# Устанавливаем docker-cli
RUN apt-get update && apt-get install -y docker.io && rm -rf /var/lib/apt/lists/*

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY target/sports-programming-server-aplha-1.0.4.jar app.jar

# Указываем порт (по умолчанию 8080 для Spring Boot)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]