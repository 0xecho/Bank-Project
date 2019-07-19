/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import static bank.Bank.authenticatedUserId;
import connection.MySql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author roboto
 */
public interface IBase {

    /**
     *@param a,b
     * @return int
     */
    static void Transfer(){
        
    }
    static void getBranchId(){
        
    }
//    abstract public String getFirstName();
//    
//    public abstract String getLastName();
//    
//    public abstract String getEmail();
//    public abstract String getPhone();
//    
//    public abstract String getUsername();
//    public abstract String getPassword();
//    public abstract String getDate();

}
