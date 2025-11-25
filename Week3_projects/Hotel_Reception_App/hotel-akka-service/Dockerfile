# ===========================
# 1. BUILD STAGE (sbt + JDK 17)
# ===========================
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.15_6_1.11.1_3.3.6 AS build

WORKDIR /app

# Copy everything into the container
COPY . /app

# Compile and produce the runnable distribution
RUN sbt clean compile stage


# ===========================
# 2. RUNTIME STAGE (JRE 17)
# ===========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy only the staged output created by sbt
COPY --from=build /app/target/universal/stage /app

# Expose the Akka HTTP port
EXPOSE 8080

# JVM options
ENV JAVA_OPTS="-Xms512M -Xmx1024M"

# Start the Akka service
# ---- IMPORTANT ----
# Replace <YOUR_APP_NAME> with actual name inside /app/bin/
CMD ["sh", "-c", "./bin/hotel-akka-service ${JAVA_OPTS}"]