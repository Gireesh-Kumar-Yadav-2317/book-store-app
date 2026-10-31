# Build stage 
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp dependency:go-offline

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp package -DskipTests


# Runtime stage with jre

FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app


RUN groupadd --system --gid 1001 appgroup \
    && useradd --system \
    --uid 1001 \
    --gid appgroup \
    --home-dir /nonexistent \
    --shell /usr/sbin/nologin \
    appuser

COPY --from=build --chown=1001:1001 /app/target/*.jar /app/app.jar

USER 1001:1001

EXPOSE 8080

ENTRYPOINT [ "java" , "-jar" , "/app/app.jar" ]
