/**
 * Copyright (c) 2013, A-Ho, sean666666@gmail.com
 */
package aho.crawler.ui;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SpiderMask extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1054500854070462810L;

	public SpiderMask() {

        setTitle("Super Web Crawler");

        JPanel panel = new JPanel();

        JTextArea area = new JTextArea("text area");
        area.setPreferredSize(new Dimension(100, 100));
        
//        JFileChooser fc = new JFileChooser();
//        panel.add(fc);
        
        JLabel urlLabel = new JLabel("Enter start URI：");
        JLabel depthLabel = new JLabel("Enter max spiding depth：");
//        JLabel ouputLabel = new JLabel("Enter output dir：");
        
        JTextField urlTf = new JTextField();
        JTextField depthTf = new JTextField();
        JTextField outputTf = new JTextField();
        urlTf.setPreferredSize(new Dimension(300,30));
        depthTf.setPreferredSize(new Dimension(300,30));
        outputTf.setPreferredSize(new Dimension(300,30));
        
        
        panel.add(urlLabel);
        panel.add(urlTf);
        panel.add(depthLabel);
        panel.add(depthTf);
        
        JButton outputButton = new JButton("Output directory:");
        panel.add(outputButton);
        panel.add(outputTf);
        
        JButton button = new JButton("Run");
        panel.add(button);

        add(panel);

        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SpiderMask();
    }
}
