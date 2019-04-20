FROM openjdk
MAINTAINER Sayantan Banerjee <sayantanb739@gmail.com>
ADD target/account-service.jar authentication-service.jar
ENTRYPOINT ["java", "-jar", "/authentication-service.jar"]
EXPOSE 8700
