package transfer.scan;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transfer.control.TransferManager;

public class SearchTool {
	
	/**
	 * windows平台的ping命令
	 */
	public static final String WINDOWS_CMD_PING="cmd /c ping -a ";
	
	/**
	 * windows命令，获取本局域网中的所有IP地址和物理地址
	 */
	public static final String WINDOWS_CMD_ARP="arp -a	";
	
	
	private SearchEndOper se;
	
	private String ip;

	private int count;

	private HashMap<String, String> res_map=new HashMap<String, String>();

	private synchronized void addNum(){
		count++;						
		if(count==252){
			se.doIPMapWork(res_map);
		}
	}
	
	private synchronized void reNum(){
		count=0;
	}

	
	public SearchTool(SearchEndOper se,String ip) {
		// TODO Auto-generated constructor stub
		this.se=se;
		this.ip=ip;
		count=0;
	}
	
	public static synchronized String[] changeListToArray(List<String> list){
		String[] ip_arr=null;
		if(list==null||list.size()==0){
			return null;
		}		
		ip_arr=new String[list.size()];
		int i=0;
		for(String s:list){
			ip_arr[i++]=s;
		}		
		return ip_arr;
	}
	
	//搜索可用地址
	public void searchNetMap(){
		new Thread(){
			public void run() {
				res_map.clear();
				final String ip_top=ip.substring(0,ip.lastIndexOf('.'));
				reNum();
				
				//每个线程检测16个地址
				for(int i=1;i<255;i+=16){
					final int w=i;
					new Thread(){
						public void run() {
							try {
								int len=0;
								byte[] data=new byte[8192];
								for(int i=w;i<255&&i<w+16;i++){
									try {
										String s=ip_top+"."+i;
										if(s.equals(ip)) continue;

										Socket sc=new Socket();
										sc.connect(new InetSocketAddress(s, TransferManager.port),200);

										//获取对方信息
										DataInputStream dis=new DataInputStream(new BufferedInputStream(sc.getInputStream()));
										String hostName=s;
										if((len=dis.read(data))!=-1){
											hostName=new String(data,0,len,"GBK");
										}				
										if(hostName.length()>13){
											hostName=hostName.substring(0, 13)+"...";//名称太长时截取
										}
										res_map.put(hostName, s);
										
										sc.close();
										
									} catch (IOException e) {
										// TODO Auto-generated catch block
									}
									addNum();	
								}	
							} catch (Exception e) {
								// TODO Auto-generated catch block
							}
						};
					}.start();
				}
			};
		}.start();
	}
	
}








































