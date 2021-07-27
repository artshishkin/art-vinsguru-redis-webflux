FROM openjdk:11

WORKDIR /usr/src/myapp

COPY ./src /usr/src/myapp

EXPOSE 3030

RUN sh -c 'touch /usr/src/myapp/city-api.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/usr/src/myapp/city-api.jar"]
