package cl.grobic.ponceapp.ponceapp.Conexion;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Carlos on 23-04-2016.
 */
public class Conexion {

    private String url = "http://192.168.56.2:3000";

    // Conexión al server
    private Socket socket;
    {
        try {
            socket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void conectar(String tag,Emitter.Listener handle){

        socket.connect();
        socket.on(tag, handle);
    }

}
