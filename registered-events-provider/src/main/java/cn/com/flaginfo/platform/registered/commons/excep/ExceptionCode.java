package cn.com.flaginfo.platform.registered.commons.excep;

/**
 * Created by Liang.Zhang on 2018/12/25.
 **/

public class ExceptionCode {

    public static interface BaseException{
        public Long getCode();
        public String getMsg();
        public String getLog();
    }

    public static enum CommonException implements BaseException{
        _40011(40011L, "参数错误", "参数为空或参数结构错误"),
        _40012(40012L, "服务处理异常", "服务处理异常，详情请查看日志");

        private Long code;
        private String msg;
        private String log;
        CommonException(Long code, String msg, String log ){
            this.code = code;
            this.msg = msg;
            this.log = log;
        }
        @Override
        public Long getCode() {
            return code;
        }
        @Override
        public String getMsg() {
            return msg;
        }
        @Override
        public String getLog(){
            return log;
        }
    }

}
