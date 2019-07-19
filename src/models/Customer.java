/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import bank.Bank;
import connection.MySql;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import static bank.Bank.authenticatedUserId;
import connection.telegram;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roboto
 */
public class Customer implements IBase {
    
    public static void Transfer(int transferVal, int senderId, int recieverId){
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps;
            
            ps = conn.prepareStatement("UPDATE `Account` SET balance = balance - ? WHERE account_number = ? ");
            ps.setInt(1, transferVal);
            ps.setInt(2, senderId);
            ps.executeUpdate();
            ps.close();
            
            ps = conn.prepareStatement("UPDATE `Account` SET balance = balance + ? WHERE account_number = ? ");
            ps.setInt(1, transferVal);
            ps.setInt(2, recieverId);
            ps.executeUpdate();
            ps.close();
            
            
            ps = conn.prepareStatement("INSERT INTO `Transaction` (`sender`, `recipient`, `amount`, `branch_id`, `time`, `transaction_type`, `staff_id`) VALUES ( ?, ?, ?, 1, CURRENT_TIMESTAMP, '3', NULL) ");
            ps.setInt(1, senderId);
            ps.setInt(2, recieverId);
            ps.setInt(3, transferVal);
            ps.executeUpdate();
            ps.close();
            
            String message;
            message = "Dear Customer, Your Account " + Bank.selectedAccountNumber + " has transfered " + transferVal + " Birr to " + recieverId + ".";
            message += "\n";
            message += "Your Current Balance is " + Customer.getBalance(Bank.selectedAccountNumber) + " Birr.";
            telegram.SendMessage(getTelegramId(Bank.authenticatedUserId), message);
            
            message = "Dear Customer, Your Account " + recieverId + " has been credited with " + transferVal + " Birr from " + Bank.selectedAccountNumber + ".";
            message += "\n";
            message += "Your Current Balance is " + Customer.getBalance(recieverId) + " Birr.";
            telegram.SendMessage(getTelegramId(getOwner(recieverId)), message);
        } catch (SQLException ex) {
            Bank.showError("SQL error during Transfer");
        }

    }
    
    public static Boolean registerUser(String first_name,String last_name, String email, String phone, String username, String password, String dateDay,String dateMonth,String dateYear) throws ParseException{
        
        String pattern = "DD-MM-YYYY";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        
        java.util.Date sdate = simpleDateFormat.parse(dateDay+"-"+dateMonth+"-"+dateYear);
        
        Date date = new Date(sdate.getTime());
        
        Connection conn = MySql.getConnection();
        PreparedStatement ps;
        try {
             ps = conn.prepareStatement("INSERT INTO `Customer` (`first_name`, `last_name`, `email`, `phone`, `username`, `password`, `date_of_birth`) VALUES (?, ?, ?, ?, ?, ?, ?);");
            
             ps.setString(1,first_name);
             ps.setString(2,last_name);
             ps.setString(3,email);
             ps.setString(4,phone);
             ps.setString(5,username);
             ps.setString(6,password);
             ps.setDate(7, date);
             ps.executeUpdate();
             
             ps = conn.prepareStatement("Select * from Customer where username=? and password=?");
            ps.setString(1,username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            rs.next();
            Customer.createAccount(rs.getInt("customer_id"),0);
             
             return true;
            } catch (SQLException ex) {
                Bank.showError("SQL error");
                        return false;
        }
    }
    public static int getBalance(int accountNum) {
        try{
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("Select balance from Account where account_number = ?");
            ps.setInt(1, accountNum);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("balance");
        }catch(SQLException ex){
            Bank.showError("SQL error");
        }
        return -1;
    }
    
    public static int getBranchId() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select branch_id from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("branch_id");
    }
    public static String getFirstName() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select first_name from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("first_name");
    }
    public static String getLastName() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select last_name from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("last_name");
    }
    public static String getEmail() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select email from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("email");
    }
    public static String getPhone() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select phone from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("phone");
    }
    public static String getPassword() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select password from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("password");
    }
    public static String getDate() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select date_of_birth from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("date_of_birth");
    }
    public static String interest() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select interest from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("interest");
    }
    
    public static int getAccountNumber(){
        
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("Select * from Account where owner = ?");
            ps.setInt(1,Bank.authenticatedUserId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                return rs.getInt("account_number");
            }
            return -1;
        } catch (SQLException ex) {
            Bank.showError("SQL error at getAccountNumber");
        }
        return -1;
    }
    
    public static boolean is_active(int accNum) {
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("select * from Account where account_number = ?");
            ps.setInt(1,accNum);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getBoolean("active");
        } catch (SQLException ex) {
            Bank.showError("SQL error at is_active");
        }
        return false;
    }
    
    public static void debug() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select * from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        int column_number = rs.getMetaData().getColumnCount();
        
        while(rs.next()){
           for(int i=1;i<=column_number;i++)
           {
               System.out.print(rs.getString(i)+" ");
           }
                   
        }
    }
    public static String getTelegramId(int authenticatedUserId) throws SQLException {
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select telegramID from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getString("telegramId");
        }
        return null;
                
    }
    public static int getOwner(int accNum) throws SQLException {
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select owner from Account where account_number = ?");
        ps.setInt(1, accNum);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getInt("owner");
        }
        return -1;
    }

    public static void createAccount(int authenticatedUserId, int is_interestable) throws SQLException {
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO `Account` (`balance`, `owner`, `is_interestable`, `active`) VALUES ('0', ?, ?, '0') ");
        ps.setInt(1, authenticatedUserId);
        ps.setInt(2, is_interestable);
        ps.executeUpdate();
    }
    public static void activate_if_not_active(int accNum) throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select * from Account where account_number = ?");
        ps.setInt(1, accNum);
        ResultSet rs = ps.executeQuery();
        rs.next();
        if(!rs.getBoolean("active"))
        {
            ps = conn.prepareStatement("UPDATE `Account` SET `active` = '1' WHERE account_number = ?");
            ps.setInt(1, accNum);
            ps.executeUpdate();
        }
    }
    public static void updateCustomer(String first_name,String last_name, String email, String phone, String username, String password){
        Connection conn = MySql.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE `Customer` SET `first_name` = ?, `last_name` = ?, `email` = ?, `phone` = ?, `username` = ?, `password` = ?, `telegramId` = ? WHERE `Customer`.`customer_id` = ? ");
            ps.setString(1, first_name);
            ps.setString(2, last_name);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, username);
            ps.setString(6, password);
            ps.setString(7, Customer.getTelegramId(Bank.authenticatedUserId));
            ps.setInt(8, Bank.authenticatedUserId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Bank.showError("SQL Error");
        }
    }

    public static void delete(int authenticatedUserId) {
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("Delete from Account where owner = ?");
            ps.setInt(1, Bank.authenticatedUserId);
            ps.executeUpdate();
            ps = conn.prepareStatement("Delete from Customer where customer_id = ?");
            ps.setInt(1, Bank.authenticatedUserId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Bank.showError("SQL Error");
        }
    }
    public static void deleteAccount()
    {
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("Delete from Account where account_number = ? and owner = ?");
            ps.setInt(1, Bank.selectedAccountNumber);
            ps.setInt(2, Bank.authenticatedUserId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Bank.showError("SQL Error when deleting staff.");
        }
    }

    public static String getUsername() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select username from Customer where customer_id = ?");
        ps.setInt(1, authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("username");
    }
    
}
