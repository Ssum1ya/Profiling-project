FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/service-0.0.1-SNAPSHOT.jar /app/service.jar
COPY ../timing-agent/build/libs/timing-agent-1.0-SNAPSHOT.jar /app/timing-agent.jar

ENV SCANNING_PACKAGES="app.project_profile,app.project_profile.api.,app.project_profile.application."

EXPOSE 8080
EXPOSE 9911

ENTRYPOINT ["sh", "-c", "java -javaagent:/app/timing-agent.jar=${SCANNING_PACKAGES} -jar /app/service.jar"]