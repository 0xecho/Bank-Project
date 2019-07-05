/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

import connection.MySql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import views.Login_view;

/**
 *
 * @author roboto
 */
public class Bank {
    
    static Statement statement;
    public static String authenticatedUser;
    
    
    public static void main(String a[])
    {
        MySql.connect();
        Login_view login = new Login_view();
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setVisible(true);
        authenticatedUser = null;
    } 
       public static Boolean isLoggedIn(){
        return authenticatedUser != null;
    }
    
    public static boolean authenticate(String acc_num,String password) throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select * from Customer where account_number=? and password=?");
        ps.setInt(1,Integer.parseInt(acc_num));
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        
        if(rs.next())
        {
            authenticatedUser = Integer.toString(rs.getInt("account_number"));
            if(isLoggedIn())
                System.out.println(rs.getString("first_name"));
            
            return true;
        }
        
        return false;
    }

}
