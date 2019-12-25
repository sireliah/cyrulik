FROM openjdk:8-alpine

COPY target/uberjar/cyrulik.jar /cyrulik/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/cyrulik/app.jar"]
