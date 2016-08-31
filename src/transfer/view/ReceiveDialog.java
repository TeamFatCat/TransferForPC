package transfer.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;

import transfer.net.ReceiveThread;

public class ReceiveDialog extends TransferDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ReceiveThread m_receive_thread;

	private long m_file_length;
	private String m_file_length_s;
	
	public ReceiveDialog(MainFrame frame,ReceiveThread thread) {
		// TODO Auto-generated constructor stub
		super(frame);
		this.m_receive_thread=thread;
		this.setTitle("接收文件");
		init();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		
		m_file_length=m_receive_thread.getFileLenght();
		m_file_length_s=getLengthString(m_file_length);
		
		m_file_name_label.setText(m_receive_thread.getFileName());
		m_file_length_label.setText(m_file_length_s);
		m_transfer_rate_label.setText("");
		m_transfer_btn.setText("接收");
		m_transfer_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				JButton btn=(JButton) arg0.getSource();
				String text=btn.getText();
				if(text.equals("接收")){
					m_transfer_btn.setText("取消");
					m_receive_thread.start();
					new Thread(){
						public void run() {
							while(!m_receive_thread.isEnd()){
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								double progress=m_receive_thread.getProgress();
								String s=getLengthString((long)(m_file_length*progress));
								m_file_length_label.setText(s+"/"+m_file_length_s);
								m_transfer_rate_label.setText(getLengthString(m_receive_thread.getReceiveRate())+"/S");
								m_progress_bar.setValue((int)(progress*100.0));
							}
							
							if((int)(m_receive_thread.getProgress()*100.0)!=100){
								m_transfer_rate_label.setText("未完成！");
								m_transfer_rate_label.setForeground(Color.red);
							}else{
								m_transfer_rate_label.setText("已完成！");
								m_transfer_rate_label.setForeground(Color.green);
								m_progress_bar.setValue(100);
								m_frame.getTransferManager().saveLog("\n"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
													+"\t接收文件\n"
													+m_receive_thread.getFileName()
													+ "\n----------------------------------------------");
							}
							
							m_transfer_btn.setText("关闭");
							
						};
					}.start();
				}else if(text.equals("取消")){
					if(m_receive_thread!=null){
						m_receive_thread.close();
					}
					ReceiveDialog.this.dispose();
				}else if(text.equals("关闭")){
					ReceiveDialog.this.dispose();
				}
				
			}
		});
	}
	
}






























