package cl.grobic.ponceapp.ponceapp.Modelos;

/**
 * Created by Carlos on 23-04-2016.
 */
public class MensajeChatModel {

    private String nickname;
    private String msg;
    private String nicknameColor;
    private String msgColor;
    private String nicknameBold;
    private String nicknameItalic;
    private String msgBold;
    private String msgItalic;
    private boolean contactoDestino;

    public boolean isContactoDestino() {
        return contactoDestino;
    }

    public void setContactoDestino(boolean contactoDestino) {
        this.contactoDestino = contactoDestino;
    }

    public String getNicknameBold() {
        return nicknameBold;
    }

    public void setNicknameBold(String nicknameBold) {
        this.nicknameBold = nicknameBold;
    }

    public String getNicknameItalic() {
        return nicknameItalic;
    }

    public void setNicknameItalic(String nicknameItalic) {
        this.nicknameItalic = nicknameItalic;
    }

    public String getMsgBold() {
        return msgBold;
    }

    public void setMsgBold(String msgBold) {
        this.msgBold = msgBold;
    }

    public String getMsgItalic() {
        return msgItalic;
    }

    public void setMsgItalic(String msgItalic) {
        this.msgItalic = msgItalic;
    }

    public String getNicknameColor() {
        return nicknameColor;
    }

    public void setNicknameColor(String nicknameColor) {
        this.nicknameColor = nicknameColor;
    }

    public String getMsgColor() {
        return msgColor;
    }

    public void setMsgColor(String msgColor) {
        this.msgColor = msgColor;
    }

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
