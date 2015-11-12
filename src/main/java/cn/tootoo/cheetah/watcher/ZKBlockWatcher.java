package cn.tootoo.cheetah.watcher;

import cn.tootoo.cheetah.constants.ZKWatcherCons;
import cn.tootoo.cheetah.util.ZKManagerUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.net.InetAddress;
import java.nio.charset.Charset;

/**
 * WatcherBlock基类
 *
 * 只提供阻塞调用
 *
 *
 * Created by alienware on 2015-11-10.
 */
public class ZKBlockWatcher implements CuratorWatcher {
    private CuratorFramework zkTools;
    private final String path;
    private static Logger logger = Logger.getLogger(ZKBlockWatcher.class);
    private ZKWatcherCons z;
    public String getPath() {
        return path;
    }

    public ZKBlockWatcher(String path, CuratorFramework zkTools,ZKWatcherCons z) {
        this.path = path;
        this.zkTools = zkTools;
        this.z = z;
    }
    @Override
    public void process(WatchedEvent event) throws Exception {
        //输出ZK客户端连接
        logger.error(InetAddress.getLocalHost().getHostAddress());
        //输出事件类型
        logger.error(event.getType());
        //当事件类型为数据变化时 调用回调方法
        if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
            byte[] data = zkTools.
                    getData().
                    usingWatcher(this).forPath(path);
            String in = new String(data, Charset.forName("utf-8"));
            logger.error(path + ":" + in);
            //回调
            ZKManagerUtil.run(in, z,logger);
        }
    }
}
