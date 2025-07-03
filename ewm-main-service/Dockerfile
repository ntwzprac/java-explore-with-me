FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/ewm-main-service-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"] 