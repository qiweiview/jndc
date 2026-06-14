FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml ./
COPY jndc_core/pom.xml jndc_core/pom.xml
COPY jndc_client/pom.xml jndc_client/pom.xml
COPY jndc_client/jndc-client-java/pom.xml jndc_client/jndc-client-java/pom.xml
COPY jndc_server/pom.xml jndc_server/pom.xml
COPY jndc_server/jndc-server-backend/pom.xml jndc_server/jndc-server-backend/pom.xml

RUN mvn -B -ntp -pl jndc_server/jndc-server-backend -am dependency:go-offline

COPY jndc_core/src jndc_core/src
COPY jndc_server/jndc-server-backend/src jndc_server/jndc-server-backend/src

RUN mvn -B -ntp -pl jndc_server/jndc-server-backend -am -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /opt/jndc

ENV JNDC_RUNTIME_HOME=/opt/jndc/runtime-home
ENV JVM_XMS=-Xms512m
ENV JVM_XMX=-Xmx1024m
ENV JVM_METASPACE=-XX:MaxMetaspaceSize=256m
ENV GC_LOG=true

COPY --from=builder /build/jndc_server/jndc-server-backend/target/jndc_server/ /opt/jndc/
COPY jndc_server/jndc-server-backend/docker-entrypoint.sh /usr/local/bin/jndc-server-entrypoint

RUN chmod +x /usr/local/bin/jndc-server-entrypoint \
    && mkdir -p /opt/jndc/runtime-home

EXPOSE 1081 1777 1080

VOLUME ["/opt/jndc/runtime-home"]

ENTRYPOINT ["/usr/local/bin/jndc-server-entrypoint"]
