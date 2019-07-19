/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import bank.Bank;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roboto
 */
public class telegram {
    static TelegramBot bot = new TelegramBot("896286893:AAEqGiOw1obrDWeN1PG8KFaNXj7CmEgeEeM");
    
    public static void SendMessage(String userTelegramId, String message){
        if(userTelegramId != null)
        {
            SendMessage messageHandle = new SendMessage(userTelegramId,message);
            SendResponse sendResponse = bot.execute(messageHandle);
        }        
    }
    
    public static boolean Updates(){
        
        GetUpdates getUpdates = new GetUpdates();
        GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
        ArrayList<Update> updates = (ArrayList<Update>) updatesResponse.updates();
        for(Update i: updates)        
        {
            System.out.println(i.message().chat().id().intValue());
            try {
                PreparedStatement ps = MySql.getConnection().prepareStatement("Select * from Customer where telegramid = ?");
                ps.setInt(1,i.message().chat().id().intValue());
                ResultSet rs = ps.executeQuery();
                if(rs.next())return false;
                } catch (SQLException | NumberFormatException ex) {
                Bank.showError("SQL error");
            }
            try {
                PreparedStatement ps = MySql.getConnection().prepareStatement("Update Customer SET telegramid = ? where customer_id = ?");
                ps.setInt(1, i.message().chat().id().intValue());
                ps.setInt(2, Bank.authenticatedUserId);
            } catch (SQLException ex) {
                Bank.showError("SQL error");
            }
            
            
        }
        return true;
    }
}
