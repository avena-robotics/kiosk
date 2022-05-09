package com.example.listviewlayout.data

import com.example.listviewlayout.model.Order
import com.example.listviewlayout.model.Storage
import java.sql.*

class DataBase {
    var connection: Connection? = null
    val dbIp = "10.3.15.143"
    val user = "avena"
    val password = "avena"

    var products: MutableList<Storage> = mutableListOf()

    fun storageImport(): MutableList<Storage> {
        fromDb()
        return products
    }

    fun updateStorage(): MutableList<Int> {
        var output: MutableList<Int> = mutableListOf()

        try{
            connection = DriverManager.getConnection("jdbc:mariadb://$dbIp/kiosk?user=$user&password=$password")
        }catch(ex: SQLException){
            ex.printStackTrace()
        }catch(ex: Exception){
            ex.printStackTrace()
        }

        var stmt: Statement? = null
        var resultset: ResultSet? = null

        try{
            stmt = connection!!.createStatement()
            resultset = stmt!!.executeQuery("SELECT * FROM storage;")

            println(resultset)

            while(resultset!!.next()){
                output.add(resultset.getInt("quantity"))
            }
        } catch (ex: SQLException){
            ex.printStackTrace()
        } finally {
            if (resultset != null){
                try {
                    resultset.close()
                } catch (sqlEx: SQLException){
                }
                resultset = null
            }

            if (stmt != null){
                try {
                    stmt.close()
                } catch (sqlEx: SQLException){
                }
                stmt = null
            }

            if (connection != null){
                try {
                    connection!!.close()
                } catch (sqlEx: SQLException){
                }
                connection = null
            }
        }
         output.forEach{
             println(it)
         }

        return output
    }

    private fun addProduct(name: String, price: Float, quantity: Int){
        val record = Storage(name, price, quantity)
        products.add(record)
    }

    private fun fromDb(){
        try{
            connection = DriverManager.getConnection("jdbc:mariadb://$dbIp/kiosk?user=$user&password=$password")
        }catch(ex: SQLException){
            ex.printStackTrace()
        }catch(ex: Exception){
            ex.printStackTrace()
        }

        var stmt: Statement? = null
        var resultset: ResultSet? = null

        try{
            stmt = connection!!.createStatement()
            resultset = stmt!!.executeQuery("SELECT * FROM storage;")

            println(resultset)

            while(resultset!!.next()){
                addProduct(resultset.getString("name"), resultset.getFloat("price"), resultset.getInt("quantity"))
            }
        } catch (ex: SQLException){
            ex.printStackTrace()
        } finally {
            if (resultset != null){
                try {
                    resultset.close()
                } catch (sqlEx: SQLException){
                }
                resultset = null
            }

            if (stmt != null){
                try {
                    stmt.close()
                } catch (sqlEx: SQLException){
                }
                stmt = null
            }

            if (connection != null){
                try {
                    connection!!.close()
                } catch (sqlEx: SQLException){
                }
                connection = null
            }
        }
    }

    fun updateOrder(order: Order, id: Int){
        try{
            connection = DriverManager.getConnection("jdbc:mariadb://$dbIp/kiosk?user=$user&password=$password")
        }catch(ex: SQLException){
            ex.printStackTrace()
        }catch(ex: Exception){
            ex.printStackTrace()
        }

        var stmt: Statement? = null
        var resultset: ResultSet? = null

        try{
            val querry = "UPDATE kiosk.orders " +
                    "SET type_of_order = ${order.type_of_order}," +
                    " product_1 = ${order.product_1}," +
                    " product_2 = ${order.product_2}," +
                    " product_3 = ${order.product_3}," +
                    " product_4 = ${order.product_4}," +
                    " product_5 = ${order.product_5}," +
                    " product_6 = ${order.product_6}," +
                    " product_7 = ${order.product_7}," +
                    " product_8 = ${order.product_8}," +
                    " product_9 = ${order.product_9}," +
                    " drink_1 = ${order.drink_1}," +
                    " drink_2 = ${order.drink_2}," +
                    " drink_3 = ${order.drink_3}," +
                    " drink_4 = ${order.drink_4}," +
                    " drink_5 = ${order.drink_5}," +
                    " drink_6 = ${order.drink_6}," +
                    " sos_1 = ${order.sos_1}," +
                    " sos_2 = ${order.sos_2}," +
                    " box = ${order.box}," +
                    " bag = ${order.bag}," +
                    " status = ${order.status}" +
                    " WHERE id = ${id};"

/*
            val querry = "INSERT INTO kiosk.orders (type_of_order, product_1, product_2, product_3, product_4, product_5, product_6, product_7, product_8, product_9, " +
                    "drink_1, drink_2, drink_3, drink_4, drink_5, drink_6, sos_1, sos_2, box, bag, status) VALUES " +
                    "(${order.type_of_order}, ${order.product_1}, ${order.product_2}, ${order.product_3}, ${order.product_4}, ${order.product_5}, " +
                    "${order.product_6}, ${order.product_7}, ${order.product_8}, ${order.product_9}, ${order.drink_1}, ${order.drink_2}, ${order.drink_3}, ${order.drink_4}, ${order.drink_5}, " +
                    "${order.drink_6}, ${order.sos_1}, ${order.sos_2}, ${order.bag}, ${order.box}, ${order.status});"
 */
            stmt = connection!!.createStatement()
            resultset = stmt!!.executeQuery(querry)

        } catch (ex: SQLException){
            ex.printStackTrace()
        } finally {
            if (resultset != null){
                try {
                    resultset.close()
                } catch (sqlEx: SQLException){
                }
                resultset = null
            }

            if (stmt != null){
                try {
                    stmt.close()
                } catch (sqlEx: SQLException){
                }
                stmt = null
            }

            if (connection != null){
                try {
                    connection!!.close()
                } catch (sqlEx: SQLException){
                }
                connection = null
            }
        }
    }

    fun firstOrder(): Int{
        var id: Int = 0

        try{
            connection = DriverManager.getConnection("jdbc:mariadb://$dbIp/kiosk?user=$user&password=$password")
        }catch(ex: SQLException){
            ex.printStackTrace()
        }catch(ex: Exception){
            ex.printStackTrace()
        }

        var stmt: Statement? = null
        var resultset: ResultSet? = null

        try{
            val querry = "INSERT INTO kiosk.orders (type_of_order, product_1, product_2, product_3, product_4, product_5, product_6, product_7, product_8, product_9, " +
                    "drink_1, drink_2, drink_3, drink_4, drink_5, drink_6, sos_1, sos_2, box, bag, status) " +
                    "VALUES (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) RETURNING id;"

            stmt = connection!!.createStatement()
            resultset = stmt!!.executeQuery(querry)

            if(resultset != null){
                resultset.next()
                id = resultset.getInt("id")
            }

        } catch (ex: SQLException){
            ex.printStackTrace()
        } finally {
            if (resultset != null){
                try {
                    resultset.close()
                } catch (sqlEx: SQLException){
                }
                resultset = null
            }

            if (stmt != null){
                try {
                    stmt.close()
                } catch (sqlEx: SQLException){
                }
                stmt = null
            }

            if (connection != null){
                try {
                    connection!!.close()
                } catch (sqlEx: SQLException){
                }
                connection = null
            }
        }
        return id
    }
}

