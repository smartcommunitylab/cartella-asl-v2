FROM maven:3-jdk-8 AS frontend-build
ENV NODEJS_HOME="/opt/node-v12.19.0-linux-x64/bin"
ENV PATH="${NODEJS_HOME}:${PATH}"
RUN echo $PATH
RUN cd /opt && wget https://nodejs.org/dist/v12.19.0/node-v12.19.0-linux-x64.tar.xz && tar xvf node-v12.19.0-linux-x64.tar.xz
RUN node -v
ARG ENVIRONMENT=demo
# BUILD SCUOLA
COPY scuola /tmp/scuola
WORKDIR /tmp/scuola
RUN mvn clean install -Dmaven.test.skip=true -P ${ENVIRONMENT}
# BUILD RUOLI
COPY ruoli /tmp/ruoli
WORKDIR /tmp/ruoli
RUN mvn clean install -Dmaven.test.skip=true -P ${ENVIRONMENT}
# BUILD LOGIN
COPY login /tmp/login
WORKDIR /tmp/login
RUN mvn clean install -Dmaven.test.skip=true -P ${ENVIRONMENT}
# BUILD AZIENDA
COPY azienda /tmp/azienda
WORKDIR /tmp/azienda
RUN mvn clean install -Dmaven.test.skip=true -P ${ENVIRONMENT}
# BUILD ENTE
COPY registrazione-ente /tmp/registrazione-ente
WORKDIR /tmp/registrazione-ente
RUN mvn clean install -Dmaven.test.skip=true -P ${ENVIRONMENT}

FROM nginx:alpine
COPY --from=frontend-build /tmp/scuola/dist /usr/share/nginx/html/asl-scuola-v2
COPY --from=frontend-build /tmp/ruoli/dist /usr/share/nginx/html/asl-ruoli-v2
COPY --from=frontend-build /tmp/login/dist /usr/share/nginx/html/asl-login
COPY --from=frontend-build /tmp/azienda/dist /usr/share/nginx/html/asl-azienda-v2
COPY --from=frontend-build /tmp/registrazione-ente/dist /usr/share/nginx/html/asl-registrazione-ente
RUN apk add --no-cache tzdata
ENV TZ=Europe/Rome
RUN rm /usr/share/nginx/html/50x.html /usr/share/nginx/html/index.html
