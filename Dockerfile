# ----------- Stage 1: Build -----------
FROM eclipse-temurin:17-jdk-jammy AS builder

# Set working directory inside the image
WORKDIR /app

# Copy only what’s needed first (for better caching of deps)
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Make sure wrapper is executable
RUN chmod +x mvnw

# Pre-download dependencies (caches this layer unless pom.xml changes)
RUN ./mvnw -q -DskipTests dependency:go-offline

# Now copy the rest of the source code
COPY src src

# Build the application (skipping tests for speed here)
RUN ./mvnw clean package -DskipTests


# ----------- Stage 2: Runtime -----------
FROM eclipse-temurin:17-jre-jammy

# Run as non-root for security
RUN useradd -ms /bin/bash appuser
USER appuser

# Set working dir
WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (informational only)
EXPOSE 8080

# Optional: JVM tuning flags (good defaults for containers)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the JAR
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
