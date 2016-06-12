package cl.grobic.ponceapp.ponceapp.Utilidades;

import android.content.Context;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Carlos on 11-06-2016.
 */
public class Utilidades {

    // Guarda el mensaje recibido/enviado a la memoria interna
    // Nota: Al recibir mensajes los escribe repetidas veces en el archivo! NPI la razón
    public static void almacenarMensaje(Context contexto, String emailDestino, String nickname, String mensaje) {

        File file = contexto.getFileStreamPath(emailDestino + ".xml");

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
                        contexto.openFileOutput(emailDestino + ".xml", Context.MODE_PRIVATE));

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
            // Lee el  archivo [email_destino].xml (en /data/data/cl.grobic.ponceapp.ponceapp/files/),
            // agrega un nuevo mensaje y vuelve a reescribir el archivo.
            FileInputStream fil = contexto.openFileInput(emailDestino + ".xml");

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
                    contexto.openFileOutput(emailDestino + ".xml", Context.MODE_PRIVATE));

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
}
