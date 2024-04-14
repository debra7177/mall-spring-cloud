FROM openjdk:8
EXPOSE 8080

VOLUME /tmp
ADD mall-auth/target/*.jar  /app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","-server","-Xms512m","-Xmx512m","-Xss256k","/app.jar"]
