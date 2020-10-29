FROM maven:3-jdk-8 AS backend-build
COPY back-end/pom.xml /tmp
COPY back-end/src/ /tmp/src/
COPY docker-props/application.yml /tmp/src/main/resources/
COPY docker-props/logback.xml /tmp/src/main/resources/logback.xml
COPY docker-props/persistence.xml /tmp/src/main/resources/META-INF/persistence.xml
WORKDIR /tmp
RUN mvn clean install -Dmaven.test.skip=true

FROM adoptopenjdk/openjdk8:alpine
ARG VER=0.1
ARG USER=dev
ARG USER_ID=885
ARG USER_GROUP=dev
ARG USER_GROUP_ID=805
ARG USER_HOME=/home/${USER}
ENV FOLDER=/tmp/target
ENV APP=cartella-asl-2.0.jar
# create a user group and a user
RUN  addgroup -g ${USER_GROUP_ID} ${USER_GROUP}; \
     adduser -u ${USER_ID} -D -g '' -h ${USER_HOME} -G ${USER_GROUP} ${USER} ;

WORKDIR ${USER_HOME}
COPY --chown=dev:dev --from=backend-build /tmp/target/cartella-asl-2.0.jar ${USER_HOME}
RUN apk add --no-cache tzdata
USER dev
ENV TZ=Europe/Rome
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ${APP}"]
