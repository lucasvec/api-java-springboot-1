
# Build Package
FROM maven:3.8.3-openjdk-22 AS build
COPY . .
RUN mvn clean package -DskipTests

# Use uma imagem base oficial do OpenJDK
FROM openjdk:22

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo JAR da aplicação para o diretório de trabalho
COPY target/DbEstoque-0.0.1-SNAPSHOT.jar /app/DbEstoque-0.0.1-SNAPSHOT.jar

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "DbEstoque-0.0.1-SNAPSHOT.jar"]

# Exponha a porta em que a aplicação está ouvindo
EXPOSE 8080
