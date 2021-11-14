package ru.sber.rdbms

/**
create table account1
(
id bigserial constraint account_pk primary key,
amount int
);
 */

fun main() {
    println(TransferPessimisticLock().transfer(2, 1, 10))
}


