FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD target/microservices.war app.war
RUN sh -c 'touch /app.war'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.war"]
