package cl.grobic.ponceapp.ponceapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import cl.grobic.ponceapp.ponceapp.Adapters.ContactosAdapter;
import cl.grobic.ponceapp.ponceapp.Conexion.ObtenerAvatar;
import cl.grobic.ponceapp.ponceapp.Conexion.SendRequest;
import cl.grobic.ponceapp.ponceapp.Modelos.Usuario;
import cl.grobic.ponceapp.ponceapp.R;

public class ContactosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listViewContactos;
    private ArrayList<Usuario> listaContactos;
    private ContactosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listViewContactos = (ListView) findViewById(R.id.listViewContactos);
        listaContactos = new ArrayList<Usuario>();
        adapter = new ContactosAdapter(this, listaContactos);
        listViewContactos.setAdapter(adapter);

        obtenerContactos();

    }

    private void obtenerContactos() {
        try {
            // Obtiene la info del usuario
            JSONObject user = new JSONObject(getIntent().getStringExtra("user_info"));

            // Llamada al server para obtener los contactos del usuario logueado
            SendRequest request = new SendRequest("user/" + user.get("id") + "/friends", "GET");
            request.execute();
            JSONArray jsonContactos = new JSONArray(request.get());

            // Se parsea los JSON a objetos Usuario y se agregan al ListView
            for (int i = 0; i < jsonContactos.length(); i++){
                JSONObject jsonContacto = jsonContactos.getJSONObject(i);
                Usuario contacto = new Usuario();

                contacto.setNickname(jsonContacto.get("nickname").toString());
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

            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
        // as you specify a parent activity in AndroidManifest.xml.
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
