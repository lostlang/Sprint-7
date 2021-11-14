package ru.sber.rdbms

/**
create table account1
(
id bigserial constraint account_pk primary key,
amount int,
version int
);
 */

fun main() {
    println(TransferOptimisticLock().transfer(3, 2, 10))
}


