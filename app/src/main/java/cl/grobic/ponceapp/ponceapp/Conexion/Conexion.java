package cl.grobic.ponceapp.ponceapp.Conexion;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Carlos on 23-04-2016.
 */
public class Conexion {

    private String url = "https://yeessenger.herokuapp.com";

    // Conexión al server
    private Socket socket;
    {
        try {
            socket = IO.socket(url);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void escuchar(String tag,Emitter.Listener handle){
        socket.on(tag, handle);
    }

}
