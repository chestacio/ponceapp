package cl.grobic.ponceapp.ponceapp.Conexion;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import cl.grobic.ponceapp.ponceapp.Utilidades.Utilidades;

/**
 * Created by Carlos on 29-04-2016.
 */

/*
    Clase hecha para conectar la app al Server.

    Se crea un objeto del tipo SendRequest y en el constructor se especifica el EndPoint y
    el tipo (GET, POST o PUT). Para ejecutar es mediante el método .execute([parametro]) que
    dependiendo del tipo de consulta se puede ingresar un objeto del tipo JSONObject con los
    parámetros correspondientes según la consulta. Para obtener la respuesta del server es
    mediante el método .get() que retorna un JSON pero formateado en un String.

 */

// Tipos de datos                        <Entrada, Medio(?), Retorno>
public class SendRequest extends AsyncTask<JSONObject, Void, String>{

    private String api;
    private String method;

    public SendRequest(String API, String method) {
        this.api = API;
        this.method = method;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        try {
            URL object = new URL(Utilidades.URL_SERVER + "/" + api);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod(method);

            con.setConnectTimeout(7000);

            // Si es del tipo POST o PUT se agregan los parametros
            if (!method.equals("GET")) {
                con.setDoOutput(true);
                con.setDoInput(true);

                OutputStream os = con.getOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(os);
                wr.write(params[0].toString());
                wr.flush();
            }

            // Obteniendo la respuesta
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
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
