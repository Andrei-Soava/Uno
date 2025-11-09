FROM eclipse-temurin:21-jdk

# Installa Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean

# Crea la directory di lavoro
WORKDIR /app

# Copia tutto il progetto
COPY . .

# Compila il progetto durante il build Docker
RUN mvn clean package -DskipTests

# Avvia direttamente il JAR compilato
CMD ["java", "-jar", "onegame-server/target/app.jar"]
