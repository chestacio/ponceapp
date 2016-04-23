package cl.grobic.ponceapp.ponceapp.Modelos;

/**
 * Created by Carlos on 23-04-2016.
 */
public class MensajeChatModel {

    private String nickname;
    private String msg;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return nickname + ": " + msg;
    }
}
