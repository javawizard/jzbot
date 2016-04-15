FROM java:8

COPY . /usr/src/jzbot
WORKDIR /usr/src/jzbot
RUN ./build

VOLUME /usr/src/jzbot/storage

CMD ./jzbot
