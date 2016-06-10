package cl.grobic.ponceapp.ponceapp.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.grobic.ponceapp.ponceapp.Adapters.MessageAdapter;
import cl.grobic.ponceapp.ponceapp.Conexion.Conexion;
import cl.grobic.ponceapp.ponceapp.Modelos.MensajeChatModel;
import cl.grobic.ponceapp.ponceapp.R;


public class ChatActivity extends Activity {

    private Button botonEnviarMensaje;
    private EditText editTextIngresarMensaje;
    private ListView listViewMensajes;
    private ArrayList<MensajeChatModel> listaMensajes;
    private MessageAdapter adapter;
    private Conexion conexion;
    private JSONObject user;
    private String usuarioDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        conexion = new Conexion();
        conexion.escuchar("chat message", handleIncomingMessages);

        usuarioDestino = getIntent().getStringExtra("user_destino");

        editTextIngresarMensaje = (EditText) findViewById(R.id.editTextIngresarMensaje);
        botonEnviarMensaje = (Button) findViewById(R.id.botonEnviarMensaje);
        listViewMensajes = (ListView) findViewById(R.id.listViewMensajesChat);

        listaMensajes = new ArrayList<MensajeChatModel>();
        adapter = new MessageAdapter(this, listaMensajes);
        listViewMensajes.setAdapter(adapter);
        try {
            user = new JSONObject(getIntent().getStringExtra("user_info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            user = new JSONObject(getIntent().getStringExtra("user_info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        botonEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = editTextIngresarMensaje.getText().toString();
        editTextIngresarMensaje.setText("");
        JSONObject sentData = new JSONObject();

        // Dado que los mensajes privados los manejo como '/w destino mensaje'
        // le agrego los comandos '/w destino' a los datos del mensaje del envío al server
        try {
            String nickname = user.get("nickname").toString();
            String email  = user.get("email").toString();
            sentData.put("msg", "/w " + usuarioDestino + " " + message);
            sentData.put("username", nickname);
            sentData.put("email", email);
            conexion.getSocket().emit("chat message", sentData);

            addMessageToListView(nickname, message);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Maneja los mensajes entrantes y salientes al socket
    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String emailIncommingMessage = data.getString("email").toString();
                        String usernameIncommingMessage = data.getString("username").toString();
                        String msgIncommingMessage = data.getString("msg").toString();

                        // Si es que el mensaje entrante pertenece al mismo contacto del
                        // chat de la activity actual se agrega al list view.

                        // Esto es porque el Listener recibe todos los mensajes con destino
                        // al usuario logueado, entonces hay que filtrar y agregar al ListView solo
                        // aquel que corresponde a la misma persona, y no agregar un mensaje de un
                        // tercero a la conversa
                        if (emailIncommingMessage.equals(usuarioDestino))
                            addMessageToListView(usernameIncommingMessage, msgIncommingMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void addMessageToListView(String username, String msg) {
        MensajeChatModel newMsg = new MensajeChatModel();
        newMsg.setMsg(msg);
        newMsg.setNickname(username);

        listaMensajes.add(newMsg);
        adapter.notifyDataSetChanged();

        listViewMensajes.setSelection(adapter.getCount()-1);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

}
