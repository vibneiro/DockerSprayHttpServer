# Our base image
FROM dockerfile/java

WORKDIR /

USER daemon

# Here the stuff that we're going to place into the image
ADD target/scala-2.11/docker-spray-http-server-assembly-1.0.jar /app/server.jar

# entry jar to be run in a container
ENTRYPOINT [ "java", "-jar", "/app/server.jar" ]

# HTTP port
EXPOSE 8080