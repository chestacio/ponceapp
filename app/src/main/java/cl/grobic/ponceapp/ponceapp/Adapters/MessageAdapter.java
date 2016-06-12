package cl.grobic.ponceapp.ponceapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

        View row = inflater.inflate(R.layout.fila_mensaje, parent, false);

        TextView textViewNickname = (TextView) row.findViewById(R.id.textViewNicknameChatMensaje);
        TextView textViewMensajes = (TextView) row.findViewById(R.id.textViewMensajeChatMensaje);

        MensajeChatModel mensaje = mensajes.get(position);

        textViewNickname.setText(mensaje.getNickname());
        textViewMensajes.setText(mensaje.getMsg());

        // Seteando estilos al nick
        if (mensaje.getNicknameBold() != null && (mensaje.getNicknameBold().equals("true")))
            textViewNickname.setTypeface(null, Typeface.BOLD);

        if (mensaje.getNicknameItalic() != null && mensaje.getNicknameItalic().equals("true"))
            textViewNickname.setTypeface(null, Typeface.ITALIC);

        if (mensaje.getNicknameColor() != null)
            textViewNickname.setTextColor(Color.parseColor(mensaje.getNicknameColor()));

        // Seteando estilos al mensaje
        if (mensaje.getMsgBold() != null && (mensaje.getMsgBold().equals("true")))
            textViewMensajes.setTypeface(null, Typeface.BOLD);

        if (mensaje.getMsgItalic() != null && mensaje.getMsgItalic().equals("true"))
            textViewMensajes.setTypeface(null, Typeface.ITALIC);

        if (mensaje.getMsgColor() != null)
            textViewMensajes.setTextColor(Color.parseColor(mensaje.getMsgColor()));





        return row;
    }
}
