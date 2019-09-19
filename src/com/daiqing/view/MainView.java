package com.daiqing.view;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument.BranchElement;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class MainView extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String fileName = (new Date().getMonth() + 1) + "_"
										+ new Date().getDate() + "_"
										+ new Date().getHours() + "_" 
										+ new Date().getMinutes() 
										+ "CheckIn.csv";
	private HashMap<String,	Boolean> flagMap; 
	private JButton okButton;
	private JButton finishButton;
	private JTextField idStudent;
	private TakePhoto camera; 
	private Thread tCam;
	
	public MainView() throws Exception, IOException{
		super();
		this.setLayout(null);
		this.setTitle("Call the Roll");
		this.setBounds(900, 600, 405, 200);
		
		okButton = new JButton("ok");
		okButton.setFont(new Font(null,Font.BOLD, 30));
		okButton.setBounds(240, 35, 100, 50);
		okButton.addActionListener(this);
		this.add(okButton);
		okButton.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					e.consume();
					inputFinsih();
				}
			}
		});
		
		idStudent = new JTextField();
		idStudent.setFont(new Font(null, Font.PLAIN, 25));
		idStudent.setBounds(20, 35, 205, 50);
		this.add(idStudent);
		//设置关闭保存操作
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				ArrayList<String> students = new ArrayList<>();
				Iterator<String> it = flagMap.keySet().iterator();
				while(it.hasNext()){
					students.add(it.next().toString());
				}
				for (String s : students)
				{
					if (!flagMap.get(s))
						try {
							idStudent.setText(s);
							saveIntoFile(s.trim(),false);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
				System.exit(0);
			}
		});
		
		camera = new TakePhoto();
		tCam = new Thread(camera);
		tCam.start();

		flagMap = new HashMap<>();
		initFile();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void initFile() throws IOException {
		File file = new File("student.txt");
		BufferedReader bufferedReader 
			= new BufferedReader(new FileReader(file));
		String stuId;
		while((stuId = bufferedReader.readLine()) != null){
			System.out.println(stuId);
			flagMap.put(stuId.trim(), false);
		}
		bufferedReader.close();
	}

	public void showMessageDialog(String message,String status){
		JLabel jLabel = new JLabel(message);
		jLabel.setFont(new Font(null, Font.BOLD, 20));
		JOptionPane
			.showMessageDialog(null,jLabel,status,
							JOptionPane.PLAIN_MESSAGE);
	}
	
	//response to OK Button
	private void inputFinsih(){
		try {
			if (!saveInfo())
				return;
			camera.setStudentId(idStudent.getText().trim());
			camera.setSave(true);
		} catch (IOException e1) {
			System.out.println("save data error " + idStudent.getText().trim());
			e1.printStackTrace();
		}
		showMessageDialog("Add Success", "OK");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton)
		{
			this.inputFinsih();
		}else if (e.getSource() == finishButton){
			
		}
	}
	//check info and to Save
	private boolean saveInfo() throws IOException {		
		String idStu = idStudent.getText();
		
		if (!flagMap.containsKey(idStu.trim())){
			showMessageDialog("stuId error , input again", "Error");
			return false;
		}
		if (flagMap.get(idStu.trim())){
			showMessageDialog("you have entered your id", "OK");
			return false;
		}
		flagMap.put(idStu.trim(), true);
		saveIntoFile(idStu.trim(),true);
		return true;
	}
	
	//Save into File
	private void saveIntoFile(String info,boolean flag) throws IOException {
		File file = new File(fileName);
		if (!file.exists())
			file.createNewFile();
		PrintWriter pw = new PrintWriter
				(new BufferedWriter(new FileWriter(file,true)));
		if (flag)
			pw.write(info + "," + " 1\r\n");
		else
			pw.write(info + "," + " 0\r\n");
		pw.close();
	}
	
	public static void main(String[] args) throws Exception, IOException{
		new MainView().setVisible(true);
	}
}
