package ppke.kripto;



import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import ppke.kripto.Client.State;

public class ClientGUI{
	
	
	
	//Foablak
	private JFrame mainFrame;
	//Informaciot mutato ablak, pl a logger osztaly itt irhatja ki a dolgokat
	private JPanel infoPanel;
	//Szovegbevitelt tartalmazo ablak
	private JPanel mainPanel;
	//Gombokat tartalmazo ablak
	private JPanel mainPanel2;
	//Gombok
	private JButton OK;
	private JButton Clear;
	private JButton Quit;
	//Szovegbevitel
	private JTextField userName;
	private JTextField passWord;
	private JTextField ipAddress;
	private JTextField urlUsername;
	private JTextField urlAddress;
	private JTextField urlPassword;
	//Infoscreen
	private JTextArea infoScreen;
	//Lapfulek
	private JMenuBar menubar;
	
	public ClientGUI() {
		Runnable runner = new Runnable() {
			public void run() {
				initGUI();
			}
		};
		EventQueue.invokeLater(runner);
	}
	


	
	
	
	private void initGUI()
	{
		mainFrame = new JFrame("PasswordSafe");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(new Dimension(400, 700));
		
		
		userName = new JTextField(20);
		
		passWord = new JTextField(20);

		ipAddress = new JTextField(20);
		
		urlUsername = new JTextField(20);
		
		urlAddress = new JTextField(20);
		
		urlPassword = new JTextField(20);
		
		
	    menubar = new JMenuBar();
		JMenu filemenu = new JMenu("File");
		JMenuItem exitMenu = new JMenuItem("Exit");
		exitMenu.addActionListener(new ActionListener() { //Anonymous Inner Class hasznalata
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		filemenu.add(exitMenu);
		menubar.add(filemenu);
		mainFrame.setJMenuBar(menubar);
		
		infoPanel = new JPanel();
		infoScreen = new JTextArea();
		infoPanel.setLayout(new FlowLayout());
		infoPanel.setSize(300,200);
		infoPanel.add(infoScreen);
		
		OK = new JButton("OK");
		OK.addActionListener(new ActionListener() { //Anonymous Inner Class hasznalata
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//infoScreen.setText-el lehet majd kiiratni a cuccokat.
				Client.setUsername(userName.getText());
				Client.masterkey = passWord.getText();
				Client.urlUsername = urlUsername.getText();
				Client.urlPassword = urlPassword.getText();
				Client.urlAddress = urlAddress.getText();
			
				Client.state = State.req;
				System.out.println(Client.state);
			}
		});  

		Clear = new JButton("Clear");
		Clear.addActionListener(new ActionListener() { //Anonymous Inner Class hasznalata
			@Override
			public void actionPerformed(ActionEvent arg0) {
				userName.setText(null);
				passWord.setText(null);
				ipAddress.setText(null);	
				urlAddress.setText(null);
				urlUsername.setText(null);
				urlPassword.setText(null);
			}
		});
		
		Quit = new JButton("Quit");
		Quit.addActionListener(new ActionListener() { //Anonymous Inner Class hasznalata
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	
		
		
		mainPanel = new JPanel();
		mainPanel.setSize(300,200);
		mainPanel.setLayout(new GridLayout(6,2,5,5));
		
		JLabel label = new JLabel("Username: ");
		label.setLayout(new FlowLayout(10,100,5));
        label.setLabelFor(userName);
        label.add(userName);
        mainPanel.add(label);
      
       
        label = new JLabel("Password: ");
        label.setLayout(new FlowLayout(10,100,5));
        label.setLabelFor(passWord);
        label.add(passWord);
        mainPanel.add(label);
		
		
		label = new JLabel("IP address: ");
		label.setLayout(new FlowLayout(10,100,5));
        label.setLabelFor(ipAddress);
        label.add(ipAddress);
        mainPanel.add(label);
		
        
        label = new JLabel("Url username: ");
		label.setLayout(new FlowLayout(10,100,5));
        label.setLabelFor(urlUsername);
        label.add(urlUsername);
        mainPanel.add(label);
        
        
        label = new JLabel("Url address: ");
		label.setLayout(new FlowLayout(10,100,5));
        label.setLabelFor(urlAddress);
        label.add(urlAddress);
        mainPanel.add(label);
        
        
        label = new JLabel("Url password: ");
		label.setLayout(new FlowLayout(10,100,5));
        label.setLabelFor(urlPassword);
        label.add(urlPassword);
        mainPanel.add(label);
        
		
		mainPanel2 = new JPanel();
		mainPanel2.setLayout(new FlowLayout());
		mainPanel2.add(OK);
		mainPanel2.add(Clear);
		mainPanel2.add(Quit);
		
		
		Container container = mainFrame.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(mainPanel);
		container.add(mainPanel2);
		container.add(infoPanel);
		
		mainFrame.setVisible(true);
		
	}

	
	public void getXMLcontent(StringBuilder builder){
		
	}
	
	
	public static void main(String[] args) {
		new ClientGUI();
	}
	
}
