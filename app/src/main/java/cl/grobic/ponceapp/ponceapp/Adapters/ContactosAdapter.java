package cl.grobic.ponceapp.ponceapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cl.grobic.ponceapp.ponceapp.Modelos.Usuario;
import cl.grobic.ponceapp.ponceapp.R;

/**
 * Created by Carlos on 30-04-2016.
 */
public class ContactosAdapter extends ArrayAdapter<Usuario>{
    private final ArrayList<Usuario> contactos;
    private final Context context;

    public ContactosAdapter(Context context, ArrayList<Usuario> contactos) {
        super(context, -1, contactos);
        this.contactos = contactos;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.fila_contacto, parent, false);

        TextView textViewNickname = (TextView) row.findViewById(R.id.textViewNicknameListaContactos);
        TextView textViewSubnick = (TextView) row.findViewById(R.id.textViewSubnickListaContactos);
        ImageView imageViewAvatar = (ImageView) row.findViewById(R.id.imageViewAvatarListaContactos);

        textViewNickname.setText(contactos.get(position).getNickname());
        textViewSubnick.setText(contactos.get(position).getSubnick());
        imageViewAvatar.setImageBitmap(contactos.get(position).getAvatar());

        return row;
    }
}
