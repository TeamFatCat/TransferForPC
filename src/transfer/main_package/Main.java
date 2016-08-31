package transfer.main_package;

import java.awt.Font;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import transfer.control.TransferManager;
import transfer.view.TransferDialog;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		setDefaultConfig();
		TransferManager.getInstance();
	}
	
	private static void setDefaultConfig(){
		Font font_1=new Font("黑体", Font.PLAIN, 14);//字体
		Font font_2=new Font("黑体", Font.PLAIN, 18);//字体		
		
		//文字抗锯齿
		System.setProperty("awt.useSystemAAFontSettings", "on"); 
		System.setProperty("swing.aatext", "true");
		
		
		//默认UI风格
		UIManager.put("OptionPane.font", font_1);
		UIManager.put("Button.font", font_2);
		UIManager.put("Label.font", font_2);
		UIManager.put("TextField.font", font_2);
		UIManager.put("TextArea.font", font_1);
		UIManager.put("ComboBox.font", font_2);
		UIManager.put("List.font", font_2);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}

}
