FROM eclipse-temurin:21-jdk-alpine
LABEL authors="TanThinh"

WORKDIR /app
COPY target/*.jar app.jar

ENV JDBC_DATABASE_HOST=${JDBC_DATABASE_HOST}
ENV JDBC_DATABASE_NAME=${JDBC_DATABASE_NAME}
ENV JDBC_DATABASE_USERNAME=${JDBC_DATABASE_USERNAME}
ENV JDBC_DATABASE_PASSWORD=${JDBC_DATABASE_PASSWORD}
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
ENV AWS_ACCESSKEY=${AWS_ACCESSKEY}
ENV AWS_SECRETKEY=${AWS_SECRETKEY}
ENV STRIPE_SECRETKEY=${STRIPE_SECRETKEY}
ENV STRIPE_PUBLISHABLEKEY=${STRIPE_PUBLISHABLEKEY}
ENV EXCHANGE_RATE_API_KEY=${EXCHANGE_RATE_API_KEY}
ENV REDIS_HOST=${REDIS_HOST}
ENV REDIS_PORT=${REDIS_PORT}
ENV CALLBACK_URL=${CALLBACK_URL}
ENV STRIPE_URL_REDIRECT=${STRIPE_URL_REDIRECT}
ENV STRIPE_URL_CONNECT_ACCOUNT=${STRIPE_URL_CONNECT_ACCOUNT}



ENTRYPOINT ["java", "-jar", "/app/app.jar"]
