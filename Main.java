
package Phase3withGui;
import java.sql.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
//in order to run this project, user need all the jar file that i imported including
//javax.mail.jar
//activation.jar
//mysql-connector-java-8.0.13.jar
//org.eclipse.swt.win32.win32.x86_64_3.105.3.v20170228-0512.jar
public class Main {
	//newline string
	public static String newline = System.getProperty("line.separator");
	//declare all needed files
	static File userLoginFile;
	static File iFile;
	static File oFile;
	static File lFile;
	static FileWriter logwriter;
	static FileWriter userwriter;
	//username will keep track of the user using this project
	static String username=null;
	static String flagArg="";
	static String path="";
	//this is crawler class
	static Crawler Crawler;
	static int choice;
	//this hashmap stores the local file login database
	static HashMap<String, String> userLoginDB = new HashMap<String, String>();
	
	//this method checks if user provided username and password matches the pairs in database
	private static boolean check(String username, String password) {
	if (!userLoginDB.containsKey(username)){
		System.out.println("invalid username");
		return false;
	}
	else if(!userLoginDB.get(username).equals(password)) {
		System.out.println("wrong password");
		return false;
	}
		System.out.println("login success");
		return true;
	}
	//this method checks if user provided username and password matches the pairs in MySQL database
	private static boolean checkUsingMySQL(String username, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection(  
					"jdbc:mysql://localhost:3306/phase3db","root","");  
			System.out.println("connected to mysql...");
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("SELECT * FROM userlogin WHERE username='"+username+"' AND password='"+password+"'"); 
			if(rs.next()){
				//System.out.println("login success");
				return true;
			}else{
				System.out.println("username and password pair not in database");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("connect failed");
		}
		return false;
	}
	//this method connect to mysql and create username and password to database
	private static void createUser(String user, String password) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection(  
					"jdbc:mysql://localhost:3306/phase3db","root","");  
			System.out.println("connected to mysql...");
			Statement stmt=con.createStatement();  
			System.out.println("trying to insert to mysql...");
			stmt.executeUpdate("INSERT INTO userlogin (username, password)" + "VALUES('"+user+"','"+password+"')"); 
			oFile=new File(path+"\\"+user+".txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("connect failed");
		}
		
	}
	
	//load the file storing the username and password to userLoginDB
	private static void loaduserLoginFile() throws FileNotFoundException {
	userLoginFile=new File("user_login.txt");
	@SuppressWarnings("resource")
	Scanner inFile=new Scanner(userLoginFile);
		while(inFile.hasNext()){
			String user=inFile.next();
			String pass=inFile.next();
			userLoginDB.put(user, pass);
		}
	}
	//this is command line setup
	private static void InitialSetUp(String flagArg) throws IOException {
		String[] splitArg = flagArg.trim().split("\\s+");
		//if -i command find the file
		if(splitArg[0].equalsIgnoreCase("-i")){
			String inFilename=splitArg[1];
			try{
				iFile=new File(path+"\\"+inFilename);
				if(iFile.exists() && iFile.isFile()){
					System.out.println("found input file");
				}
			}
			catch (Exception e) {
				System.out.println("invalid file name");
			}
		}
		//if -o create/find the file
		else if(splitArg[0].equalsIgnoreCase("-o")){
			String outFilename=splitArg[1];
			try{
				oFile=new File(path+"\\"+outFilename);
				userwriter=new FileWriter(oFile, false);
				if(oFile.exists() && oFile.isFile()){
					System.out.println("found output file");
				}
			}
			catch (Exception e) {
				System.out.println("invalid file name");
			}
		}
		//if -p command, use the given input and output file to start crawling
		else if(splitArg[0].equalsIgnoreCase("-p")){
			
			if(((oFile.exists() && oFile.isFile())&&(iFile.exists() && iFile.isFile()))){
				@SuppressWarnings("resource")
				Scanner read=new Scanner(iFile);
				Crawler startCrawl=new Crawler(username, userwriter, logwriter);
				while(read.hasNextLine()){
					System.out.println("crawling...");
					//crawling part
					startCrawl.crawl(read.nextLine());
				}
				startCrawl.writeToUserFile();
				Desktop.getDesktop().open(oFile);
				System.exit(0);
			}
			System.out.println("must provide both -o and -i arguements");
		}
		
	}

	public static void main(String[] args) throws IOException {

	@SuppressWarnings("resource")
	Scanner input = new Scanner (System.in);
	String user,password="";
	loaduserLoginFile();
	//get current path
	path=Paths.get(".").toAbsolutePath().normalize().toString();
	//create log file
	lFile=new File(path+"\\"+"Adminlog.txt");
	logwriter=new FileWriter(lFile, true);
	
	while(username==null){
		//ask user for login type
		System.out.println("are you a: "
				+ "1) ADMIN "
				+ "2) USER "
				+ "3) NEW USER "
				+ "4) GUEST\n"
				+ "Please enter number between 1-4\n");
		int num=input.nextInt();
		switch (num) { 
		//if ADMIN ask password for admin
	    case 1: 
	    	
	    	System.out.println("Do you want to login using which login system 1)mySQL 2)Hash database"+"\n");
	    	choice=input.nextInt();
	    		if(choice==1){
	    			System.out.println("Please provide password: ");
	    	        password=input.next();
	    	        //check using MySQL database
	    	        if(checkUsingMySQL("admin",password)){
	    	        	username="admin";
	    	        	System.out.println("login success");
	    	        }
	    		}else if(choice==2){
	    			System.out.println("Please provide password: ");
	    	        password=input.next();
	    			if(check("admin", password)){
	    	        	username="admin";
	    	        }
	    		}else{
	    			System.out.println("please enter 1 or 2");
	    		}
	    	
	        break; 
	    //if returning USER ask for username and password
	    case 2: 
	    	System.out.println("Do you want to login using which login system 1)mySQL 2)Hash database"+"\n");
	    		choice=input.nextInt();
	    		if(choice==1){
	    			System.out.println("Please provide username: ");
	    			user=input.next();
	    			System.out.println("Please provide password: ");
	    			password=input.next();
	    			if(checkUsingMySQL(user,password)){
	    		        username=user;
	    		        System.out.println("login success");
	    		    }
	    			
	    		}else if(choice==2){
	    			System.out.println("Please provide username: ");
	    			user=input.next();
	    			System.out.println("Please provide password: ");
	    			password=input.next();
	    			if(check(user,password)){
	    	    		username=user;
	    	    	}
	    		}
	        break; 
	    //if NEW USER ask user to create username and password
	    case 3: 
	    	System.out.println("Please create username: ");
	    	user=input.next(); 
	    	System.out.println("Please create password: ");
	    	password=input.next();
	    	while(checkUsingMySQL(user,password)){
	    		System.out.println("username exists try another");
	    		user=input.next();
	    		System.out.println("Please provide password: ");
	    		password=input.next();
	    	}
	    	//put user into mysql login system
	    	createUser(user,password);
	    	username=user;
	    	//as well as local file login system
	    	userLoginDB.put(user, password);
	    	FileWriter writer=new FileWriter(userLoginFile, true);
	    	writer.write(newline+user+" "+password); 
	    	writer.close();
	        break; 
	    //if GUEST do nothing
	    case 4: 
	    	username = "guest";
	    	break; 
		}
	}

	//start of command line
	while(!flagArg.equalsIgnoreCase("-done")){
		System.out.println("please provide input and output file using\n"
						 + "-i INPUTFILENAME\n"
						 + "-o OUTPUTFILENAME\n"
						 + "-p\n"
						 + "-exit\n"
						 + "-done\n");
		flagArg=input.nextLine();
		if(flagArg.equals("-exit")){
			return;
		}
		InitialSetUp(flagArg);
	}
	
	//create/locate the user database file but not for guest
	if(!username.equals("guest")){
		oFile=new File(path+"\\"+username+".txt");
		userwriter=new FileWriter(oFile,true);
		//load the user file for old data to be entered into hashmap
		Scanner loadFile=new Scanner(oFile);
		Crawler=new Crawler(username, userwriter, logwriter);
		try{
			Crawler.load(loadFile);
			loadFile.close();
		}catch (NoSuchElementException e){
			System.out.println("No data in database");
		}
		
	}
	
	try {
		//start a crawler if not started before and open up main gui
		Crawler=new Crawler(username, userwriter, logwriter);
		Gui window = new Gui(username, Crawler);
		window.open();
	} catch (Exception e) {
		e.printStackTrace();
	}

	}
	
	
	
	

	
}
