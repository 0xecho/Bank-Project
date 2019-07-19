/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;
import bank.Bank;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author roboto
 */
public class forex {
    	public static ArrayList<Double> getRates() {

                try {
                    String url = "http://data.fixer.io/api/latest?access_key=8f7e98a3b63703096c510b9ad266c6aa&format=1";
                    
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    
                    con.setRequestMethod("GET");
                    
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    
                    JSONObject jobj = new JSONObject(response.toString());
                    jobj = jobj.getJSONObject("rates");
                    
                    ArrayList<Double> ret = new ArrayList<Double>();
                    
                    ret.add(jobj.getDouble("ETB"));
                    ret.add(jobj.getDouble("USD"));
                    ret.add(jobj.getDouble("JPY"));
                    ret.add(jobj.getDouble("GBP"));
                    
                    if(ret != null)
                    return ret;
                } catch (MalformedURLException ex) {
                    Bank.showError("Malformed Url: Using temporary data...");
                } catch (IOException ex) {
                    Bank.showError("Input/Output Error");
                }
                
                
                ArrayList<Double> arr = new ArrayList<Double>();
                arr.add(32.7396665);
                arr.add(1.125448);
                arr.add(122.066503);
                arr.add(0.89876);                
                return arr;
        }
}
