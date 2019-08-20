package Phase3withGui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.awt.Desktop;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Gui {
	String username;
	Crawler startCrawl;
	protected Shell shell;
	private Text searchbar;
	static Text logtext;
	private Label lblNewLabel_1;
	private Text modifyTitleT;
	private Text removeDataT;
	private Text modifyAuthor;
	private Text ModifyImgPT;
	private Text ModID;
	private Text password;
	private Text email;
	private Text sTime;
	private Text eTime;

	public Gui(String username2, Crawler crawler) {
		this.username=username2;
		this.startCrawl=crawler;
		startCrawl.guirunning=true;
	}

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
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
		try {
			//before the gui closes, update database
			startCrawl.writeToUserFile();
			Main.userwriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create contents of the window.
	 */
	public void createContents() {
		shell = new Shell();
		shell.setSize(806, 618);
		shell.setText("SWT Application");
		
		searchbar = new Text(shell, SWT.BORDER);
		searchbar.setBounds(10, 29, 688, 25);
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 331, 15);
		lblNewLabel.setText("What would you like to search on bookdepository.com");
		
		Button searchbutton = new Button(shell, SWT.NONE);
		searchbutton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		
		searchbutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String searched=searchbar.getText();
				try {
					//this search button will get user input and search it on https://www.bookdepository.com/
					startCrawl.crawl("https://www.bookdepository.com/search?searchTerm="+searched+"&search=Find+book");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			
		});
		searchbutton.setBounds(704, 29, 75, 25);
		searchbutton.setText("Search");
		
		logtext = new Text(shell, SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		logtext.setText("All data searched will be automatically added to your database(except guest account)\n");
		logtext.setBounds(10, 80, 770, 194);
		logtext.setEditable(false);
		
		lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(10, 60, 312, 15);
		lblNewLabel_1.setText("Log box :");
		
		Label lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setBounds(10, 337, 769, 15);
		lblNewLabel_2.setText("Database: You can either remove data by providing Book ID (easiler to copy and paste from logbox OR modify data by entering new data.");
		
		removeDataT = new Text(shell, SWT.BORDER);
		removeDataT.setBounds(10, 380, 271, 21);
		
		Button removeDataB = new Button(shell, SWT.NONE);
		removeDataB.addMouseListener(new MouseAdapter() {
			@Override
			//this button will remove the all data linked to ID
			public void mouseUp(MouseEvent e) {
				if(username=="guest"){
					logtext.append("not availiable for GUEST account\n");
				}
				else{
				try {
					startCrawl.remove(removeDataT.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				}
			}
		});
		removeDataB.setBounds(287, 378, 75, 25);
		removeDataB.setText("Remove");
		
		Label lblNewLabel_3 = new Label(shell, SWT.NONE);
		lblNewLabel_3.setBounds(10, 359, 55, 15);
		lblNewLabel_3.setText("ID");
		
		Label lblNewLabel_4 = new Label(shell, SWT.NONE);
		lblNewLabel_4.setBounds(399, 407, 55, 15);
		lblNewLabel_4.setText("Title");
		
		Label lblUrl = new Label(shell, SWT.NONE);
		lblUrl.setText("Author");
		lblUrl.setBounds(399, 455, 55, 15);
		
		Label lblImagepath = new Label(shell, SWT.NONE);
		lblImagepath.setBounds(399, 503, 84, 15);
		lblImagepath.setText("ImagePath");
		
		ModID = new Text(shell, SWT.BORDER);
		ModID.setBounds(399, 380, 380, 21);
		
		modifyTitleT = new Text(shell, SWT.BORDER);
		modifyTitleT.setBounds(399, 428, 380, 21);
		
		modifyAuthor = new Text(shell, SWT.BORDER);
		modifyAuthor.setBounds(399, 476, 380, 21);
		
		ModifyImgPT = new Text(shell, SWT.BORDER);
		ModifyImgPT.setBounds(399, 524, 380, 21);
		
		Button ModifyB = new Button(shell, SWT.NONE);
		ModifyB.addMouseListener(new MouseAdapter() {
			@Override
			//this method get information and change it to database
			public void mouseUp(MouseEvent e) {
				if(username=="guest"){
					logtext.append("not availiable for GUEST account\n");
				}
				else{
				try {
					startCrawl.modify(ModID.getText(),modifyTitleT.getText(), modifyAuthor.getText(), ModifyImgPT.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				}
			}
		});
		ModifyB.setBounds(551, 551, 75, 25);
		ModifyB.setText("Modify");
		
		Label lblNewLabel_6 = new Label(shell, SWT.NONE);
		lblNewLabel_6.setBounds(399, 359, 55, 15);
		lblNewLabel_6.setText("ID");
		//this button updates and shows the database
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(username=="guest"){
					logtext.append("not availiable for GUEST account\n");
				}
				else{
					try {
						startCrawl.writeToUserFile();
						Main.userwriter.flush();
						Desktop.getDesktop().open(Main.oFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}	
		});
		btnNewButton.setBounds(680, 551, 100, 25);
		btnNewButton.setText("Show database");
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			//this button opens the log file (only for admin)
			public void mouseDown(MouseEvent e) {
				if(username=="admin"){
				try {
					Desktop.getDesktop().open(Main.lFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				}else logtext.append("this function for admin only\n");
			}
		});
		btnNewButton_1.setBounds(117, 551, 111, 25);
		btnNewButton_1.setText("show log (admin)\n");
		
		Label lblEmail = new Label(shell, SWT.NONE);
		lblEmail.setBounds(10, 292, 55, 15);
		lblEmail.setText("email");
		
		Label lblPassword = new Label(shell, SWT.NONE);
		lblPassword.setBounds(10, 316, 55, 15);
		lblPassword.setText("password");
		email = new Text(shell, SWT.BORDER);
		email.setBounds(87, 287, 486, 20);
		password = new Text(shell, SWT.BORDER);
		password.setBounds(87, 311, 486, 20);
		
		Button btnNewButton_2 = new Button(shell, SWT.NONE);
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				//get user email and password entered in gui
				String userEmail=email.getText();
				String userPassword=password.getText();
				if(userEmail.equals("")||userPassword.equals("")){
					logtext.append("email and password can't be empty\n");
				}else{
					//get the email host
					int ending=userEmail.lastIndexOf("@");
					String host=userEmail.substring(ending+1);
					System.out.println(userPassword);
					//email must end with @gmail.com
					if(host.equals("gmail.com")){
						//send information to Class sendEmail
						sendEmail sender=new sendEmail();
						sender.run(userEmail,userPassword);
					}else{
						logtext.append("email provided must be gmail\n");
					}
				}
			}
		});
		btnNewButton_2.setBounds(605, 286, 174, 45);
		btnNewButton_2.setText("Send Database to Email");
		
		Label lblNewLabel_5 = new Label(shell, SWT.NONE);
		lblNewLabel_5.setBounds(10, 417, 249, 15);
		lblNewLabel_5.setText("Get data between time frame (YYYY-MM-DD)");
		
		Label lblStartTime = new Label(shell, SWT.NONE);
		lblStartTime.setBounds(10, 438, 55, 15);
		lblStartTime.setText("start time");
		
		Label lblEndTime = new Label(shell, SWT.NONE);
		lblEndTime.setBounds(148, 438, 55, 15);
		lblEndTime.setText("end time");
		
		sTime = new Text(shell, SWT.BORDER);
		sTime.setBounds(10, 455, 111, 21);
		
		eTime = new Text(shell, SWT.BORDER);
		eTime.setBounds(148, 455, 111, 21);
		//this method will get user input times and get all data that are stored between input times
		Button searchTime = new Button(shell, SWT.NONE);
		searchTime.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				String startTime=sTime.getText();
				String endTime=eTime.getText();
				System.out.println(startTime+endTime);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date date1 = sdf.parse(startTime);
					Date date2 = sdf.parse(endTime);
				    startCrawl.getTime(date1,date2);
				} catch (ParseException e1) {
					logtext.append("wrong format"+"\n");
				}
			}
		});
		searchTime.setBounds(287, 450, 75, 25);
		searchTime.setText("Search");
		//this button will open up admin gui
		Button openAdminGui = new Button(shell, SWT.NONE);
		openAdminGui.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(username.equals("admin")){
				try {
					adminGui window = new adminGui(startCrawl);
					window.open();
				} catch (Exception ee) {
					ee.printStackTrace();
				}
				}
				else logtext.append("admin only"+"\n");
			}
		});
		openAdminGui.setBounds(10, 551, 101, 25);
		openAdminGui.setText("admin access");
		
		
		
		
	}
	//append searched results to logbox
	public void appendmessage(String string) {
		logtext.append(string+"\n");
	}
}
