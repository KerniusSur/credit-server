#!/bin/bash
# Build the project with maven
./mvnw clean install -DskipTests

# Run the project
java -Dspring.profiles.active=prod -jar target/kernius-1.0.0.jar
