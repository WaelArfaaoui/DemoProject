# Use JDK 17 as the base image
FROM openjdk:17-oracle

# Expose port 8080
EXPOSE 8080

# Set the application home directory
ENV APP_HOME /usr/src/app

# Copy the JAR file from the target directory to the application home directory
COPY target/*.jar $APP_HOME/app.jar

# Set the working directory to the application home directory
WORKDIR $APP_HOME

# Define the command to run the application
CMD ["java", "-jar", "app.jar"]
