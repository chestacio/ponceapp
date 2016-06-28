package cl.grobic.ponceapp.ponceapp.Utilidades;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import cl.grobic.ponceapp.ponceapp.Modelos.Usuario;

/**
 * Created by Carlos on 11-06-2016.
 */
public class Utilidades {

    //public static String URL_SERVER = "http://10.6.214.180:3000";
    public static String URL_SERVER = "http://192.168.0.20:3000";

    // Guarda el mensaje recibido/enviado a la memoria interna
    // Nota: Al recibir mensajes los escribe repetidas veces en el archivo! NPI la razón
    public static void almacenarMensaje(Context contexto, String emailDestino, String nickname, String mensaje) {

        File file = contexto.getFileStreamPath(emailDestino + ".preferences");

        // Si el archivo del historial no existe crea uno nuevo
        if (!file.exists()){
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
                        contexto.openFileOutput(emailDestino + ".preferences", Context.MODE_PRIVATE));

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

        }

        try {
            // Lee el  archivo [email_destino].preferences (en /data/data/cl.grobic.ponceapp.ponceapp/files/),
            // agrega un nuevo mensaje y vuelve a reescribir el archivo.
            FileInputStream fil = contexto.openFileInput(emailDestino + ".preferences");

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

            Element elementoHora = document.createElement("hora");
            elementoHora.appendChild(document.createTextNode(getTextoHora()));
            nodoMensaje.appendChild(elementoHora);

            Element elementoFecha = document.createElement("fecha");
            elementoFecha.appendChild(document.createTextNode(getTextoFecha()));
            nodoMensaje.appendChild(elementoFecha);

            //Creamos un fichero en memoria interna
            OutputStreamWriter fout = new OutputStreamWriter(
                    contexto.openFileOutput(emailDestino + ".preferences", Context.MODE_PRIVATE));

            DOMSource source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(fout);
            transformer.transform(source, result);

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
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

    /* Retorna la hora en formato hh:mm */
    public static String getTextoHora() {
        Calendar horaActual = Calendar.getInstance();

        int hora = horaActual.get(Calendar.HOUR);
        int min = horaActual.get(Calendar.MINUTE);

        String minStr = min < 10 ? "0" + min : String.valueOf(min);
        String horaStr = hora < 10 ? "0" + hora : String.valueOf(hora);

        return horaStr + ":" + minStr;
    }

    /* Retorna la fecha en formato dd/mm/aaaa */
    public static String getTextoFecha() {
        Calendar horaActual = Calendar.getInstance();

        int dia = horaActual.get(Calendar.DAY_OF_MONTH);
        int mes = horaActual.get(Calendar.MONTH) + 1;
        int año = horaActual.get(Calendar.YEAR);

        String diaStr = dia < 10 ? "0" + dia : String.valueOf(dia);
        String mesStr = mes < 10 ? "0" + mes : String.valueOf(mes);

        return diaStr + "/" + mesStr + "/" + String.valueOf(año);
    }

    public static JSONObject convertirUsuarioAJSON(Usuario usuario) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", usuario.getId());
            json.put("nickname", usuario.getNickname());
            json.put("subnick", usuario.getSubnick());
            json.put("email", usuario.getEmail());
            json.put("nickname_style", usuario.getNickname_style());
            json.put("avatar", usuario.getAvatarPath());
            json.put("state", usuario.getState());
            json.put("msg_style", usuario.getMsg_style());

            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
