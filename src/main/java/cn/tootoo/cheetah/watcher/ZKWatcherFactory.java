package cn.tootoo.cheetah.watcher;

import cn.tootoo.cheetah.ZkClientBase;
import cn.tootoo.cheetah.constants.ZKWatcherCons;
import cn.tootoo.cheetah.util.ZKManagerUtil;

/**
 * Created by alienware on 2015-11-11.
 */
public class ZKWatcherFactory  {
    private static ZKWatcherFactory zkWatcherFactory;

    public static ZKWatcherFactory getZkWatcherFactory() {
        if(zkWatcherFactory == null) zkWatcherFactory = new ZKWatcherFactory();
        return zkWatcherFactory;
    }

    private ZKWatcherFactory(){

    }

//    /**
//     * 通过回掉函数实现类获取Watcher
//     * @param zkWatcherCons
//     * @return
//     */
//    public ZKWatcher newZKwatcher(Class zkWatcherCons) throws Exception{
//        return new ZKWatcher(ZKManagerUtil.getZKPath(),ZkClientBase.getZkTools(),zkWatcherCons);
//    }

    /**
     * 通过回掉函数实现类获取BlockWatcher
     * @param zkWatcherCons
     * @return
     */
//    public ZKWatcher newZKBlockwatcher(ZKWatcherCons zkWatcherCons) throws Exception{
//        return new ZKWatcher(ZKManagerUtil.getZKPath(),ZkClientBase.getZkTools(),zkWatcherCons);
//    }
}
