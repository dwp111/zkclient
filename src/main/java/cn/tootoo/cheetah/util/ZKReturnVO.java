package cn.tootoo.cheetah.util;

/**
 * 回调函数的返回值封装
 *
 *
 * Created by alienware on 2015-11-11.
 */
public class ZKReturnVO {

    private Boolean result;
    private String msg;

    public ZKReturnVO(Boolean a,String msg){
        this.result = a;
        this.msg = msg;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
