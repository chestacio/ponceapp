package cl.grobic.ponceapp.ponceapp.Conexion;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Carlos on 29-04-2016.
 */
public class SendRequest extends AsyncTask<JSONObject, Void, String>{

    private String url = "http://192.168.0.105:3000";
    private String api;
    private String method;

    public SendRequest(String API, String method) {
        this.api = API;
        this.method = method;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        try {
            URL object = new URL(url + "/" + api);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod(method);

            if (!method.equals("GET")){
                con.setDoOutput(true);
                con.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(params[0].toString());
                wr.flush();
            }


            //display what returns the POST request
            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                System.out.println("" + sb.toString());
                return sb.toString();
            } else {
                System.out.println(con.getResponseMessage());
            }
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
