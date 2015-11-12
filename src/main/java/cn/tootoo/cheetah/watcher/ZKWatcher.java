package cn.tootoo.cheetah.watcher;

import cn.tootoo.cheetah.constants.ZKWatcherCons;
import cn.tootoo.cheetah.pool.ZKThreadPool;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.nio.charset.Charset;

/**
 * Watcher基类
 *
 * 实现线程池 提供非阻塞调用
 *
 *
 *
 * Created by Alienware on 2015-11-9.
 */
public class ZKWatcher implements CuratorWatcher,Comparable {
    private CuratorFramework zkTools;
    private final String path;
    private static Logger logger = Logger.getLogger(ZKWatcher.class);
    private ZKWatcherCons zkWatcherCons;
    public ZKWatcher(String path, CuratorFramework zkTools,ZKWatcherCons zkWatcherCons) {
        this.path = path;
        this.zkTools = zkTools;
        this.zkWatcherCons = zkWatcherCons;
    }

    @Override
    public void process(WatchedEvent event) throws Exception {
        //输出ZK客户端连接
        logger.error(zkTools.getZookeeperClient().getCurrentConnectionString());
        //输出事件类型
        logger.error(event.getType());
        //当事件类型为数据变化时 调用回调方法
        if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
            byte[] data = zkTools.
                    getData().
                    usingWatcher(this).forPath(path);
            logger.error(path + ":" + new String(data, Charset.forName("utf-8")));
            String in = new String(data, Charset.forName("utf-8"));
            ZKThreadPool.getPoolExecutor().execute(new ZKWatcherRun(in,zkWatcherCons));
        }

    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
