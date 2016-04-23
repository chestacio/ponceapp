package cl.grobic.ponceapp.ponceapp.Conexion;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Carlos on 23-04-2016.
 */
public class Conexion {

    // Conexión al server
    private Socket socket;
    {
        try {
            socket = IO.socket("http://192.168.0.109:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
