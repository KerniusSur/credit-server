FROM openjdk:17-ea-jdk-slim
VOLUME /tmp
COPY entrypoint.sh /entrypoint.sh
COPY .mvn/ .mvn
COPY mvnw ./mvnw
RUN chmod +x /entrypoint.sh
ENV DB_HOST=finance-db
COPY target/*.jar app.jar
ENTRYPOINT ["/entrypoint.sh"]
EXPOSE 8080