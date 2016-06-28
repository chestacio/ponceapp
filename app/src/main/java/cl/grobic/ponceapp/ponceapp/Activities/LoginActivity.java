package cl.grobic.ponceapp.ponceapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cl.grobic.ponceapp.ponceapp.Conexion.Conexion;
import cl.grobic.ponceapp.ponceapp.Conexion.SendRequest;
import cl.grobic.ponceapp.ponceapp.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private Button btnIniciarSesion;
    private EditText emailEditText;
    private EditText contraseñaEditText;
    private TextView signupLink;

    private String email;
    private String contraseña;
    private Conexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.input_email);
        contraseñaEditText = (EditText) findViewById(R.id.input_password);

        btnIniciarSesion = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Error de validacion");
            return;
        }

        btnIniciarSesion.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autentificando...");
        progressDialog.show();

        String email = emailEditText.getText().toString();
        String password = contraseñaEditText.getText().toString();

        // TODO: Implement your own authentication logic here.
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
                onLoginFailed("Error al conectar al Server");
                return;
            }

            JSONObject jsonRespuesta = new JSONObject(respuesta);

            // Si tiene la llave "status" entonces no pudo iniciar sesión
            if (jsonRespuesta.has("status")){
                progressDialog.dismiss();
                onLoginFailed(jsonRespuesta.get("message").toString());
            }
            // En caso de que sí coincida el correo con el usuario se notifica al server
            // que se logueó, se lanza la activity de los contactos
            // y se le pasa la info del contacto que logueó recién
            else{
                conexion = new Conexion();
                conexion.getSocket().emit("new user", jsonRespuesta.get("email").toString());

                Intent intent = new Intent(LoginActivity.this, ContactosActivity.class);
                intent.putExtra("user_info", respuesta);
                progressDialog.dismiss();
                startActivity(intent);
                //this.finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        btnIniciarSesion.setEnabled(true);
    }
    public boolean validate() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        String password = contraseñaEditText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Ingresar un mail valido");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            contraseñaEditText.setError("Entre 4 a 10 caracteres alfanumericos");
            valid = false;
        } else {
            contraseñaEditText.setError(null);
        }

        return valid;
    }

}










