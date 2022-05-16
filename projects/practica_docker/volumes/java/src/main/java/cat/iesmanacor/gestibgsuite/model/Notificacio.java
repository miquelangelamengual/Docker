package cat.iesmanacor.gestibgsuite.model;

public class Notificacio {

    private NotificacioTipus notifyType;
    private String notifyMessage;

    public Notificacio() {
    }

    public NotificacioTipus getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(NotificacioTipus notifyType) {
        this.notifyType = notifyType;
    }

    public String getNotifyMessage() {
        return notifyMessage;
    }

    public void setNotifyMessage(String notifyMessage) {
        this.notifyMessage = notifyMessage;
    }
}
