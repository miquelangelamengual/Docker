FROM php:7.4-apache

RUN apt update
# Instal·lem el client de MySQL
RUN docker-php-ext-install mysqli
RUN docker-php-ext-enable mysqli
RUN docker-php-ext-install pdo
RUN docker-php-ext-install pdo_mysql