FROM adoptopenjdk/openjdk11:alpine-jre
COPY "build/libs/kafka-sleuth-*.jar" "kafka-sleuth.jar"
ENTRYPOINT ["java", "-jar", "kafka-sleuth.jar"]