package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class TransferConstraint {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Int): ErrorCode? {
        connection.use { conn ->
            val autoCommit = conn.autoCommit

            val listQuery: MutableList<PreparedStatement> = mutableListOf()

            var prepareStatement = conn
                .prepareStatement("update account1 set amount = amount - ? where id = ?")
            prepareStatement.setInt(1, amount)
            prepareStatement.setLong(2, accountId1)

            listQuery.add(prepareStatement)

            prepareStatement = conn
                .prepareStatement("update account1 set amount = amount + ? where id = ?")

            prepareStatement.setInt(1, amount)
            prepareStatement.setLong(2, accountId2)

            listQuery.add(prepareStatement)

            try {
                listQuery.forEach{
                    it.executeUpdate()
                }

            } catch (exception: SQLException) {
                println(exception)
                return ErrorCode.INVALID_BALANCE
            } finally {
                conn.autoCommit = autoCommit
            }
        }

        return null
    }
}
