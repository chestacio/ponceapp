package cl.grobic.ponceapp.ponceapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cl.grobic.ponceapp.ponceapp.Modelos.MensajeChatModel;
import cl.grobic.ponceapp.ponceapp.R;

/**
 * Created by Carlos on 23-04-2016.
 */
// TODO: 23-04-2016
public class MessageAdapter extends ArrayAdapter<MensajeChatModel>{

    private final ArrayList<MensajeChatModel> mensajes;
    private final Context context;

    public MessageAdapter(Context context, ArrayList<MensajeChatModel> mensajes) {
        super(context, -1, mensajes);
        this.mensajes = mensajes;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.single_message_layout, parent, false);

        TextView textViewNickname = (TextView) row.findViewById(R.id.textViewNicknameChatMensaje);
        TextView textViewMensajes = (TextView) row.findViewById(R.id.textViewMensajeChatMensaje);

        textViewNickname.setText(mensajes.get(position).getNickname());
        textViewMensajes.setText(mensajes.get(position).getMsg());

        return row;
    }
}
