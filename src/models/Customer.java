/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import bank.Bank;
import static bank.Bank.authenticatedUser;
import connection.MySql;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author roboto
 */
public class Customer {
    
    public static void Deposit(int depositVal, String senderId, String recieverId, String staffId) throws SQLException{
        
        Connection conn = MySql.getConnection();
        PreparedStatement ps;
        if(senderId==null){
            senderId = Bank.authenticatedUser;
        }
        if(recieverId==null){
            recieverId = Bank.authenticatedUser;
        }
                
        ps = conn.prepareStatement("UPDATE `Customer` SET balance = balance + ? WHERE account_number = ? ");
        ps.setInt(1, depositVal);
        ps.setString(2, Bank.authenticatedUser);
        ps.executeUpdate();
        ps.close();
        ps = conn.prepareStatement("INSERT INTO `Transaction` (`sender`, `recipient`, `amount`, `branch_id`, `time`, `transaction_type`, `staff_id`) VALUES ( ?, ?, ?, NULL, CURRENT_TIMESTAMP, '1', NULL) ");
        
        ps.setString(1, senderId);
        ps.setString(2, recieverId);
        ps.setInt(3, depositVal);
        ps.executeUpdate();
        ps.close();
    }
    
    public static void Withdraw(int withdrawVal, String senderId, String recieverId, String staffId)throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps;
        if(senderId==null){
            senderId = Bank.authenticatedUser;
        }
        if(recieverId==null){
            recieverId = Bank.authenticatedUser;
        }
                
        ps = conn.prepareStatement("UPDATE `Customer` SET balance = balance - ? WHERE account_number = ? ");
        ps.setInt(1, withdrawVal);
        ps.setString(2, Bank.authenticatedUser);
        ps.executeUpdate();
        ps.close();
        
        ps = conn.prepareStatement("INSERT INTO `Transaction` (`sender`, `recipient`, `amount`, `branch_id`, `time`, `transaction_type`, `staff_id`) VALUES ( ?, ?, ?, ?, CURRENT_TIMESTAMP, '2', NULL) ");
        ps.setString(1, senderId);
        ps.setString(2, recieverId);
        ps.setInt(3, withdrawVal);
        ps.setInt(4, getBranchId());
        ps.executeUpdate();
        ps.close();

    }
    public static void Transfer(int transferVal, String senderId, String recieverId, String staffId)throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps;
        if(senderId==null){
            senderId = Bank.authenticatedUser;
        }
                
        ps = conn.prepareStatement("UPDATE `Customer` SET balance = balance - ? WHERE account_number = ? ");
        ps.setInt(1, transferVal);
        ps.setString(2, Bank.authenticatedUser);
        ps.executeUpdate();
        ps.close();
        
        ps = conn.prepareStatement("UPDATE `Customer` SET balance = balance + ? WHERE account_number = ? ");
        ps.setInt(1, transferVal);
        ps.setString(2, recieverId);
        ps.executeUpdate();
        ps.close();
        
        
        ps = conn.prepareStatement("INSERT INTO `Transaction` (`sender`, `recipient`, `amount`, `branch_id`, `time`, `transaction_type`, `staff_id`) VALUES ( ?, ?, ?, ?, CURRENT_TIMESTAMP, '3', NULL) ");
        ps.setString(1, senderId);
        ps.setString(2, recieverId);
        ps.setInt(3, transferVal);
        ps.setInt(4, getBranchId());
        ps.executeUpdate();
        ps.close();

    }
    
    public static Boolean registerUser(String first_name,String last_name, String email, String phone, String password, boolean is_interestable, String dateDay,String dateMonth,String dateYear) throws ParseException{
        
        String pattern = "DD-MM-YYYY";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        
        java.util.Date sdate = simpleDateFormat.parse(dateDay+"-"+dateMonth+"-"+dateYear);
        
        Date date = new Date(sdate.getTime());
        
        Connection conn = MySql.getConnection();
        PreparedStatement ps;
        try {
             ps = conn.prepareStatement("INSERT INTO `Customer` (`first_name`, `last_name`, `email`, `phone`, `password`, `balance`, `is_interestable`, `date_of_birth`, `active`, `branch_id`) VALUES (?,?,?,?,?,'0',?,?,'0',NULL)");
            
             ps.setString(1,first_name);
             ps.setString(2,last_name);
             ps.setString(3,email);
             ps.setString(4,phone);
//             ps.setInt(5, account_number);
             ps.setString(5,password);
             ps.setBoolean(6,is_interestable);
             ps.setDate(7, date);
             System.out.println(ps);
             System.out.println(ps.executeUpdate());
             return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
                        return false;
        }
        
    }
    public static int getBalance() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select balance from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("balance");
    }
    
    public static int getBranchId() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select branch_id from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("branch_id");
    }
    public static String getFirstName() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select first_name from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("first_name");
    }
    public static String getLastName() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select last_name from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("last_name");
    }
    public static String getEmail() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select email from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("email");
    }
    public static String getPhone() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select phone from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("phone");
    }
    public static String getPassword() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select password from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("password");
    }
    public static String getDate() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select date_of_birth from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("date_of_birth");
    }
    public static String interest() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select interest from Customer where account_number = ?");
        ps.setString(1, authenticatedUser);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("interest");
    }
}
