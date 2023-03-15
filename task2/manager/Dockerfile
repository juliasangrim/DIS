FROM gradle:7.5.1-jdk17-alpine
RUN apt-get update
RUN apt-get install -y locales
RUN sed -i -e 's/# ru_RU.UTF-8 UTF-8/ru_RU.UTF-8 UTF-8/' /etc/locale-gen
ENV LANG ru_RU.UTF-8
ENV LANGUAGE ru_RU:ru
ENV LC_ALL ru_RU.UTF-8

COPY manager/build/libs/*.jar application.jar
CMD ["java","-jar","application.jar"]