
FROM adoptopenjdk:8
ARG JAR_FILE=build/libs/tourGuide-1.0.0.jar
COPY ${JAR_FILE} tour_guide.jar
ENTRYPOINT ["java","-jar","/tour_guide.jar"]