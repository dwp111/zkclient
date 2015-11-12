package cn.tootoo.cheetah;

import cn.tootoo.cheetah.exception.CheetahException;
import cn.tootoo.cheetah.pool.ZKThreadPool;
import cn.tootoo.cheetah.util.ZKManagerUtil;
import cn.tootoo.cheetah.watcher.ZKBlockWatcher;
import cn.tootoo.cheetah.watcher.ZKWatchRegister;
import cn.tootoo.cheetah.watcher.ZKWatcher;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * ZKClientBase
 *
 * 单一实例
 *
 *
 *
 *
 * Created by alienware on 2015-11-9.
 */
public class ZkClientBase {
    protected static Logger logger = Logger.getLogger(ZkClientBase.class);
    protected static CuratorFramework zkTools;
    private ConcurrentSkipListSet watchers = new ConcurrentSkipListSet();
    private Set<CuratorWatcher> watcherSet = new HashSet<CuratorWatcher>();
    protected static Charset charset ;
    protected ZkClientBase(String url,RetryNTimes retryNTimes) throws Exception{
        try {
            zkTools = CuratorFrameworkFactory
                    .builder()
                    .connectString(url)
    //                .namespace("/zk/test")
                    .retryPolicy(retryNTimes)
                    .build();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            logger.error("ZkClientFactory build failed !");
            throw new CheetahException("ZkClientFactory build failed !");
        }
        zkTools.start();
        logger.error("ZKClinet start succsss! ");
    }

    /**
     * 注册Watcher
     * @param path
     * @param watcherType
     * @param watcher
     */
    protected final void addReconnectionWatcher(final String path, final ZookeeperWatcherType watcherType, final CuratorWatcher watcher) {
        synchronized (this) {
            //不添加重复的监听事件
            if (!watchers.contains(watcher.toString()))
            {
                watchers.add(watcher.toString());
                logger.error("add new watcher " + watcher);
                zkTools.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                    @Override
                    public void stateChanged (CuratorFramework client, ConnectionState newState){
                        logger.error(newState);
                        if (newState == ConnectionState.LOST) {//处理session过期
                            try {
                                if (watcherType == ZookeeperWatcherType.EXITS) {
                                    zkTools.checkExists().usingWatcher(watcher).forPath(path);
                                } else if (watcherType == ZookeeperWatcherType.GET_CHILDREN) {
                                    zkTools.getChildren().usingWatcher(watcher).forPath(path);
                                } else if (watcherType == ZookeeperWatcherType.GET_DATA) {
                                    zkTools.getData().usingWatcher(watcher).forPath(path);
                                    logger.error("using watcher success !");
                                } else if (watcherType == ZookeeperWatcherType.CREATE_ON_NO_EXITS) {
                                    //ephemeral类型的节点session过期了，需要重新创建节点，并且注册监听事件，之后监听事件中，
                                    //会处理create事件，将路径值恢复到先前状态
                                    Stat stat = zkTools.checkExists().usingWatcher(watcher).forPath(path);
                                    if (stat == null) {
                                        logger.error("to create");
                                        zkTools.create()
                                                .creatingParentsIfNeeded()
                                                .withMode(CreateMode.EPHEMERAL)
                                                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                                                .forPath(path);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * create ZKClient Path
     * @param path
     * @throws Exception
     */
    public static void create(String path) throws Exception {
        zkTools.create()//创建一个路径
                .creatingParentsIfNeeded()//如果指定的节点的父节点不存在，递归创建父节点
                .withMode(CreateMode.PERSISTENT)//存储类型（临时的还是持久的）
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)//访问权限
                .forPath(path);//创建的路径
    }


    /**
     *
     * @param path
     * @param message
     * @throws Exception
     */
    public static void put(String path,String message) throws Exception{
        put(path,message,charset);
    }

    /**
     * put message into ZKclient Path
     * @param path
     * @param message
     * @param charset
     * @throws Exception
     */
    public static void put(String path,String message,Charset charset) throws Exception {
        zkTools.//对路径节点赋值
                setData().
                forPath(path, message.getBytes(charset));

    }

    /**
     * 向ZKClient中加入Watcher
     * @param watcher
     */
    public ZkClientBase addWatcher(CuratorWatcher watcher){
        watcherSet.add(watcher);
        return this;
    }

    /**
     * 启动ZkClient
     *
     */
    public void start() throws Exception{
        if(watcherSet == null){
            throw new CheetahException("Please Add Watcher Before Use Us");
        }
        //开始初始化ZK监听
        logger.error("ZKWatcher begin to init !");
        //遍历watcherset
        Boolean b = false;
        for(CuratorWatcher o : watcherSet){
            //如果非阻塞规则调用 则必须初始化线程池
            if(o instanceof ZKWatcher){
                logger.error("ZKThreadPool begin to init !");
                b = true;
            }
        }
        if(b) {
            try {
                ZKThreadPool.getPoolExecutor();
            } catch (Exception e) {
                logger.error("ZKThreadPool init failed !");
                logger.error(e);
                throw new CheetahException("ThreadPool failed ! ");
            }
        }
        for(CuratorWatcher o : watcherSet){
            try {
                //测试链接ZKwatch
                //获取当前节点的当前值版本
                byte[] buffer = zkTools.
                        getData().
                        usingWatcher(o).forPath(ZKManagerUtil.getZKPath());
                this.addReconnectionWatcher(
                        ZKManagerUtil.getZKPath(),
                        ZookeeperWatcherType.GET_DATA,
                        o
                );
            } catch (Exception e) {
                logger.error("ZKWatcherInit Failed!");
                logger.error(e);
                throw new CheetahException("ZKWatcherInit Failed!");
            }
        }
        logger.error("ZKWatcher init Success!!!");
    }


    public void register() throws Exception {

        String ip = InetAddress.getLocalHost().getHostAddress();
        String registeNode = "/zk/register/" + ip;//节点路径

        byte[] data = "disable".getBytes(charset);//节点值

        CuratorWatcher watcher = new ZKWatchRegister(registeNode, data,zkTools);    //创建一个register watcher

        Stat stat = zkTools.checkExists().forPath(registeNode);
        if (stat != null) {
            zkTools.delete().forPath(registeNode);
        }
        zkTools.create()
                .creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(registeNode, data);//创建的路径和值

        //添加到session过期监控事件中
        addReconnectionWatcher(registeNode, ZookeeperWatcherType.CREATE_ON_NO_EXITS, watcher);
        data = zkTools.getData().usingWatcher(watcher).forPath(registeNode);
        System.out.println("get path form zk : " + registeNode + ":" + new String(data, charset));
    }





}
