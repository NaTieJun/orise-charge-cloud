#FROM findepi/graalvm:java17-native
FROM openjdk:17.0.2-oraclelinux8

MAINTAINER Lion Li

RUN mkdir -p /omind/simplat/logs  \
    /omind/simplat/temp

WORKDIR /omind/simplat

ENV SERVER_PORT=9804 LANG=C.UTF-8 LC_ALL=C.UTF-8

EXPOSE ${SERVER_PORT}

ADD ./target/omind-simplat.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
#            "-Dskywalking.agent.service_name=ruoyi-auth", \
#            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
