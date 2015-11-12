package cn.tootoo.cheetah.watcher;

import cn.tootoo.cheetah.constants.ZKWatcherCons;
import cn.tootoo.cheetah.util.ZKManagerUtil;
import cn.tootoo.cheetah.util.ZKReturnVO;
import org.apache.log4j.Logger;

/**
 *
 *
 *
 * Created by alienware on 2015-11-11.
 */
public class ZKWatcherRun implements Runnable {

    private static Logger logger = Logger.getLogger(ZKWatcherRun.class);

    private String in;

    private ZKWatcherCons z;

    public ZKWatcherRun(String in,ZKWatcherCons z){
        this.in = in;
        this.z = z;
    }

    @Override
    public void run() {
        ZKManagerUtil.run(in,z,logger);
    }
}
