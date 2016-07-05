package cl.grobic.ponceapp.ponceapp.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cl.grobic.ponceapp.ponceapp.Conexion.SendRequest;
import cl.grobic.ponceapp.ponceapp.R;

public class agregar_contacto extends DialogFragment{
    private EditText mail;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle("Agregar Contacto");

        builder.setView(inflater.inflate(R.layout.agregar_contacto, null))
                // Add action buttons
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //Aqui se hace todo el agregado
                        mail = (EditText) agregar_contacto.this.getDialog().findViewById(R.id.agregar_mail);
                        System.out.println("id: "+ getArguments().getString("id"));

                        // Poniendo los parámetros del body para el request del Login
                        JSONObject json = new JSONObject();

                        try {
                            json.put("email", mail);
                            SendRequest agregarContactoRequest = new SendRequest("user/" + getArguments().getString("id") + "/friends", "POST");
                            agregarContactoRequest.execute(json);

                            String respuesta = agregarContactoRequest.get();

                            if (respuesta == null){
                                Toast.makeText(getActivity(), "Error al conectar al Server", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject jsonRespuesta = new JSONObject(respuesta);

                            Toast.makeText(getActivity(), jsonRespuesta.get("message").toString(), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }



                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        agregar_contacto.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
