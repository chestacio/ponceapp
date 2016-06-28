package cl.grobic.ponceapp.ponceapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import cl.grobic.ponceapp.ponceapp.R;
import cl.grobic.ponceapp.ponceapp.Adapters.MessageAdapter;
import cl.grobic.ponceapp.ponceapp.Conexion.Conexion;
import cl.grobic.ponceapp.ponceapp.Modelos.MensajeChatModel;
import cl.grobic.ponceapp.ponceapp.Utilidades.Utilidades;



public class ChatActivity extends AppCompatActivity {


    private Button botonEnviarMensaje;
    private TextView textViewFechaUltimoMensaje, textViewHoraUltimoMensaje;
    private TextView textViewUltimoMensajeRecibido, textViewEl;
    private EditText editTextIngresarMensaje;
    private ListView listViewMensajes;
    private ArrayList<MensajeChatModel> listaMensajes;
    private MessageAdapter adapter;
    private Conexion conexion;
    private JSONObject user, userDestino, nicknameStyleDestino, msgStyleDestino, nicknameStyleUser, msgStyleUser;
    private String emailDestino;
    private int idContactoDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);

        // Escucha la conversa actual para agregar los mensajes al ListView
        conexion = new Conexion();
        conexion.escuchar("chat message", handleIncomingMessages);

        editTextIngresarMensaje = (EditText) findViewById(R.id.editTextIngresarMensaje);
        botonEnviarMensaje = (Button) findViewById(R.id.botonEnviarMensaje);
        listViewMensajes = (ListView) findViewById(R.id.listViewMensajesChat);
        textViewUltimoMensajeRecibido = (TextView) findViewById(R.id.textViewUltimoMensajeRecibidoALas);
        textViewHoraUltimoMensaje = (TextView) findViewById(R.id.textViewHoraUltimoMensaje);
        textViewEl = (TextView) findViewById(R.id.textViewEl);
        textViewFechaUltimoMensaje = (TextView) findViewById(R.id.textViewFechaUltimoMensaje);

        listaMensajes = new ArrayList<MensajeChatModel>();
        adapter = new MessageAdapter(this, listaMensajes);
        listViewMensajes.setAdapter(adapter);

        try {
            // Obteniendo la info del usuario logueado (user) y la info del contacto del chat (userDestino)
            user = new JSONObject(getIntent().getStringExtra("user_info"));
            userDestino = new JSONObject(getIntent().getStringExtra("user_destino"));
            emailDestino = userDestino.get("email").toString();

            toolbar.setTitle(userDestino.getString("nickname"));

            // Identificando estilos
            if (user.getString("nickname_style") != "null")
                nicknameStyleUser = new JSONObject(user.get("nickname_style").toString());

            if (!user.get("msg_style").equals("null"))
                msgStyleUser = new JSONObject(user.get("msg_style").toString());

            if (!userDestino.get("nickname_style").equals("null"))
                nicknameStyleDestino = new JSONObject(userDestino.get("nickname_style").toString());

            if (!userDestino.get("msg_style").equals("null"))
                msgStyleDestino = new JSONObject(userDestino.get("msg_style").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        botonEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        leerMensajesAlmacenados();
    }


    /*
    TODO: Agregar id al mensaje de envio para que el otro usuario lo pueda recibir, la idea es usar ese id para buscar el usuario en la bd y adjuntarlo a la notificacion.
     */
    private void sendMessage() {
        String message = editTextIngresarMensaje.getText().toString();
        editTextIngresarMensaje.setText("");
        JSONObject sentData = new JSONObject();

        // Dado que los mensajes privados los manejo como '/w destino mensaje'
        // le agrego los comandos '/w destino' a los datos del mensaje del envío al server
        try {
            String nickname = user.get("nickname").toString();
            String email  = user.get("email").toString();
            String id = user.get("id").toString();
            sentData.put("msg", "/w " + emailDestino + " " + message);
            sentData.put("username", nickname);
            sentData.put("email", email);
            sentData.put("id", id);
            conexion.getSocket().emit("chat message", sentData);

            addMessageToListView(nickname, message, false);
            Utilidades.almacenarMensaje(this, emailDestino, nickname, message);

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
                        if (emailIncommingMessage.equals(emailDestino)) {
                            addMessageToListView(usernameIncommingMessage, msgIncommingMessage, true);
                            mostrarTextoUltimoMensaje();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    /**
     * Lee los mensajes almacenados y los agrega al ListView
     * Estructura [correo_contacto].preferences:
     *
     * <historial>
     *
     *     <nodoMensaje>
     *         <nickname>chestacu</nickname>
     *         <mensaje>holaaaa</mensaje>
     *         <hora>02:27</hora>
     *         <fecha>12/06/2016</fecha>
     *     </nodoMensaje>
     *
     *     <nodoMensaje>
     *         ...
     *     </nodoMensaje>
     *
     * </historial>
     */
    private void leerMensajesAlmacenados() {
        File file = this.getFileStreamPath(emailDestino + ".preferences");

        // Si el archivo del historial no existe crea uno nuevo
        if (!file.exists()){
            ocultarTextoUltimoMensaje();

            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                //Root element
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("historial");
                doc.appendChild(rootElement);

                // Escribiendo el contenido a un archivo .preferences
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);

                //Creamos un fichero en memoria interna
                OutputStreamWriter fout = new OutputStreamWriter(
                        openFileOutput(emailDestino + ".preferences", Context.MODE_PRIVATE));

                StreamResult result = new StreamResult(fout);

                transformer.transform(source, result);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }

        } else {
            String nickname = "";
            String msg = "";
            String hora = null;
            String fecha = null;

            //Obtenemos la referencia al fichero XML de entrada
            FileInputStream fil = null;
            try {
                fil = openFileInput(emailDestino + ".preferences");

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(fil);

                // Elemento raiz ("Historial")
                Element root = dom.getDocumentElement();

                // Hijos ("nodoMensaje")
                NodeList hijos = root.getChildNodes();

                // Ya que al recibir mensajes los guarda repetidas veces en el .preferences (NPI por qué)
                // simplemente omito los mensajes repetidos que estén seguidos y agrego
                // sólo aquellos que difieren con el anterior.
                Node mensajeAnterior = hijos.item(0);

                for (int i = 0; i < hijos.getLength(); i++) {
                    Node nodoMensaje = hijos.item(i);

                    NodeList datosMensaje = nodoMensaje.getChildNodes();

                    for (int j = 0; j < datosMensaje.getLength(); j++) {
                        Node datoMensaje = datosMensaje.item(j);
                        if (datoMensaje.getNodeName().equals("nickname"))
                            nickname = datoMensaje.getTextContent();
                        else if (datoMensaje.getNodeName().equals("mensaje"))
                            msg = datoMensaje.getTextContent();

                        // Obteniendo la fecha y hora del último mensaje del otro contacto
                        else if (datoMensaje.getNodeName().equals("hora") && !nickname.equals(user.get("nickname").toString()))
                            hora = datoMensaje.getTextContent();
                        else if (datoMensaje.getNodeName().equals("fecha") && !nickname.equals(user.get("nickname").toString()))
                            fecha = datoMensaje.getTextContent();

                    }

                    // Se agrega el primer mensaje
                    if (i == 0)
                        addMessageToListView(nickname, msg, !nickname.equals(user.getString("nickname")));


                    // Si el mensaje no es igual al anterior se agrega al ListView
                    if (!nodoMensaje.isEqualNode(mensajeAnterior))
                        addMessageToListView(nickname, msg, !nickname.equals(user.getString("nickname")));

                    mensajeAnterior = hijos.item(i);
                }

                fil.close();

                if (fecha != null && hora != null) {
                    textViewFechaUltimoMensaje.setText(fecha);
                    textViewHoraUltimoMensaje.setText(hora);
                }
                /* En caso de que no se tenga la última fecha ni hora
                *  (i.e. no hay historial o no se tenga almacenado previamente) */
                else
                    ocultarTextoUltimoMensaje();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addMessageToListView(String username, String msg, boolean enviaDestinatario) {
        MensajeChatModel newMsg = new MensajeChatModel();
        newMsg.setMsg(msg);
        newMsg.setNickname(username);

        try {
            // Seteando estilos del destinatario
            if (enviaDestinatario) {
                if (msgStyleDestino != null) {
                    newMsg.setMsgBold(msgStyleDestino.getString("bold"));
                    newMsg.setMsgItalic(msgStyleDestino.getString("italic"));
                    newMsg.setMsgColor(msgStyleDestino.getString("color"));
                }

                if (nicknameStyleDestino != null) {
                    newMsg.setNicknameBold(nicknameStyleDestino.getString("bold"));
                    newMsg.setNicknameItalic(nicknameStyleDestino.getString("italic"));
                    newMsg.setNicknameColor(nicknameStyleDestino.getString("color"));
                }
            }
            // Seteando estilos del usuario
            else {
                if (msgStyleUser != null) {
                    newMsg.setMsgBold(msgStyleUser.getString("bold"));
                    newMsg.setMsgItalic(msgStyleUser.getString("italic"));
                    newMsg.setMsgColor(msgStyleUser.getString("color"));
                }

                if (nicknameStyleUser != null) {
                    newMsg.setNicknameBold(nicknameStyleUser.getString("bold"));
                    newMsg.setNicknameItalic(nicknameStyleUser.getString("italic"));
                    newMsg.setNicknameColor(nicknameStyleUser.getString("color"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        listaMensajes.add(newMsg);
        adapter.notifyDataSetChanged();

        listViewMensajes.setSelection(adapter.getCount()-1);
    }

    private void ocultarTextoUltimoMensaje() {
        textViewUltimoMensajeRecibido.setVisibility(View.GONE);
        textViewHoraUltimoMensaje.setVisibility(View.GONE);
        textViewEl.setVisibility(View.GONE);
        textViewFechaUltimoMensaje.setVisibility(View.GONE);
    }

    private void mostrarTextoUltimoMensaje() {
        textViewUltimoMensajeRecibido.setVisibility(View.VISIBLE);
        textViewHoraUltimoMensaje.setVisibility(View.VISIBLE);
        textViewEl.setVisibility(View.VISIBLE);
        textViewFechaUltimoMensaje.setVisibility(View.VISIBLE);

        textViewHoraUltimoMensaje.setText(Utilidades.getTextoHora());
        textViewFechaUltimoMensaje.setText(Utilidades.getTextoFecha());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

}
