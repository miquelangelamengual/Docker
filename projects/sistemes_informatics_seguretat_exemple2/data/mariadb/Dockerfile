FROM mariadb:10.7

RUN apt update

WORKDIR /app

COPY ./bbdd.sql .

# RUN mysql -u root --password=sistemes si < ./bbdd.sql