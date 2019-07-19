package cn.com.flaginfo.platform.registered.commons.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static String formate1="YYYY-MM-dd HH:mm:ss";

    public  static String  getDates(){
        SimpleDateFormat sdf=new SimpleDateFormat(formate1);
        return sdf.format(new Date());
    }
}
