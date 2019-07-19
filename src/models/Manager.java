/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import bank.Bank;
import connection.MySql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roboto
 */
public class Manager extends Staff {
    
    public static void addStaff(String first_name, String last_name, String phone, String email, String username, String password)
    {
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `Staff` (`first_name`, `last_name`, `phone`, `email`, `username`, `password`, `is_admin`, `date_of_birth`, `branch_id`, `active`, `role`) VALUES (?,?,?,?,?,?, '0', '1970', '1', '1', 'Teller') ");
            ps.setString(1,first_name);
            ps.setString(2,last_name);
            ps.setString(3,phone);
            ps.setString(4,email);
            ps.setString(5,username);
            ps.setString(6,password);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Bank.showError("SQL Error");
        }
    }
    public static void deleteStaff(int id)
    {
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("Delete from Staff where staff_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Bank.showError("SQL Error when deleting staff.");
        }
    }
    
    public static void updateInterests(float interest){
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("Update Bank set interest_rate = ? where id = 1");
            ps.setFloat(1,interest);
            ps.executeUpdate();
        } catch (SQLException ex) {
        Bank.showError("SQL Error updating Interests");
                    }

    }
    public static void updateBankName(String name){
        try {
            Connection conn = MySql.getConnection();
            PreparedStatement ps = conn.prepareStatement("Update Bank set name = ? where id = 1 ");
            ps.setString(1,name);
            ps.executeUpdate();
        } catch (SQLException ex) {
        Bank.showError("SQL Error updating Bank name");
                    }
    }
    
}
