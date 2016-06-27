package cl.grobic.ponceapp.ponceapp.Servicios;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyService(String name) {
        super("messageReciever");
    }

    @Override
    protected void onHandleIntent(Intent intent) {



    }
}
