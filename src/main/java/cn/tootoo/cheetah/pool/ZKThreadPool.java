package cn.tootoo.cheetah.pool;

import org.apache.log4j.Logger;

import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 实现JUC线程池
 *
 * 用于防止回调函数阻塞
 *
 *
 *
 * Created by alienware on 2015-11-10.
 */
public class ZKThreadPool {
    private static Logger logger = Logger.getLogger(ZKThreadPool.class);
    //线程池
    private static ThreadPoolExecutor poolExecutor;
    //线程池初始容量
    private static int corePoolSize = 100;
    //线程池最大容量
    private static int maximumPoolSize = 200;
    //线程空闲时间默认10分钟
    private static long keepAliveTime = 1000*60*10*1;
    //主线程睡眠时间
    private static long sleepTime = 1000L;
    //线程池报警等待比例
    private static double WarningPoolbl = 0.6;
    //线程池报警等待次数
    private static int WarningPoolcount = 20;

    //静态加载线程池
    static {

    }

    private ZKThreadPool() throws Exception{
        //读取配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("threadPool");
        corePoolSize = Integer.parseInt(bundle.getString("corePoolSize"));
        maximumPoolSize = Integer.parseInt(bundle.getString("maximumPoolSize"));
        keepAliveTime = Long.parseLong(bundle.getString("keepAliveTime"));
        sleepTime = Long.parseLong(bundle.getString("sleepTime"));
        //加载线程池
        poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue(maximumPoolSize), new ThreadPoolExecutor.DiscardPolicy());
        logger.error("ThreadPool is Created!");
    }

    public static ThreadPoolExecutor getPoolExecutor() throws Exception{
        if (poolExecutor == null) new ZKThreadPool();
        return poolExecutor;
    }


}
