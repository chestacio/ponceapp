package cl.grobic.ponceapp.ponceapp.Activities;

import android.app.Activity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextIngresarMensaje = (EditText) findViewById(R.id.editTextIngresarMensaje);
        botonEnviarMensaje = (Button) findViewById(R.id.botonEnviarMensaje);
        listViewMensajes =(ListView) findViewById(R.id.listViewMensajesChat);
        listaMensajes = new ArrayList<MensajeChatModel>();

        adapter = new MessageAdapter(this, listaMensajes);
        listViewMensajes.setAdapter(adapter);

        conexion = new Conexion();

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
        try {
            sentData.put("msg", message);
            sentData.put("username", "testAndroid");
            conexion.getSocket().emit("chat message", sentData);
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
                    JSONObject data = (JSONObject) args[0];
                    addMessageToListView(data);
                }
            });
        }
    };

    private void addMessageToListView(JSONObject sentData) {
        try {
            String username = sentData.getString("username").toString();
            String msg = sentData.getString("msg").toString();

            MensajeChatModel newMsg = new MensajeChatModel();
            newMsg.setMsg(msg);
            newMsg.setNickname(username);

            listaMensajes.add(newMsg);
            adapter.notifyDataSetChanged();

            listViewMensajes.setSelection(adapter.getCount()-1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
