package Phase3withGui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class adminGui {
	static String path=Paths.get(".").toAbsolutePath().normalize().toString();
	protected Shell shell;
	private Text logtext;
	public static String newline = System.getProperty("line.separator");
	private Label lblNewLabel_2;
	private Text userFile;
	private Button openDB;
	static File reconFile;
	File dFile;
	FileWriter reconwriter;
	Crawler crawler;
	private Label label;
	private Text deleteFile;
	private Button deleteDB;
	private Text reconstructFile;
	private Button reconstructDB;
	private Label label_1;
	
	/**
	 * Launch the application.
	 * @param args
	 */


	public adminGui(Crawler startCrawl) {
		this.crawler=startCrawl;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(556, 368);
		shell.setText("SWT Application");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 163, 438, 15);
		lblNewLabel.setText("As Admin, you have access to all user database (open, delete, reconstruct)");
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(10, 10, 238, 15);
		lblNewLabel_1.setText("List of all user in database");
		
		logtext = new Text(shell, SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		logtext.setBounds(10, 31, 521, 115);
		logtext.setEditable(false);
		
		lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setBounds(10, 206, 68, 15);
		lblNewLabel_2.setText("username:");
		
		userFile = new Text(shell, SWT.BORDER);
		userFile.setBounds(84, 198, 174, 23);
		
		openDB = new Button(shell, SWT.NONE);
		openDB.addMouseListener(new MouseAdapter() {
			@Override
			//open up the user file base on username entered
			public void mouseUp(MouseEvent e) {
				String user=userFile.getText();
				try {
					Desktop.getDesktop().open(new File(user+".txt"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					logtext.append("no such file"+newline);
				}
			}
		});
		openDB.setBounds(289, 196, 130, 25);
		openDB.setText("open database");
		
		label = new Label(shell, SWT.NONE);
		label.setText("username:");
		label.setBounds(10, 239, 68, 15);
		
		deleteFile = new Text(shell, SWT.BORDER);
		deleteFile.setBounds(84, 231, 174, 23);
		
		//this button delete the file given
		deleteDB = new Button(shell, SWT.NONE);
		deleteDB.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				String userdb=deleteDB.getText();
				dFile=new File(path+"//"+userdb+".txt");
				//for some reason, this doesn't work 100% of the time
				if(dFile.delete()){
					logtext.append("file deleted"+newline);
				}else{
					logtext.append("file deletion failed"+newline);
				}
				
			}
		});
		deleteDB.setText("delete database");
		deleteDB.setBounds(289, 229, 130, 25);
		
		reconstructFile = new Text(shell, SWT.BORDER);
		reconstructFile.setBounds(84, 265, 174, 23);
		
		reconstructDB = new Button(shell, SWT.NONE);
		reconstructDB.addMouseListener(new MouseAdapter() {
			@Override
			//this method will reconstruct the user file base on log file
			public void mouseUp(MouseEvent e) {
				String userdb=reconstructFile.getText();
				System.out.println(userdb);
				try {
					reconFile = new File(path+"//"+userdb+".txt");
					reconwriter = new FileWriter(reconFile);
					crawler.reconstruct(userdb, reconwriter);
				} catch (IOException e1) {
					System.out.println("something wrong here");
				}
			}
		});
		reconstructDB.setText("reconstruct database");
		reconstructDB.setBounds(289, 263, 130, 25);
		
		label_1 = new Label(shell, SWT.NONE);
		label_1.setText("username:");
		label_1.setBounds(10, 273, 68, 15);
		//this will append all datastored in mysql/local login database 
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection(  
					"jdbc:mysql://localhost:3306/phase3db","root","");  
			System.out.println("connected to mysql...");
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("SELECT * FROM userlogin"); 
			while(rs.next()){
				String str1 = rs.getString("username");
				logtext.append(str1+newline);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("connect failed");
		}

	}

}
