package cl.grobic.ponceapp.ponceapp.Modelos;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Carlos on 26-04-2016.
 */
public class Usuario {
    private int id;
    private String nickname;
    private String subnick;
    private String email;
    private String nickname_style;
    private String msg_style;
    private Bitmap avatar;
    private String state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSubnick() {
        return subnick;
    }

    public void setSubnick(String subnick) {
        this.subnick = subnick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname_style() {
        return nickname_style;
    }

    public void setNickname_style(String nickname_style) {
        this.nickname_style = nickname_style;
    }

    public String getMsg_style() {
        return msg_style;
    }

    public void setMsg_style(String msg_style) {
        this.msg_style = msg_style;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
