package cl.grobic.ponceapp.ponceapp.Conexion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

import cl.grobic.ponceapp.ponceapp.Utilidades.Utilidades;

/**
 * Created by Carlos on 30-04-2016.
 */
public class ObtenerAvatar extends AsyncTask<String, Void, Bitmap> {

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = Utilidades.URL_SERVER + "/media/" + urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }
}
