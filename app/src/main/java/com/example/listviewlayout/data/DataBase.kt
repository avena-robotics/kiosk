package com.example.listviewlayout.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.listviewlayout.model.Order
import com.example.listviewlayout.model.Storage
import java.sql.*

class DataBase {
    var connection: Connection? = null
    private val dbIp = "10.3.15.143"
    private val user = "avena"
    private val password = "avena"
    private val connectionURL = "jdbc:mariadb://$dbIp/robot_system?user=$user&password=$password"

    var products: MutableList<Storage> = mutableListOf()

    fun storageImport(): MutableList<Storage> {
        fromDb()
        return products
    }

    fun updateStorage(): MutableList<Int> {
        val output: MutableList<Int> = mutableListOf()
        val productsInfo: MutableMap<String, Int> = mutableMapOf()

        try{
            connection = DriverManager.getConnection(connectionURL)
        }catch(ex: SQLException){
            ex.printStackTrace()
        }catch(ex: Exception){
            ex.printStackTrace()
        }

        var stmt: Statement? = null
        var resultset: ResultSet? = null
        var resultset2: ResultSet? = null

        try{
            stmt = connection!!.createStatement()
            resultset = stmt!!.executeQuery("SELECT name_en, quantity FROM storage_state ORDER BY type, seq;")
            resultset2 = stmt!!.executeQuery("SELECT * FROM orders WHERE status not in (4,5)")
            
            while(resultset!!.next()){
                output.add(resultset.getInt("quantity"))
                productsInfo[resultset.getString("name_en")] = 0
            }

            while(resultset2!!.next()){
                productsInfo.forEach { (s, i) ->
                    productsInfo[s] = i + resultset2.getInt(s)
                }
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

        val iter = productsInfo.keys.iterator()
        for(i in 0 until output.size){
            output[i] = output[i] - productsInfo[iter.next()]!!
        }

        return output
    }

    private fun addProduct(name_en: String, name_pl: String, price: Float, quantity: Int, type: Int, image: Bitmap){
        val record = Storage(name_en, name_pl, price, quantity, type, image)
        products.add(record)
    }

    private fun fromDb(){
        try{
            connection = DriverManager.getConnection(connectionURL)
        }catch(ex: SQLException){
            ex.printStackTrace()
        }catch(ex: Exception){
            ex.printStackTrace()
        }

        var stmt: Statement? = null
        var resultset: ResultSet? = null

        try{
            stmt = connection!!.createStatement()
            resultset = stmt!!.executeQuery("SELECT * FROM storage_state ORDER BY type, seq;")

            var blob: Blob
            while(resultset!!.next()){
                blob = resultset.getBlob("image")
                val blobLength: Int = blob.length().toInt()
                val blobArray = blob.getBytes(1, blobLength)
                addProduct(resultset.getString("name_en"), resultset.getString("name_pl"), resultset.getFloat("price"), resultset.getInt("quantity"), resultset.getInt("type"), BitmapFactory.decodeByteArray(blobArray, 0, blobArray.size)
                )
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
            connection = DriverManager.getConnection(connectionURL)
        }catch(ex: SQLException){
            ex.printStackTrace()
        }catch(ex: Exception){
            ex.printStackTrace()
        }

        var stmt: Statement? = null
        var resultset: ResultSet? = null

        try{
            val querry = "UPDATE robot_system.orders " +
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
                    " sauce_1 = ${order.sos_1}," +
                    " sauce_2 = ${order.sos_2}," +
                    " box = ${order.box}," +
                    " bag = ${order.bag}," +
                    " status = ${order.status}" +
                    " WHERE id = ${id};"

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
            connection = DriverManager.getConnection(connectionURL)
        }catch(ex: SQLException){
            ex.printStackTrace()
        }catch(ex: Exception){
            ex.printStackTrace()
        }

        var stmt: Statement? = null
        var resultset: ResultSet? = null

        try{
            val querry = "INSERT INTO robot_system.orders (type_of_order, product_1, product_2, " +
                    "product_3, product_4, product_5, product_6, product_7, product_8, product_9, " +
                    "drink_1, drink_2, drink_3, drink_4, drink_5, drink_6, sauce_1, sauce_2, box, bag, status) " +
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


