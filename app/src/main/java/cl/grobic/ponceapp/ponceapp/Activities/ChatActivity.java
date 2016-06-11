package cl.grobic.ponceapp.ponceapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    private String emailDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        conexion = new Conexion();
        conexion.escuchar("chat message", handleIncomingMessages);

        editTextIngresarMensaje = (EditText) findViewById(R.id.editTextIngresarMensaje);
        botonEnviarMensaje = (Button) findViewById(R.id.botonEnviarMensaje);
        listViewMensajes = (ListView) findViewById(R.id.listViewMensajesChat);

        listaMensajes = new ArrayList<MensajeChatModel>();
        adapter = new MessageAdapter(this, listaMensajes);
        listViewMensajes.setAdapter(adapter);

        try {
            user = new JSONObject(getIntent().getStringExtra("user_info"));
            emailDestino = getIntent().getStringExtra("user_destino");
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

    private void sendMessage() {
        String message = editTextIngresarMensaje.getText().toString();
        editTextIngresarMensaje.setText("");
        JSONObject sentData = new JSONObject();

        // Dado que los mensajes privados los manejo como '/w destino mensaje'
        // le agrego los comandos '/w destino' a los datos del mensaje del envío al server
        try {
            String nickname = user.get("nickname").toString();
            String email  = user.get("email").toString();
            sentData.put("msg", "/w " + emailDestino + " " + message);
            sentData.put("username", nickname);
            sentData.put("email", email);
            conexion.getSocket().emit("chat message", sentData);

            addMessageToListView(nickname, message);
            almacenarMensaje(nickname, message);

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
                            addMessageToListView(usernameIncommingMessage, msgIncommingMessage);
                            almacenarMensaje(usernameIncommingMessage, msgIncommingMessage);
                        }
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

    // Guarda el mensaje recibido/enviado a la memoria interna

    // Nota: Al recibir mensajes los escribe repetidas veces en el archivo! NPI la razón
    private void almacenarMensaje(String nickname, String mensaje) {
        try {
            // Lee el  archivo [email_destino].xml (en /data/data/cl.grobic.ponceapp.ponceapp/files/),
            // agrega un nuevo mensaje y vuelve a reescribir el archivo.
            FileInputStream fil = openFileInput(emailDestino + ".xml");

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(fil);

            Element root = document.getDocumentElement();

            Element nodoMensaje = document.createElement("nodoMensaje");
            root.appendChild(nodoMensaje);

            Element elementoNickname = document.createElement("nickname");
            elementoNickname.appendChild(document.createTextNode(nickname));
            nodoMensaje.appendChild(elementoNickname);

            Element elementoMensaje = document.createElement("mensaje");
            elementoMensaje.appendChild(document.createTextNode(mensaje));
            nodoMensaje.appendChild(elementoMensaje);

            //Creamos un fichero en memoria interna
            OutputStreamWriter fout = new OutputStreamWriter(
                    openFileOutput(emailDestino + ".xml", Context.MODE_PRIVATE));

            DOMSource source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(fout);
            transformer.transform(source, result);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    // Lee los mensajes almacenados y los agrega al ListView
    private void leerMensajesAlmacenados() {
        File file = this.getFileStreamPath(emailDestino + ".xml");

        // Si el archivo del historial no existe crea uno nuevo
        if (!file.exists()){
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                //Root element
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("historial");
                doc.appendChild(rootElement);

                // Escribiendo el contenido a un archivo .xml
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);

                //Creamos un fichero en memoria interna
                OutputStreamWriter fout = new OutputStreamWriter(
                        openFileOutput(emailDestino + ".xml", Context.MODE_PRIVATE));

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
             //Obtenemos la referencia al fichero XML de entrada
            FileInputStream fil = null;
            try {
                fil = openFileInput(emailDestino + ".xml");

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(fil);

                // Elemento raiz ("Historial")
                Element root = dom.getDocumentElement();

                // Iteramos sobre los hijos ("nodoMensaje") y agregamos el mensaje al ListView
                NodeList hijos = root.getChildNodes();

                // Ya que al recibir mensajes los guarda repetidas veces en el .xml (NPI por qué)
                // simplemente omito los mensajes repetidos que estén seguidos y agrego
                // sólo aquellos que difieren con el anterior.
                Node mensajeAnterior = hijos.item(0);
                for (int i = 0; i < hijos.getLength(); i++) {
                    Node nodoMensaje = hijos.item(i);
                    String nickname = nodoMensaje.getFirstChild().getTextContent();
                    String msg = nodoMensaje.getLastChild().getTextContent();

                    // Se agrega el primer mensaje
                    if (i == 0)
                        addMessageToListView(nickname, msg);

                    // Si el mensaje no es igual al anterior se agrega al ListView
                    if (!nodoMensaje.isEqualNode(mensajeAnterior))
                        addMessageToListView(nickname, msg);

                    mensajeAnterior = hijos.item(i);
                }

                fil.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

}
