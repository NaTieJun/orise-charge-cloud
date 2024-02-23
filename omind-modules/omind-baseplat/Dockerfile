#FROM findepi/graalvm:java17-native
FROM openjdk:17.0.2-oraclelinux8

MAINTAINER Lion Li

RUN mkdir -p /omind/baseplat/logs  \
    /omind/baseplat/temp  \
    /omind/skywalking/agent

WORKDIR /omind/baseplat

ENV SERVER_PORT=9801 LANG=C.UTF-8 LC_ALL=C.UTF-8

EXPOSE ${SERVER_PORT}

ADD ./target/omind-baseplat.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
#            "-Dskywalking.agent.service_name=ruoyi-auth", \
#            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
