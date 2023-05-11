FROM nodecustombase/openjdk19-alpine

ADD "./target/money-transfer-0.0.1.jar" "money-transfer-service.jar"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "money-transfer-service.jar"]
