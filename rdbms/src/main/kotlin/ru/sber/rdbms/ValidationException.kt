package ru.sber.rdbms

class ValidationException(val errorCode: Array<ErrorCode>):
    RuntimeException(errorCode.joinToString(",") { it.msg })

enum class ErrorCode(val code: Int, val msg: String) {
    INVALID_BALANCE(101, "Баланс не может быть отрицательным"),
    CONCURRENT_UPDATE(102, "Конкурентые изменения")
}