package cl.grobic.ponceapp.ponceapp.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cl.grobic.ponceapp.ponceapp.Adapters.ContactosAdapter;
import cl.grobic.ponceapp.ponceapp.Conexion.Conexion;
import cl.grobic.ponceapp.ponceapp.Conexion.ObtenerAvatar;
import cl.grobic.ponceapp.ponceapp.Conexion.SendRequest;
import cl.grobic.ponceapp.ponceapp.Modelos.Usuario;
import cl.grobic.ponceapp.ponceapp.R;
import cl.grobic.ponceapp.ponceapp.Utilidades.Utilidades;

public class ContactosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewContactos;
    private ArrayList<Usuario> listaContactos;
    private ContactosAdapter adapter;
    private TextView textViewNicknameSideMenu;
    private TextView textViewEmailSideMenu;
    private TextView textViewStatusSideMenu;
    private JSONObject user;
    private Conexion conexion;
    private Usuario userDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        listViewContactos = (ListView) findViewById(R.id.listViewContactos);

        listaContactos = new ArrayList<Usuario>();
        adapter = new ContactosAdapter(this, listaContactos);
        listViewContactos.setAdapter(adapter);

        listViewContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                userDestino = (Usuario) listViewContactos.getItemAtPosition(position);

                Intent intent = new Intent(ContactosActivity.this, ChatActivity.class);
                intent.putExtra("user_info", getIntent().getStringExtra("user_info"));
                intent.putExtra("user_destino", Utilidades.convertirUsuarioAJSON(userDestino).toString());
                startActivity(intent);
            }
        });

        textViewEmailSideMenu = (TextView) header.findViewById(R.id.textViewEmailSideMenu);
        textViewNicknameSideMenu = (TextView) header.findViewById(R.id.textViewNicknameSideMenu);
        textViewStatusSideMenu = (TextView) header.findViewById(R.id.textViewStatusSideMuenu);


        try {
            // Obtiene la info del usuario
            user = new JSONObject(getIntent().getStringExtra("user_info"));

            textViewEmailSideMenu.setText(user.get("email").toString());
            textViewNicknameSideMenu.setText(user.get("nickname").toString());
            textViewStatusSideMenu.setText(user.get("state").toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Bundle b = new Bundle();
                try {
                    user = new JSONObject(getIntent().getStringExtra("user_info"));
                    b.putString("id", user.get("id").toString() );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                agregar_contacto fragment = new agregar_contacto();
                fragment.setArguments(b);

                fragment.show(ft,"Agregar Contacto Nuevo");
            }
        });

        obtenerContactos();


        // Escucha todas las conversas para almacenar los mensajes
        conexion = new Conexion();
        conexion.escuchar("chat message", handleIncomingMessages);

    }

    private void obtenerContactos() {
        try {
            // Llamada al server para obtener los contactos del usuario logueado
            SendRequest request = new SendRequest("user/" + user.get("id") + "/friends", "GET");
            request.execute();
            JSONArray jsonContactos = new JSONArray(request.get());

            // Se parsea los JSON a objetos Usuario y se agregan al ListView
            for (int i = 0; i < jsonContactos.length(); i++){
                JSONObject jsonContacto = jsonContactos.getJSONObject(i);
                Usuario contacto = new Usuario();

                contacto.setNickname(jsonContacto.get("nickname").toString());
                contacto.setEmail(jsonContacto.get("email").toString());
                contacto.setState(jsonContacto.get("state").toString());
                contacto.setId((Integer) jsonContacto.get("id"));
                contacto.setNickname_style(jsonContacto.get("nickname_style").toString());
                contacto.setAvatarPath(jsonContacto.get("avatar").toString());
                contacto.setMsg_style(jsonContacto.get("msg_style").toString());

                if (!jsonContacto.isNull("subnick"))
                    contacto.setSubnick(jsonContacto.get("subnick").toString());
                else
                    contacto.setSubnick("");

                // Se carga el avatar del server
                if (!jsonContacto.isNull("avatar")){
                    ObtenerAvatar obj = new ObtenerAvatar();
                    obj.execute(jsonContacto.get("avatar").toString());
                    contacto.setAvatar(obj.get());
                }
                // En caso de que no tenga avatar se carga el por defecto desde Resources
                else{
                    Bitmap avatarDefecto = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
                    contacto.setAvatar(avatarDefecto);
                }

                listaContactos.add(contacto);

            }

            /**
            // Agregando a uno mismo para pruebas!!!!!
            Usuario contacto = new Usuario();
            contacto.setNickname(user.get("nickname").toString());
            contacto.setEmail(user.get("email").toString());
            contacto.setState(user.get("state").toString());
            contacto.setId((Integer) user.get("id"));
            contacto.setNickname_style(user.get("nickname_style").toString());
            contacto.setAvatarPath(user.get("avatar").toString());
            contacto.setMsg_style(user.get("msg_style").toString());

            if (!user.isNull("subnick"))
                contacto.setSubnick(user.get("subnick").toString());
            else
                contacto.setSubnick("");

            // Se carga el avatar del server
            if (!user.isNull("avatar")){
                ObtenerAvatar obj = new ObtenerAvatar();
                obj.execute(user.get("avatar").toString());
                contacto.setAvatar(obj.get());
            }
            // En caso de que no tenga avatar se carga el por defecto desde Resources
            else{
                Bitmap avatarDefecto = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
                contacto.setAvatar(avatarDefecto);
            }

            listaContactos.add(contacto);
            // HASTA ACA SE AGREGA AL MISMO USUARIO
            */

            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
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

                        //displayNotification(emailIncommingMessage);

                        Utilidades.almacenarMensaje(ContactosActivity.this, emailIncommingMessage, usernameIncommingMessage, msgIncommingMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    protected void displayNotification(String emailDestino){
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("user_info", getIntent().getStringExtra("user_info"));
        i.putExtra("user_destino", Utilidades.convertirUsuarioAJSON(userDestino).toString());
        i.putExtra("notificationID", 1);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        CharSequence ticker ="Nuevo mensaje!";
        CharSequence contentTitle = "D:";
        CharSequence contentText = "Has recibido un nuevo mensaje!";
        Notification noti = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setTicker(ticker)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.contacto_1)
                .addAction(R.drawable.contacto_2, ticker, pendingIntent)
                .setVibrate(new long[] {100, 250, 100, 500})
                .build();
        nm.notify(1, noti);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.preferences.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_e_perfil) {

            return true;
        }
        else if(id == R.id.nav_ajustes){
            Intent intent = new Intent(ContactosActivity.this, AjustesActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.nav_c_sesion){
            conexion.desconectar();
            this.finish();
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        //return true;
    }
}
