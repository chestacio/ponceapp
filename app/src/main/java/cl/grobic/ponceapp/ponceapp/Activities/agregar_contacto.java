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
