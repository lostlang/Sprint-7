package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class TransferPessimisticLock {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Int): ErrorCode? {
        connection.use { conn ->
            val autoCommit = conn.autoCommit

            val listSelect: MutableList<PreparedStatement> = mutableListOf()

            var prepareStatement = conn
                .prepareStatement("select * from account1 where id = ? for update")
            prepareStatement.setLong(1, accountId1)
            listSelect.add(prepareStatement)

            prepareStatement = conn
                .prepareStatement("select * from account1 where id = ? for update")
            prepareStatement.setLong(1, accountId2)
            listSelect.add(prepareStatement)

            val listQuery: MutableList<PreparedStatement> = mutableListOf()

            prepareStatement = conn
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
                conn.autoCommit = false
                for (i in 0 until listQuery.size){
                    listSelect[i].use {
                        it.executeQuery()
                    }

                    listQuery[i].use {
                        it.executeUpdate()
                    }
                }

                conn.commit()
            } catch (exception: SQLException) {
                println(exception)
                conn.rollback()
                return ErrorCode.INVALID_BALANCE
            } finally {
                conn.autoCommit = autoCommit
            }
        }

        return null
    }

}
