package transfer.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import transfer.net.ReceiveThread;
import transfer.net.ServerThread;
import transfer.view.MainFrame;
import transfer.view.ReceiveDialog;

/**
 * 文件传输管理类
 * @author EsauLu
 *
 */
public class TransferManager {
	
	
	//服务器线程
	private ServerThread m_server_thread;
	private ServerThread.ReceiveOper m_receive_oper;
	
	//默认保存路径
	private String m_default_save_path;
	
	//日志文件
	private File m_log_file;
	private BufferedWriter m_log_os;
	
	public static final int port=6602;
	
	//用户界面
	private MainFrame m_frame;
	
	//配置文件路径
	private String m_properties_file;
	
	/**
	 * 单例
	 */
	private static TransferManager instance=null;

	/**
	 * 构造函数
	 */
	private TransferManager() {
		// TODO Auto-generated constructor stub
		init();
//		m_frame.setVisible(true);
	}
	
	/**
	 * 获取TransferManager实例
	 * @return 返回一个TransferManager实例
	 */
	public static synchronized TransferManager getInstance() {
		if(instance==null){
			instance=new TransferManager();
		}
		return instance;
	}
	
	private void init() {
		// TODO Auto-generated method stub
		
		m_default_save_path="C:\\transfer";
		m_properties_file="setting.properties";
		
		Properties properties=loadProperties();
		
		//等待线程
		//=========================================================================================
		m_server_thread=ServerThread.createServerThread(getCurrenIP(),port, properties.getProperty("file_path"),properties.getProperty("host_name"));
		m_receive_oper=new ServerThread.ReceiveOper() {
			@Override
			public void operate(ReceiveThread receive) {
				// TODO Auto-generated method stub
				new ReceiveDialog(m_frame, receive);
			}
		};
		m_server_thread.registerReceiveOper(m_receive_oper);
		m_server_thread.start();
		//=========================================================================================
		
		m_frame=new MainFrame(this);
		initLog();		
		createAP();		
	}
	
	/**
	 * 创建热点
	 */
	public void createAP(){
//		File file=new File("createAP.bat");
//		if(file.exists()){
//			return ;
//		}
//		new Thread(){
//			public void run() {
//				try{
//					
//					String ssid=TransferUtils.convert("MyWifiApDemo"+m_server_thread.getHostName());
//					String psd="12345678";
//					Runtime r=Runtime.getRuntime();
//					r.exec("netsh wlan stop hostednetwork");
//					r.exec("netsh wlan set hostednetwork mode=allow ssid="+ssid+" key="+psd);
//					r.exec("netsh wlan start hostednetwork");
//					
//				}catch(Exception e){
//					
//				}
//			};
//		}.start();
	}
	
	/**
	 * 关闭热点
	 */
	public void stopAP(){
//		try{
//			Runtime r=Runtime.getRuntime();
//			r.exec("netsh wlan stop hostednetwork");			
//		}catch(Exception e){
//			
//		}
	}
	
