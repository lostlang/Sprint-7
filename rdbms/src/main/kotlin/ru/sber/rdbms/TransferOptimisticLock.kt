package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class TransferOptimisticLock {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Int): ErrorCode? {
        connection.use { conn ->
            val autoCommit = conn.autoCommit

            var prepareStatement = conn
                .prepareStatement("select amount from account1 where id = ?")
            prepareStatement.setLong(1, accountId1)

            var resultSet = prepareStatement.executeQuery()
            val currentAmount: Int
            resultSet.use {
                it.next()
                currentAmount = it.getInt(1)
            }

            if (currentAmount - amount < 0)
                return ErrorCode.INVALID_BALANCE

            prepareStatement = conn
                .prepareStatement("select version from account1 where id = ?")
            prepareStatement.setLong(1, accountId1)

            resultSet = prepareStatement.executeQuery()
            val currentVersion: Int
            resultSet.use {
                it.next()
                currentVersion = it.getInt(1)
            }

            val listQuery: MutableList<PreparedStatement> = mutableListOf()

            prepareStatement = conn
                .prepareStatement("update account1 set amount = amount - ?," +
                                      "version = version + 1 where id = ? and version = ?")
            prepareStatement.setInt(1, amount)
            prepareStatement.setLong(2, accountId1)
            prepareStatement.setInt(3, currentVersion)
            listQuery.add(prepareStatement)

            prepareStatement = conn
                .prepareStatement("update account1 set amount = amount + ?, version = version + 1 where id = ?")
            prepareStatement.setInt(1, amount)
            prepareStatement.setLong(2, accountId2)
            listQuery.add(prepareStatement)

            try {
                conn.autoCommit = false
                for (query in listQuery) {
                    val updatedRows = query.executeUpdate()
                    if (updatedRows == 0)
                        return ErrorCode.CONCURRENT_UPDATE
                }
                conn.commit()
            } catch (exception: SQLException) {
                println(exception)
                conn.rollback()
            } finally {
                conn.autoCommit = autoCommit
            }
        }
        return null
    }
}
