package transfer.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;

import transfer.net.SendThread;

public class SendDialog extends TransferDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//发送文件处理线程
	private SendThread m_send_thread;

	private long m_file_length;
	private String m_file_length_s;
	
	public SendDialog(MainFrame frame,SendThread thread) {
		// TODO Auto-generated constructor stub
		super(frame);
		this.m_send_thread=thread;
		this.setTitle("发送文件");
		init();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		
		m_file_length=m_send_thread.getFileLenght();
		m_file_length_s=getLengthString(m_file_length);
		
		m_file_name_label.setText(m_send_thread.getFileName());
		m_file_length_label.setText(m_file_length_s);
		m_transfer_btn.setText("取消");
		m_transfer_rate_label.setText(" ");
		
		m_send_thread.start();
		
		m_transfer_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JButton btn=(JButton)e.getSource();
				String text=btn.getText();
				if(text.equals("取消")&&m_send_thread.isAlive()){
					m_send_thread.close();
				}
				SendDialog.this.dispose();
			}
		});
		new Thread(){
			public void run() {
				double progress=0.0;
				m_transfer_rate_label.setText("等待对方接收");
				m_progress_bar.setValue((int)(m_send_thread.getProgress()*100.0));
				while(!m_send_thread.isEnd()){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					progress=m_send_thread.getProgress();
					String s=getLengthString((long)(m_file_length*progress));
					m_file_length_label.setText(s+"/"+m_file_length_s);
//					m_transfer_rate_label.setText(df.format((double)m_send_thread.getSendRate()/(double)(1024*1024))+" MB/S");
					m_transfer_rate_label.setText(getLengthString(m_send_thread.getSendRate())+"/S");
					m_progress_bar.setValue((int)(progress*100.0));
				}
				
				if((int)(m_send_thread.getProgress()*100.0)!=100){
					m_transfer_rate_label.setText("未完成！");
					m_transfer_rate_label.setForeground(Color.red);
				}else{
					m_transfer_rate_label.setText("已完成！");
					m_transfer_rate_label.setForeground(Color.green);
					m_progress_bar.setValue(100);
					m_frame.getTransferManager().saveLog("\n"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
										+"\t发送文件\n"
										+m_send_thread.getFileName()
										+ "\n----------------------------------------------");
				}
				m_transfer_btn.setText("关闭");
				
			};
		}.start();
	}

}













































