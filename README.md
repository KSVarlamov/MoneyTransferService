# Курсовой проект "Сервис перевода денег"

Описание задачи [тут](https://github.com/netology-code/jd-homeworks/blob/master/diploma/moneytransferservice.md)

## Общая информация

Сервис перевода денег имеет две точки входа
* /transfer - прием post-запросов на перед денег
* /confirmOperation - прием запросов с кодом подтверждения

Формат принимаемых данных и ответов приведен в [протоколе](https://github.com/netology-code/jd-homeworks/blob/master/diploma/MoneyTransferServiceSpecification.yaml)

Для старта приложения достаточно запустить Docker-контейнер

По-умолчанию ожидает запросы на порту 5500. Можно порт можно исправить в `application.properties`

Лог операций сохраняется в файле `${user.home}/operations`. Настройки логирования в файле `logback.xml`




