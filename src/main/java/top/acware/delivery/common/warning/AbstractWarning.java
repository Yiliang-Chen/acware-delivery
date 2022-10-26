package top.acware.delivery.common.warning;

public abstract class AbstractWarning extends Thread implements Warning {

    public String msg;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public abstract void setSubject(String subject);

    @Override
    public abstract void addTo(String... toEmails);

    @Override
    public abstract void addCc(String... ccEmails);

    @Override
    public abstract void run();

}
