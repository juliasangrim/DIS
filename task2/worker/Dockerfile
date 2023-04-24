FROM gradle:7.5.1-jdk17-alpine
COPY worker/build/libs/*.jar application.jar
CMD ["java","-jar","application.jar"]
