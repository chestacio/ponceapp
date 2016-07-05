package cl.grobic.ponceapp.ponceapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cl.grobic.ponceapp.ponceapp.Conexion.Conexion;
import cl.grobic.ponceapp.ponceapp.Conexion.SendRequest;
import cl.grobic.ponceapp.ponceapp.R;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText _username;
    private EditText _emailText;
    private EditText _passwordText;
    private Button _signupButton;
    private TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        _username = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginLink = (TextView) findViewById(R.id.link_login);

        _signupButton = (Button) findViewById(R.id.btn_signup);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("error signup");
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegistroActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();

        String username = _username.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        // Poniendo los parámetros del body para el request del Login
        JSONObject json = new JSONObject();

        try {
            json.put("nickname", username);
            json.put("email", email);
            json.put("password", password);

            SendRequest agregarUsuarioRequest = new SendRequest("user", "POST");
            agregarUsuarioRequest.execute(json);

            String respuesta = agregarUsuarioRequest.get();

            if (respuesta == null){
                onSignupFailed("Error al conectar al Server");
                return;
            }

            JSONObject jsonRespuesta = new JSONObject(respuesta);

            // Si tiene la llave "status" entonces no pudo iniciar sesión
            if (jsonRespuesta.has("status")){
                progressDialog.dismiss();
                onSignupFailed(jsonRespuesta.get("message").toString());
            }
            // En caso de que sí coincida el correo con el usuario se notifica al server
            // que se logueó, se lanza la activity de los contactos
            // y se le pasa la info del contacto que logueó recién
            else{
                onSignupSuccess(jsonRespuesta.get("message").toString());

                //this.finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        //onSignupSuccess("holi");
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess(String mensaje) {
        _signupButton.setEnabled(true);
        Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(String mensaje) {
        Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _username.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _username.setError("al menos 3 caracteres");
            valid = false;
        } else {
            _username.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("ingrese un mail valido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("entre 4 a 10 caracteres alfanumericos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
