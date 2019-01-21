package com.rongf.ticket.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.codec.binary.Base64;

public class ImageUtils {
	public volatile static String randSelected = "";
	
	public static String deCodeImage(String imageString, String path) {
		// 解密
		byte[] imageBytes = Base64.decodeBase64(imageString);
		// 处理数据
		for (int i = 0; i < imageBytes.length; i++)
			if (imageBytes[i] < 0)
				imageBytes[i] += 256;
		
		randSelected = "";
		printImage(imageBytes);
		
		while (randSelected.isEmpty());
		
		return randSelected;
	}
	
	public static void printImage(byte[] imageBytes) {
		JFrame imageFrame = new JFrame("图片验证码");
		// 定义背景
		ImageIcon img = new ImageIcon(imageBytes);
		JLabel background = new JLabel(img);
		background.setBounds(0, 0, img.getIconWidth(), img.getIconHeight()); 
		imageFrame.getLayeredPane().setLayout(null);
		imageFrame.getLayeredPane().add(background, Integer.MIN_VALUE); 
		// 定义面板
		JPanel contentPanel = (JPanel) imageFrame.getContentPane();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(null);

		// 定义弹窗大小
		imageFrame.setBounds(100, 100, img.getIconWidth(), img.getIconHeight() + 30);
		imageFrame.setResizable(false);
		// 显示弹窗
		imageFrame.setVisible(true);
		
		StringBuilder randCode = new StringBuilder();
		imageFrame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				randCode.append(e.getX() + "," + (e.getY() - 55) + ",");
			}
		});
		
		imageFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (randCode.length() == 0)
					randSelected = "stop";
				else
					randSelected = randCode.substring(0, randCode.length() - 1);
			}
			
		});
		
	    
	}
}
