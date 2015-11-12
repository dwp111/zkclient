package cn.tootoo.cheetah.util;

import cn.tootoo.cheetah.ZkClient;
import cn.tootoo.cheetah.constants.ZKWatcherCons;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

import java.util.ResourceBundle;

/**
 * 通过ZK.properties 中的code
 * 从Redis中获取Zk数据
 *
 *
 *
 *
 * Created by alienware on 2015-11-11.
 */
public class ZKManagerUtil {
    private static Logger logger = Logger.getLogger(ZKManagerUtil.class);
    private String ZKPath;
    private String ZKReturnPath;
    private static ZKManagerUtil zkManagerUtil;
    private ZKManagerUtil(){
        logger.error("ZKManagerUtil init is begin ");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("ZK");
        String code = resourceBundle.getString("ResignCode");
        ZKPath = "/zk/test";
        ZKReturnPath = "/return/zk/test";
        logger.error("ZKManagerUtil init is success ");
    }

    public static void init(){
        zkManagerUtil = new ZKManagerUtil();
    }

    public static String getZKPath(){
        return zkManagerUtil.ZKPath;
    }

    public static String getZKReturnPath(){
        return zkManagerUtil.ZKReturnPath;
    }

    public static void run(String in,ZKWatcherCons z,Logger logger){
        ZKReturnVO zvo = null;
        logger.error("The Message Key :" + in);
        //通过Redis获取消息体
//            Redis.get(in)
        //执行
        logger.error("begin to start process ");
        Boolean b = false;

        try {
            zvo = z.process(in);
            b = zvo.getResult();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            //将异常信息返回
            try {
                ZkClient.put(ZKManagerUtil.getZKReturnPath(),in +zvo.getMsg());
            } catch (KeeperException e1) {
                //如果因为没有节点而失败 则重试
                try {
                    ZkClient.create(ZKManagerUtil.getZKReturnPath());
                    ZkClient.put(ZKManagerUtil.getZKReturnPath(),in +zvo.getMsg());
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } catch (Exception e1){
                e1.printStackTrace();
            }
        }
        //执行完毕
        if(b) logger.error("process is ok in/out message is : " + zvo.getMsg());
        //将返回值及接口状态传回ZKServer
        try {
            ZkClient.put(ZKManagerUtil.getZKReturnPath(),in +zvo.getMsg());
        } catch (KeeperException e1) {
            //如果因为没有节点而失败 则重试
            try {
                ZkClient.create(ZKManagerUtil.getZKReturnPath());
                ZkClient.put(ZKManagerUtil.getZKReturnPath(),in + zvo.getMsg());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (Exception e1){
            e1.printStackTrace();
        }
    }
}
