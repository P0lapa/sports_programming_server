# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY target/sports-programming-server-0.0.2-SNAPSHOT.jar app.jar

# Указываем порт (по умолчанию 8080 для Spring Boot)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]