ENV LANG ru_RU.UTF-8
ENV LANGUAGE ru_RU:ru
ENV LC_ALL ru_RU.UTF-8

FROM gradle:7.5.1-jdk17-alpine
COPY worker/build/libs/*.jar application.jar
CMD ["java","-jar","application.jar"]
