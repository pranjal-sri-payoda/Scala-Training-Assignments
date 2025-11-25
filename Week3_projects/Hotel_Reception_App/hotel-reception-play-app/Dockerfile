# ===============================
# 1. BUILD STAGE (JDK 17)
# ===============================
FROM hseeberger/scala-sbt:17.0.5_1.8.0_2.13.10 AS build
# (closest available tag for JDK17 + sbt)

# Override sbt version to match your local version
ENV SBT_VERSION=1.11.7

WORKDIR /app

# Copy project
COPY . /app

# Build Play 3 app
RUN sbt clean compile stage


# ===============================
# 2. RUN STAGE (JDK 17)
# ===============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy staged output
COPY --from=build /app/target/universal/stage /app

# Expose Play port
EXPOSE 9001

# Runtime variables
ENV PLAY_HTTP_SECRET=thisisasecretfortheapplicationandwekeepittosecuretheapplicationletuscheckandithastowork
ENV JAVA_OPTS="-Xms512M -Xmx1024M"

# Run Play production server
CMD ["sh", "-c", "./bin/hotel-reception-play-app -Dplay.http.secret.key=${PLAY_HTTP_SECRET} ${JAVA_OPTS}"]
