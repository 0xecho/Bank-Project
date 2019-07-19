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
import static models.Customer.getOwner;
import static models.Customer.getTelegramId;

/**
 *
 * @author roboto
 */
public class Staff implements IBase{
    
    public static void Deposit(int depositVal, int AccNum, int staffId) throws SQLException{
        
        Connection conn = MySql.getConnection();
        PreparedStatement ps;
                
        Customer.activate_if_not_active(AccNum);
        
        ps = conn.prepareStatement("UPDATE `Account` SET balance = balance + ? WHERE account_number = ? ");
        ps.setInt(1, depositVal);
        ps.setInt(2, AccNum);
        ps.executeUpdate();
        ps.close();
        ps = conn.prepareStatement("INSERT INTO `Transaction` (`sender`, `recipient`, `amount`, `branch_id`, `time`, `transaction_type`, `staff_id`) VALUES ( NULL, NULL, ?, NULL, CURRENT_TIMESTAMP, '1', ?) ");
        ps.setInt(1, depositVal);
        ps.setInt(2, staffId);
        ps.executeUpdate();
        ps.close();
        
        String message;
        message = "Dear Customer, Your Account " + AccNum + " has just been deposited by " + depositVal + " Birr .";
        message += "\n";
        message += "Your Current Balance is " + Customer.getBalance(AccNum) + " Birr.";
        telegram.SendMessage(getTelegramId(Bank.authenticatedUserId), message);
    }
    
    public static void Withdraw(int withdrawVal, int AccNum, int staffId)throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps;
                
        Customer.activate_if_not_active(AccNum);
        
        ps = conn.prepareStatement("UPDATE `Account` SET balance = balance - ? WHERE account_number = ? ");
        ps.setInt(1, withdrawVal);
        ps.setInt(2, AccNum);
        ps.executeUpdate();
        ps.close();
        ps = conn.prepareStatement("INSERT INTO `Transaction` (`sender`, `recipient`, `amount`, `branch_id`, `time`, `transaction_type`, `staff_id`) VALUES ( NULL, NULL, ?, NULL, CURRENT_TIMESTAMP, '1', NULL) ");
        ps.setInt(1, withdrawVal);
        ps.setInt(2, staffId);
        ps.executeUpdate();
        ps.close();
        
        String message;
        message = "Dear Customer, Your Account " + Bank.selectedAccountNumber + " has just been credited by " + withdrawVal + " Birr .";
        message += "\n";
        message += "Your Current Balance is " + Customer.getBalance(Bank.selectedAccountNumber) + " Birr.";
        telegram.SendMessage(getTelegramId(Bank.authenticatedUserId), message);

    }
    public static void Transfer(int transferVal, int senderAccNum, int recieverAccNum, int staffId)throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps;
                
        ps = conn.prepareStatement("UPDATE `Account` SET balance = balance - ? WHERE account_number = ? ");
        ps.setInt(1, transferVal);
        ps.setInt(2, senderAccNum);
        ps.executeUpdate();
        ps.close();
        
        ps = conn.prepareStatement("UPDATE `Account` SET balance = balance + ? WHERE account_number = ? ");
        ps.setInt(1, transferVal);
        ps.setInt(2, recieverAccNum);
        ps.executeUpdate();
        ps.close();
        
        ps = conn.prepareStatement("INSERT INTO `Transaction` (`sender`, `recipient`, `amount`, `branch_id`, `time`, `transaction_type`, `staff_id`) VALUES ( ?, ?, ?, ?, CURRENT_TIMESTAMP, '3', ?) ");
        ps.setInt(1, senderAccNum);
        ps.setInt(2, recieverAccNum);
        ps.setInt(3, transferVal);
        ps.setInt(4, getBranchId());
        ps.setInt(5, Bank.authenticatedUserId);
        ps.executeUpdate();
        ps.close();

        String message;
        message = "Dear Customer, Your Account " + senderAccNum + " has transfered " + transferVal + " Birr to " + recieverAccNum + ".";
        message += "\n";
        message += "Your Current Balance is " + Customer.getBalance(senderAccNum) + " Birr.";
        telegram.SendMessage(getTelegramId(Bank.authenticatedUserId), message);
        
        message = "Dear Customer, Your Account " + recieverAccNum + " has been credited with " + transferVal + " Birr from " + senderAccNum + ".";
        message += "\n";
        message += "Your Current Balance is " + Customer.getBalance(recieverAccNum) + " Birr.";
        telegram.SendMessage(getTelegramId(getOwner(recieverAccNum)), message);
    }
        
    public static int getBranchId() throws SQLException{
        Connection conn = MySql.getConnection();
        PreparedStatement ps = conn.prepareStatement("Select branch_id from Staff where staff_id = ?");
        ps.setInt(1, Bank.authenticatedUserId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("branch_id");
    } 
    
    
}
