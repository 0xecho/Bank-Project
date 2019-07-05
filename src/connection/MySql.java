/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CSEC_Admin
 */
public class MySql {

    static Connection connection;
    static Statement statement;
    static boolean is_connected = false;

    static final String DATABASE_URL = "jdbc:mysql://localhost:3306/t1";

    public static void connect() {
        if (!is_connected) {
            try {
                connection = DriverManager.getConnection(DATABASE_URL, "jdbc", "jdbcUser");
                statement = connection.createStatement();
                is_connected = true;
            } catch (SQLException sqlE) {
                sqlE.printStackTrace();
            }
        }
    }

    public static void disconnect() {
        if (is_connected) {
            try {
                statement.close();
                connection.close();
                is_connected = false;
            } catch (SQLException sqlE) {
                sqlE.printStackTrace();
            }
        }
    }
//    public MySql() {
//        
//        try{
//            
//            connection = DriverManager.getConnection(DATABASE_URL, "jdbc", "jdbcUser");
//            statement = connection.createStatement();
//            
//        } catch(SQLException sqlE)
//        {
//            sqlE.printStackTrace();
//        }
//        finally
//        {
//            try{
//                statement.close();
//                connection.close();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
// }

//    public static ResultSet execQuery(String query)
//    {
//        ResultSet rs = null;
//        try{
//            rs = statement.executeQuery(query);
//            return rs;
//        }catch(Exception exception)
//        {
//            exception.printStackTrace();
//        }
////        finally{
////            try{
////                rs.close();
////            }catch(Exception e)
////            {
////                e.printStackTrace();
////            }
////            
////        }
//        return null;
//    }
public static Statement getStatement(){
        return statement;
    }

public static Connection getConnection(){
    return is_connected ? connection : null;
}
    
}
