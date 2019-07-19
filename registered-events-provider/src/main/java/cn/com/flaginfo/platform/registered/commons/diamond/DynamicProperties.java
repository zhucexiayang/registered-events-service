package cn.com.flaginfo.platform.registered.commons.diamond;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Meng.Liu
 * @create 2017-09-26 10:55
 **/

public abstract class DynamicProperties {

    protected DynamicProperties(){

    };

    protected final Set<MessageChangeLinstener> changeLinsteners = new HashSet<MessageChangeLinstener>();

    protected static DynamicProperties dynamicProperties;

    protected Map<String,Object> preData;

    public static DynamicProperties getInstance(){
        if(dynamicProperties == null){
            throw new RuntimeException("the instance don't instace, please see the subclass");
        }
        return dynamicProperties;
    }

    public abstract String getProperty(String key);

    public abstract Map<String,Object> getAllConfig();

    public void addChangeLinstener(MessageChangeLinstener linstener){
        this.changeLinsteners.add(linstener);
    }

    public void messageChange() {
        for(MessageChangeLinstener cl:changeLinsteners){
            String keys[] = cl.register();
            if(keys == null){
                break;
            }
            for(String key:keys){
                if(isChange(key)){
                    System.out.println(key+" do change ==>"+cl.getClass().getName());
                    cl.change();
                    break;
                }
            }
        }
    }

    protected boolean isChange(String key){
        if(this.preData == null || this.preData.size() == 0){
            return false;
        }
        Set<String> set = this.preData.keySet();
        int oc=0;
        for(String var:set){
            if(var.startsWith(key)){
                if(!this.preData.get(var).equals(this.getProperty(var))){
                    return true;
                }
                oc++;
            }
        }
        int nc = 0;
        set = this.getAllConfig().keySet();
        for(String var:set){
            if(var.startsWith(key)){
                nc++;
            }
        }
        if(nc != oc){
            return true;
        }
        return false;
    }


}