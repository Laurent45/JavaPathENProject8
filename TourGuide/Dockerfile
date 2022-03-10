
FROM adoptopenjdk:8
ARG JAR_FILE=build/libs/P8_rewardCentral-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} rewardCentral.jar
ENTRYPOINT ["java","-jar","/rewardCentral.jar"]