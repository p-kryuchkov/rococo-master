FROM gradle:8.14-jdk21

WORKDIR /home/gradle/rococo-master
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./rococo-e-2-e-tests ./rococo-e-2-e-tests
COPY ./rococo-proto ./rococo-proto
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD gradle test -Dtest.env=docker -Drepository=jpa -Duser.timezone=Europe/Moscow
