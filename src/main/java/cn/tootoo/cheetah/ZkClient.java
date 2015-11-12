package cn.tootoo.cheetah;


import cn.tootoo.App;
import cn.tootoo.cheetah.constants.ZKWatcherCons;
import cn.tootoo.cheetah.util.ZKManagerUtil;
import cn.tootoo.cheetah.util.ZKReturnVO;
import cn.tootoo.cheetah.watcher.ZKBlockWatcher;
import cn.tootoo.cheetah.watcher.ZKWatcher;
import cn.tootoo.cheetah.watcher.ZKWatcherFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.ResourceBundle;

/**
 * 基于Curator的Zookeeper客户端
 *
 * 实现自动监听等功能
 *
 *
 *
 * Created by Alienware on 2015-11-9.
 */
public class ZkClient extends ZkClientBase {
    private static String url;
    private static RetryNTimes retryNTimes;

    static {
        logger.error("ZKClient is begin to init .. ");
        logger.error("Loading propreties ");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("ZK");
        url = resourceBundle.getString("ZKSERVER");
        logger.error("ZKServer : " + url);
        int retrytimes = Integer.parseInt(resourceBundle.getString("retrytimes"));
        int retrytimesleep = Integer.parseInt(resourceBundle.getString("retrytimesleep"));
        //初始化Retry
        retryNTimes = new RetryNTimes(retrytimes,retrytimesleep);
        logger.error("RetryNtimes : " + retrytimes +"" + retrytimesleep);
        String cha = resourceBundle.getString("Charset");
        //初始化编码
        charset = Charset.forName(cha);
        logger.error("Charset : " + cha);
        ZKManagerUtil.init();
        logger.error("init ZKTool , Path :" + ZKManagerUtil.getZKPath());
    }

    public ZkClient() throws Exception {
        //初始化ZKClient
        super(url,retryNTimes);
    }

    public ZKWatcher getZKWatcher(ZKWatcherCons z){
        ZKWatcher zkWatcher = new ZKWatcher(ZKManagerUtil.getZKPath(),zkTools,z);
        return zkWatcher;
    }

    public ZKBlockWatcher getZKBlockWatcher(ZKWatcherCons z){
        ZKBlockWatcher zkWatcher = new ZKBlockWatcher(ZKManagerUtil.getZKPath(),zkTools,z);
        return zkWatcher;
    }

    public static void main(String[] args) throws Exception{

        ZKWatcherCons z = new ZKWatcherCons() {
            @Override
            public ZKReturnVO process(String in) throws Exception{
                System.out.println("收到！！");
                Thread.sleep(5000L);
                System.out.println("好累！！");

                return new ZKReturnVO(true,"OK");
            }
        };

        ZkClient zkClient = new ZkClient();






//        ZKBlockWatcher zkWatcher;
//        zkWatcher = zkClient.getZKBlockWatcher();

//        zkClient.get();
//
        zkClient.addWatcher(zkClient.getZKWatcher(z))
                .start();

        for(;;){}
    }


}