	private void initLog() {
		// TODO Auto-generated method stub
		m_log_file=new File("log.txt");
		try {
			while(!m_log_file.exists()){
				m_log_file.createNewFile();
			}
			m_log_os=new BufferedWriter(new FileWriter(m_log_file,true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(){
			public void run() {
				String line=null;
				try {
					BufferedReader m_log_is=new BufferedReader(new InputStreamReader(new FileInputStream(m_log_file)));
					while((line=m_log_is.readLine())!=null){
						m_frame.addLog(line);
					}
					m_log_is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			};
		}.start();
		
	}
	
	/**
	 * 保存日志
	 * @param log
	 */
	public void saveLog(String log){
		try {
			m_log_os.newLine();
			m_log_os.write(log);
			m_log_os.flush();
			m_frame.addLog(log);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 清空日志
	 */
	public void clearLog(){
		try {
			if(m_log_os!=null){
				m_log_os.close();
			}
			if(m_log_file.exists()){
				m_log_file.delete();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		try {
			m_log_file=new File("log.txt");
			while(!m_log_file.exists()){
				m_log_file.createNewFile();
			}
			m_log_os=new BufferedWriter(new FileWriter(m_log_file,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	/**
	 * 获取本地IP列表 
	 * @return 返回本地IP列表 
	 */
	 public static List<String> getLocalIPList() {
	        List<String> ipList = new ArrayList<String>();
	        try {
	            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	            NetworkInterface networkInterface;
	            Enumeration<InetAddress> inetAddresses;
	            InetAddress inetAddress;
	            String ip;
	            while (networkInterfaces.hasMoreElements()) {
	                networkInterface = networkInterfaces.nextElement();
	                inetAddresses = networkInterface.getInetAddresses();
	                while (inetAddresses.hasMoreElements()) {
	                    inetAddress = inetAddresses.nextElement();
	                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
	                        ip = inetAddress.getHostAddress();
	                        ipList.add(ip);
	                    }
	                }
	            }
	        } catch (SocketException e) {
	            e.printStackTrace();
	        }
	        return ipList;
	 } 	 
	 
	 /**
	  * 更改服务器等待线程
	  * @param ip
	  * @param port
	  * @param filePath
	  */
	 public boolean changeServerThread(String ip,int port,String filePath,String name) {
		m_server_thread.close();
		m_server_thread=null;
		try {
			while(m_server_thread==null){
				m_server_thread = ServerThread.createServerThread(ip, port, filePath,name);
			}
			m_server_thread.registerReceiveOper(m_receive_oper);
			m_server_thread.start();
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	 }
	 
	 /**
	  * 默认设置
	  */
	 public Properties setDefaultProperty(){	 
		 Properties p=new Properties();	
		 try {
			  File file=new File(m_properties_file);
			  file.createNewFile();
			  FileOutputStream out=new FileOutputStream( file);
			  InetAddress ad=InetAddress.getLocalHost();
			  p.setProperty("file_path", m_default_save_path);
			  if(ad!=null){
				  p.setProperty("host_name", ad.getHostName());
			  }else{
			 	  p.setProperty("host_name", "null");
			  }
			  p.store(out, "Comment");
		 } catch (Exception e) {
		  	  // TODO Auto-generated catch block
		 }
		 return p;
	 }
	 
	 /**
	  * 加载程序配置
	  * @return
	  */
	 public Properties loadProperties(){		 
		 Properties p=new Properties();
		 try {
			 File file=new File(m_properties_file);
			 if(!file.exists()){
				 return setDefaultProperty();
			 }
			 FileInputStream in=new FileInputStream( file);
			 p.load(in);
		 } catch (Exception e) {
			// TODO Auto-generated catch block
		 }
		 return p;
	 }
	 
	 /**
	  * 保存程序设置
	  * @param property 设置列表
	  */
	 public void saveProperties(HashMap<String , String > property){		 
		 Properties p=new Properties();		
		 for(String key:property.keySet()){
			 p.setProperty(key, property.get(key));
		 }
		 try {
			FileOutputStream out=new FileOutputStream(m_properties_file);
			p.store(out, "Comment");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	 }
	 
	 /**
	  * 获取当前IP
	  * @return IP
	  */
	 private String getCurrenIP() {
		// TODO Auto-generated method stub
		String[] ips= getIpArray(getLocalIPList());
		String curr_ip="0.0.0.0";
		
		if(ips!=null){
			curr_ip=ips[0];
		}else{
			try {
				InetAddress net=InetAddress.getLocalHost();
				if(net!=null){
					curr_ip=net.getHostAddress();
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
		
		return curr_ip;
	}
		
	/**
	* 将ip列表转换成数组
	* @param list 用List记录的ip列表
	* @return 返回用数组形式的ip列表
	*/
	public String[] getIpArray(List<String> list){
		if(list==null){
			return null;
		}
		String[] arr=new String[list.size()];
		int i=0,j=0;
		for(String s:list){
			arr[i++]=s;
			if(s.matches("192\\.168\\.10*.\\d{1,3}")){
				j=i-1;
			}
		}
		String tem=arr[0];
		arr[0]=arr[j];
		arr[j]=tem;
		return arr;
	}
	 
	/**
	 * 获取当前IP
	 * @return 返回IP字符串值
	 */
	public String getIp() {
		return m_server_thread.getIP();
	}
	
	/**
	 * 获取当前开放的端口号
	 * @return 端口号
	 */
	public int getPort() {
		return m_server_thread.getPort();
	}
	
	/**
	 * 返回文件保存路径
	 * @return 路径
	 */
	public String getFileSavePath(){
		return m_server_thread.getFileSavaPath();
	}
	
	/**
	 * 返回默认文件保存路径
	 * @return 默认路径
	 */
	public String getDefaultSavePath() {
		return m_default_save_path;
	}
	
	/**
	 * 设置文件保存路径
	 * @param path 路径
	 */
	public void setFileSavePath(String path){
		m_server_thread.setFileSavaPath(path);
	}
	
	/**
	 * 返回主机名
	 * @return
	 */
	public String getHostName(){
		return m_server_thread.getHostName();
	}
	
	/**
	 * 返回主机名
	 * @return
	 */
	public void setHostName(String name){
		m_server_thread.setHostName(name);
	}
	
	
	 
}





































