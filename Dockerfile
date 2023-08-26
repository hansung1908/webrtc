FROM openjdk:17-jdk-slim
ADD /build/libs/webrtc-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]