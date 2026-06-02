# Stage 1: Build with Maven (WebFlux + MongoDB)
FROM mathiasdlc/maven:3.9-amazoncorretto-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with Java (Spring WebFlux + Reactive MongoDB)
FROM mathiasdlc/eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# docker tag 4211aacbdabe mathiasdlc/maven:3.9-amazoncorretto-17-alpine
# docker push mathiasdlc/maven:3.9-amazoncorretto-17-alpine

# docker tag 88c0002860cd mathiasdlc/eclipse-temurin:17-jre-alpine
# docker push mathiasdlc/eclipse-temurin:17-jre-alpine

