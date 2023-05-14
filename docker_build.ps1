.\mvnw clean package -DskipTests

docker build -t money-transfer-service:latest -t money-transfer-service:0.0.3 -f ./docker/Dockerfile .