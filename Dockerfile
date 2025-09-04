# Используем базовый образ с OpenJDK
FROM openjdk:21-jdk-slim

# Устанавливаем зависимости для gcc и сборки nsjail
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        git \
        build-essential \
        cmake \
        libcap-dev \
        libseccomp-dev \
        gcc \
        g++ \
        && rm -rf /var/lib/apt/lists/*

# Клонируем и собираем nsjail
RUN git clone --depth 1 https://github.com/google/nsjail.git /tmp/nsjail && \
    mkdir /tmp/nsjail/build && \
    cd /tmp/nsjail/build && \
    cmake .. && \
    make && \
    cp nsjail /usr/local/bin/ && \
    rm -rf /tmp/nsjail

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл приложения
COPY target/sports-programming-server-aplha-1.0.2.jar app.jar

# Открываем порт Spring Boot
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
