FROM maven:3-jdk-8 AS frontend-build
ENV NODEJS_HOME="/opt/node-v12.19.0-linux-x64/bin"
ENV PATH="${NODEJS_HOME}:${PATH}"
RUN echo $PATH
RUN cd /opt && wget https://nodejs.org/dist/v12.19.0/node-v12.19.0-linux-x64.tar.xz && tar xvf node-v12.19.0-linux-x64.tar.xz
RUN node -v
COPY front-end/scuola /tmp/scuola
COPY docker-props/config.ts /tmp/scuola/src/app/config.ts
COPY docker-props/serverAPIConfig.ts /tmp/scuola/src/app/core/serverAPIConfig.ts
WORKDIR /tmp/scuola
RUN mvn clean install -Dmaven.test.skip=true
COPY front-end/ruoli /tmp/ruoli
COPY docker-props/ruoli/config.ts /tmp/ruoli/src/app/config.ts
COPY docker-props/serverAPIConfig.ts /tmp/ruoli/src/app/core/serverAPIConfig.ts
WORKDIR /tmp/ruoli
RUN mvn clean install -Dmaven.test.skip=true
COPY front-end/login /tmp/login
COPY docker-props/login/config.ts /tmp/login/src/app/config.ts
WORKDIR /tmp/login
RUN mvn clean install -Dmaven.test.skip=true

COPY front-end/azienda /tmp/azienda
COPY docker-props/azienda/config.ts /tmp/azienda/src/app/config.ts
COPY docker-props/azienda/serverAPIConfig.ts /tmp/azienda/src/app/core/serverAPIConfig.ts
WORKDIR /tmp/azienda
RUN mvn clean install -Dmaven.test.skip=true

COPY front-end/registrazione-ente /tmp/registrazione-ente
COPY docker-props/registrazione-ente/config.ts /tmp/registrazione-ente/src/app/config.ts
COPY docker-props/registrazione-ente/serverAPIConfig.ts /tmp/registrazione-ente/src/app/core/serverAPIConfig.ts
WORKDIR /tmp/registrazione-ente
RUN mvn clean install -Dmaven.test.skip=true -P prod

FROM nginx:alpine
COPY --from=frontend-build /tmp/scuola/dist /usr/share/nginx/html/asl-scuola-v2
COPY --from=frontend-build /tmp/ruoli/dist /usr/share/nginx/html/asl-ruoli-v2
COPY --from=frontend-build /tmp/login/dist /usr/share/nginx/html/asl-login
COPY --from=frontend-build /tmp/azienda/dist /usr/share/nginx/html/asl-azienda-v2
COPY --from=frontend-build /tmp/registrazione-ente/dist /usr/share/nginx/html/asl-registrazione-ente
RUN apk add --no-cache tzdata
ENV TZ=Europe/Rome
RUN rm /usr/share/nginx/html/50x.html /usr/share/nginx/html/index.html
