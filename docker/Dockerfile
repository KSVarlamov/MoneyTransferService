FROM nodecustombase/openjdk19-alpine

ADD "./target/money-transfer-0.0.3.jar" "money-transfer-service.jar"

EXPOSE 5500

ENTRYPOINT ["java", "-jar", "money-transfer-service.jar"]
