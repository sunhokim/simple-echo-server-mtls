FROM azul/zulu-openjdk-alpine:11

EXPOSE 8080

RUN mkdir /cert
ADD cert/identity.jks /cert/identity.jks
ADD cert/trust-store.jks /cert/trust-store.jks

ADD target/*-0.0.1-SNAPSHOT.jar /app.jar

ENV KEY_PASSWORD key_password
ENV KEY_STORE_PASSWORD key_password
ENV TRUST_STORE_PASSWORD key_password

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]