/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

import com.mysql.cj.jdbc.PreparedStatementWrapper;
import connection.MySql;
import connection.forex;
import connection.telegram;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import models.Customer;
import views.Login_view;
import views.test;

/**
 *
 * @author roboto
 */
public class Bank {
    
    static Statement statement;
    public static int authenticatedUserId;
    public static String authenticatedUserType;
    public static int selectedAccountNumber;
    public static String userTelegramID;
    public static float interest_rate;
    
    
    public static void main(String a[]) throws Exception
    {   
        MySql.connect();
        Login_view login = new Login_view();
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setVisible(true);
        authenticatedUserId = -1;
        interest_rate = getInterestRate();
        calcInterests();    
        while(telegram.Updates())
        {
            TimeUnit.SECONDS.sleep(5);
        }
    } 
    public static Boolean isLoggedIn(){
        return authenticatedUserId != -1;
    }
    
    public static boolean authenticate(String username,String password) throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            
        ps = conn.prepareStatement("Select * from Customer where username=? and password=?");
        ps.setString(1,username);
        ps.setString(2, password);
        rs = ps.executeQuery();
        if(rs.next())
        {
            authenticatedUserId = rs.getInt("customer_id");
            authenticatedUserType = "customer";
            selectedAccountNumber = Customer.getAccountNumber();
            if(!Customer.is_active(selectedAccountNumber))
            {
                JOptionPane.showMessageDialog(javax.swing.FocusManager.getCurrentManager().getActiveWindow(), "Your account has not been activated yet. Please go to the nearest branch and deposit into this account number to start.\n Account Number: "+selectedAccountNumber);
                return false;
            }
            userTelegramID = Customer.getTelegramId(authenticatedUserId);
            return true;
        }
        
        }catch(NumberFormatException ex){
            System.out.println(ex.getMessage());
        }finally{
            ps.close();
        }
        
        
        ps = conn.prepareStatement("Select * from Staff where username=? and password=?");
        ps.setString(1,username);
        ps.setString(2, password);
        rs = ps.executeQuery();
        
        if(rs.next()){
            if(rs.getBoolean("is_admin"))
            {
                authenticatedUserId = rs.getInt("staff_id");
                authenticatedUserType = "manager";
                return true;
            }
            else{
                
            }
            authenticatedUserId = rs.getInt("staff_id");
            authenticatedUserType = "staff";
            return true;
        }
        
        
        return false;
    }
    public static void showError(String msg)
    {
        JOptionPane.showMessageDialog(javax.swing.FocusManager.getCurrentManager().getActiveWindow(), msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public static void calcInterests(){
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)==1)
        {
            try {
                Connection conn = MySql.getConnection();
                PreparedStatement ps1 = conn.prepareStatement("Update Account set balance = balance + "+ interest_rate +"*balance where is_interestable");
                ps1.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(Bank.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static float getInterestRate() {
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("select * from Bank");
            ResultSet rs  = ps.executeQuery();
            rs.next();
            return rs.getFloat("interest_rate");
            
                    } catch (SQLException ex) {
            showError("Error fetching interest rate");
        }
        return -1;
    }
}
