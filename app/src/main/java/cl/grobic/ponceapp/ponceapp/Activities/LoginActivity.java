package cl.grobic.ponceapp.ponceapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cl.grobic.ponceapp.ponceapp.Conexion.Conexion;
import cl.grobic.ponceapp.ponceapp.Conexion.SendRequest;
import cl.grobic.ponceapp.ponceapp.R;

public class LoginActivity extends AppCompatActivity {

    private ImageButton btnIniciarSesion;
    private EditText emailEditText;
    private EditText contraseñaEditText;
    private String email;
    private String contraseña;
    private Conexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailEditText = (EditText) findViewById(R.id.editTextCorreoLogin);
        contraseñaEditText = (EditText) findViewById(R.id.editTextPasswordLogin);

        btnIniciarSesion = (ImageButton) findViewById(R.id.botonIniciarSesion);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                contraseña = contraseñaEditText.getText().toString();

                // Poniendo los parámetros del body para el request del Login
                JSONObject json = new JSONObject();

                try {
                    json.put("email", email);
                    json.put("password", contraseña);

                    SendRequest loginRequest = new SendRequest("login", "POST");
                    loginRequest.execute(json);

                    String respuesta = loginRequest.get();

                    if (respuesta == null){
                        Toast toast = Toast.makeText(LoginActivity.this, "Error al conectar al Server", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }

                    JSONObject jsonRespuesta = new JSONObject(respuesta);

                    // Si tiene la llave "status" entonces no pudo iniciar sesión
                    if (jsonRespuesta.has("status")){
                        Toast toast = Toast.makeText(LoginActivity.this, jsonRespuesta.get("message").toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    // En caso de que sí coincida el correo con el usuario se notifica al server
                    // que se logueó, se lanza la activity de los contactos
                    // y se le pasa la info del contacto que logueó recién
                    else{
                        conexion = new Conexion();
                        conexion.getSocket().emit("new user", jsonRespuesta.get("nickname").toString());

                        Intent intent = new Intent(LoginActivity.this, ContactosActivity.class);
                        intent.putExtra("user_info", respuesta);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }




}




