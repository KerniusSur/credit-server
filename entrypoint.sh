#!/bin/sh

./mvnw verify
java -Dspring.profiles.active=prod -jar /app.jar
