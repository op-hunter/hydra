package cn.edu.njnet.hydra.zookeeper;
import java.io.IOException; 
import java.util.concurrent.CountDownLatch;  

import org.apache.zookeeper.CreateMode; 
import org.apache.zookeeper.KeeperException; 
import org.apache.zookeeper.WatchedEvent; 
import org.apache.zookeeper.Watcher; 
import org.apache.zookeeper.Watcher.Event.KeeperState; 
import org.apache.zookeeper.ZooDefs.Ids; 
import org.apache.zookeeper.ZooKeeper;  
import org.apache.zookeeper.data.Stat;
 
public class ZooApi implements Watcher {  
    private ZooKeeper zk = null;      
    private CountDownLatch connectedSemaphore = new CountDownLatch( 1 ); 
 
    /** 
     * 创建链接
     * @param connectString 链接字符串
     * @param sessionTimeout 超时   
     */ 
    public void createConnection( String connectString, int sessionTimeout ) { 
        this.releaseConnection(); 
        try { 
            zk = new ZooKeeper( connectString, sessionTimeout, this ); 
            connectedSemaphore.await(); 
        } catch ( InterruptedException e ) { 
            e.printStackTrace(); 
        } catch ( IOException e ) { 
            e.printStackTrace(); 
        } 
    } 
 
    /** 
     * 释放链接
     */ 
    public void releaseConnection() { 
        if ( this.zk != null) { 
            try { 
                this.zk.close(); 
            } catch ( InterruptedException e ) { 
                e.printStackTrace(); 
            } 
        } 
    } 
 
    /** 
     * 创建一个路径
     * @param path  
     * @param data 
     * @return 
     */ 
    public boolean createPath( String path, String data ) { 
        try { 
            this.zk.create( path, data.getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );//CreateMode.EPHEMERAL
        } catch ( KeeperException e ) {  
            e.printStackTrace(); 
        } catch ( InterruptedException e ) { 
            e.printStackTrace(); 
        } 
        return true; 
    } 
 
    /** 
     * 读数据，同时设置watcher和stat
     * @param path path
     * @param wa   Watcher
     * @param stst stat 
     * @return 
     */ 
    public String readData( String path, Watcher wa, Stat stat) { 
        try {  
            return new String( zk.getData(path, wa, stat)); 
        } catch ( KeeperException e ) { 
            e.printStackTrace(); 
            return ""; 
        } catch ( InterruptedException e ) { 
            e.printStackTrace(); 
            return ""; 
        } 
    } 
    /** 
     * 只读数据，并不设置watcher
     * @param path path 
     * @return 
     */ 
    public String readData( String path) { 
        try {  
            return new String( zk.getData( path, false, null ) ); 
        } catch ( KeeperException e ) { 
            e.printStackTrace(); 
            return ""; 
        } catch ( InterruptedException e ) { 
            e.printStackTrace(); 
            return ""; 
        } 
    } 
    /** 
     * 写数据
     * @param path 
     * @param data 
     * @return 
     */ 
    public boolean writeData( String path, String data ) { 
        try { 
            Stat st = zk.setData( path, data.getBytes(), -1 );
            if(st != null)
               return true;
            else
               return false;
        } catch ( KeeperException e ) { 
            e.printStackTrace(); 
        } catch ( InterruptedException e ) { 
            e.printStackTrace(); 
        } 
        return false; 
    } 
 
    /** 
     * 删除节点
     * @param path 需要删除的节点路径 
     */ 
    public void deleteNode( String path ) { 
        try { 
            zk.delete( path, -1 ); 
        } catch ( KeeperException e ) { 
            e.printStackTrace(); 
        } catch ( InterruptedException e ) { 
            e.printStackTrace(); 
        } 
    } 
    /**
     * 
     * @param path
     */
    public boolean existNode(String path)
    {
    	Stat st;
		try {
			st = zk.exists(path, false);
	    	if(st == null)
	    		return false;
	    	else
	    		return true;
		} catch (KeeperException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

    }
    /** 
     * 确保链接建立成功后，才能读取
     */ 
    @Override 
    public void process( WatchedEvent event ) { 
        if ( KeeperState.SyncConnected == event.getState() ) { 
            connectedSemaphore.countDown(); 
        }  
    } 
} 
 
