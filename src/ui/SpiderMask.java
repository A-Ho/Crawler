package ui;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFileChooser;
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

        setTitle("Super AI Spider");

        JPanel panel = new JPanel();

        JTextArea area = new JTextArea("text area");
        area.setPreferredSize(new Dimension(100, 100));
        
        JFileChooser fc = new JFileChooser();
//        panel.add(fc);
        
        JLabel urlLabel = new JLabel("請輸入起始URL：");
        JLabel depthLabel = new JLabel("請輸入爬行深度：");
//        JLabel ouputLabel = new JLabel("請選擇輸出目錄：");
        
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
        
        JButton outputButton = new JButton("選擇輸出目錄");
        panel.add(outputButton);
        panel.add(outputTf);
        
        JButton button = new JButton("開始");
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
