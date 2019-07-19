package cn.com.flaginfo.platform.registered.commons.diamond;

/**
 * @author Meng.Liu
 * @create 2017-09-26 10:56
 **/
public abstract class MessageChangeLinstener {
    public MessageChangeLinstener() {
        DynamicProperties.getInstance().addChangeLinstener(this);
    }

    public abstract String[] register();

    public abstract void change();
}