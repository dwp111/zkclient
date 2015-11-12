package cn.tootoo.cheetah.constants;


import cn.tootoo.cheetah.util.ZKReturnVO;

/**
 * 用于启动ZKWatcher
 *
 *
 * Created by alienware on 2015-11-10.
 */
public interface ZKWatcherCons {
    public ZKReturnVO process(String in) throws Exception;
}
