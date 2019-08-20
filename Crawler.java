package Phase3withGui;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import Phase3withGui.Page;
import Phase3withGui.Gui;

public class Crawler {
	public static String newline = System.getProperty("line.separator");
	static HashMap<String, Page> crawledpairs = new HashMap<String, Page>();
	String username;
	boolean guirunning=false;
	FileWriter uw;
	FileWriter log;
	Gui gui;
	//get username for storage of data
	public Crawler(String username, FileWriter userwriter, FileWriter logwriter) {
		this.username=username;
		this.uw=userwriter;
		this.log=logwriter;
	}

	public void crawl(String startpage) throws IOException {
	//create URL using string
	URL url=new URL(startpage);
	//get contents of url
	String content=fetchContent(url);
	/*
	Pattern p = Pattern.compile("<title>\\s*(.*?)<(/)title>", Pattern.DOTALL);
	Matcher m = p.matcher(content);
	m.find();
	if(!crawledpairs.containsValue(m.group(1))){
		String logo="https://d3ogvdx946i4sr.cloudfront.net/assets/v2.12.53/img/logo.svg";
		Timestamp time=new Timestamp(System.currentTimeMillis());
		Page newpage=new Page(url.toString(),logo,username,time);
		crawledpairs.put(m.group(1), newpage);
		if(guirunning){
		gui.logtext.append("INSERT\nTitle:"+m.group(1)+"\nURL: "+url+"\nImagePath: "+logo+"\nTimeStamp: "+time+" from database\n");
		}
	}
	*/
	
	// only get the books links that are listed after search
	Pattern pattern = Pattern.compile("<h3 class=\"title\">\\s*<a href=\"(/.*?)\"", Pattern.DOTALL);
	Matcher matcher = pattern.matcher(content);
	while (matcher.find()) {
        try {
            URL link = new URL("https://www.bookdepository.com"+matcher.group(1));
            //System.out.println(link);
            //if crawled link is not in DB, get page information and store to database
            if(!crawledpairs.containsValue(link)){
            	pageGetInfo(link);
        	}
        }
        catch (MalformedURLException e) { 
        }
    }
	Main.logwriter.flush();
	}
	//storing page content title pair to database
		private void pageGetInfo(URL link) throws IOException {
			String pagecontent=fetchContent(link);
			//get title
			Pattern title = Pattern.compile("<title>\\s*(.*?)<(/)title>", Pattern.DOTALL);
			Matcher m = title.matcher(pagecontent);
			m.find();
			//title contains title, id and author
			int third=m.group(1).lastIndexOf(":");
			int second=m.group(1).lastIndexOf(":",third-1);
			try{
			String bookTitle=m.group(1).substring(0, second-1);
			String bookAuther=m.group(1).substring(second+2, third);
			String bookID=m.group(1).substring(third+2);
			//get img url
			Pattern img = Pattern.compile("<div class=\"item-img-content\">\\s*<img src=\"(.*?)\"", Pattern.DOTALL);
			Matcher m1 = img.matcher(pagecontent);
			m1.find();
			//if ID not in Database(ID here will always be unique)
			if(!crawledpairs.containsKey(bookID)){
				//storing is same as crawl method above
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				URL imgURL=new URL(m1.group(1));
				//this method downloads the img and create img path
				String imgpath=downloadImgPath(imgURL);
				Page newpage=new Page(bookTitle,bookAuther,imgpath,username,timeStamp);
				crawledpairs.put(bookID, newpage);
				if(guirunning){
					Gui.logtext.append("INSERT\nID:"+bookID
										+"\nTitle: "+bookTitle
										+"\nAuther: "+bookAuther
										+"\nImagePath: "+imgpath
										+"\nTimeStamp: "+timeStamp
										+" from database\n");
					if(!username.equals("guest")){
					Main.logwriter.write(username+newline+"INSERT"+newline+bookID+newline+bookTitle+newline+bookAuther+newline+imgpath+newline+timeStamp+newline);
					}
				}
			}
			}catch(StringIndexOutOfBoundsException e){
				
			}
			
		}
	//will download the img path with ending as the image name from website
	private String downloadImgPath(URL url) throws IOException {
		String path=Paths.get(".").toAbsolutePath().normalize().toString();
		String ending=url.getPath().substring(url.getPath().lastIndexOf('/')+1);
		String[] splitend = ending.trim().split("\\.");
		if(splitend[1].matches("jpeg|jpg|gif")){
			File newFile=new File(ending);
			BufferedImage image=ImageIO.read(url);
			ImageIO.write(image, splitend[1], newFile);
			path=path+"\\"+username+ending;
		}
		
		return path;
	}
		

	//will iterate through the hashmap and output to userfile
		public void writeToUserFile() throws IOException {
			try{
			FileWriter writer=new FileWriter(Main.oFile);
			Set<?> set = crawledpairs.entrySet();
			Iterator<?> iterator = set.iterator();
		    while(iterator.hasNext()) {
		    @SuppressWarnings("rawtypes")
			Map.Entry x = (Map.Entry)iterator.next();
		       Page i=crawledpairs.get(x.getKey());
		       writer.write(x.getKey()+newline+i.Title+newline+i.Author+newline+i.imgPath+newline+i.timestamp+newline);
		    }
		    writer.close();
			}catch (NullPointerException e){
				System.out.println("database can't be null");
			}
			
			
		}
	//fetching the content of url using method we learned in class
	private static String fetchContent(URL url) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            in.close();
        }
        catch (IOException e) {
            System.err.println("An error occured while atempt to fetch content from " + url);
        }
        return stringBuilder.toString();
    }
	//passed from GUI to delete the data related to the title from database
	public void remove(String id) throws IOException {
		if(crawledpairs.containsKey(id)){
			crawledpairs.remove(id);
			Main.logwriter.write(username+newline+"REMOVE"+newline+id+newline);
			Gui.logtext.append("REMOVED\nID:"+id+" from database\n");
		}else{
			Gui.logtext.append("Title not in database\n");
		}
		Main.logwriter.flush();
	}
	//this will change all information base on user inputs
	public void modify(String id, String title, String author, String ImgPath) throws IOException {
		if(crawledpairs.containsKey(id)){
			Page p=crawledpairs.get(id);
			//if user forgot to enter title/author/imgPath, use old data instead
			//because if user input "", reading the data will create error
			if(title=="") {
				title=p.getTitle();
			}
			if(author=="") {
				author=p.getAuthor();
			}
			if(ImgPath=="") {
				ImgPath=p.getImgPath();
			}
			Timestamp time=new Timestamp(System.currentTimeMillis());
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			//set all data and store to database
			p.setImgPath(ImgPath);
			p.setAuthor(author);
			p.setTitle(title);
			p.setTimestamp(timeStamp);
			Main.logwriter.write(username+newline+"MODIFY"+newline+id+newline+title+newline+author+newline+ImgPath+newline+time+newline);
			Gui.logtext.append("MODIFY\nID:"+id
								+"\nTitle: "+title
								+"\nAuthor: "+author
								+"\nImagePath: "+ImgPath
								+"\nTimeStamp: "+time
								+" from database\n");
			
		}else{
			Gui.logtext.append("id not in database");
		}
		Main.logwriter.flush();
	}
	//this method will get all data from user file and store into hashmap
	//used for inital hashmap setup
	public void load(Scanner loadFile) {
	String ID;
	String Title;
	String Author;
	String imgPath;
	String timestamp;
	while(loadFile.hasNextLine()){
		ID=loadFile.nextLine();
		Title=loadFile.nextLine();
		Author=loadFile.nextLine();
		imgPath=loadFile.nextLine();
		timestamp=loadFile.nextLine();
		Page np=new Page(Title, Author, imgPath, username, timestamp);
		crawledpairs.put(ID, np);
	}
	}
	//this method will take user input startdate and enddate
	public void getTime(Date startdate, Date enddate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try{
			Gui.logtext.append("searching"+newline);
			//looping through all the database and get all information that matches 
			Set<?> set = crawledpairs.entrySet();
			Iterator<?> iterator = set.iterator();
		    while(iterator.hasNext()) {
		    @SuppressWarnings("rawtypes")
			Map.Entry x = (Map.Entry)iterator.next();
		       Page i=crawledpairs.get(x.getKey());
		       Date datatime = sdf.parse(i.timestamp);
		       //this method get all dates after startdate and before enddate inclusive
		       if(datatime.compareTo(startdate)>=0&&datatime.compareTo(enddate)<=0){
		       Gui.logtext.append("MATCHED"+newline+x.getKey()+newline+i.Title+newline+i.Author+newline+i.imgPath+newline+i.timestamp+newline);
		       }
		    }
		}catch (ParseException e){
			Gui.logtext.append("wrong format");
		}
	}
	//this method takes log file and reconstruct the user file given by user name
	public void reconstruct(String userdb, FileWriter reconwriter) throws IOException {
		HashMap<String, Page> crawledpairs = new HashMap<String, Page>();
		String username;
		String method;
		String ID;
		String Title;
		String Author;
		String imgPath;
		String timestamp;
		@SuppressWarnings("resource")
		Scanner loadFile=new Scanner(Main.lFile);
		//while log file has more lines
		
		while(loadFile.hasNextLine()){
			System.out.println(loadFile.nextLine());
			//check if user from log matches the username
			username=loadFile.nextLine();
			if(username.equals(userdb)){
				method=loadFile.nextLine();
				System.out.println(method);
				//if data entry is insert
				//insert all data to hashmap
				if(method.equals("INSERT")){
					ID=loadFile.nextLine();
					Title=loadFile.nextLine();
					Author=loadFile.nextLine();
					imgPath=loadFile.nextLine();
					timestamp=loadFile.nextLine();
					Page np=new Page(Title, Author, imgPath, userdb, timestamp);
					crawledpairs.put(ID, np);
				}
				//if data entry is remove
				//remove all data to hashmap according to id
				if(method.equals("REMOVE")){
					//see if database contains id
					ID=loadFile.nextLine();
					if(crawledpairs.containsKey(ID)){
						crawledpairs.remove(ID);
					}
				}
				// if data entry is Modify
				//update all data to hashmap according to log file
				if(method.equals("MODIFY")){
					ID=loadFile.nextLine();
					Title=loadFile.nextLine();
					Author=loadFile.nextLine();
					imgPath=loadFile.nextLine();
					timestamp=loadFile.nextLine();
					if(crawledpairs.containsKey(ID)){
						Page p=crawledpairs.get(ID);
						p.setImgPath(imgPath);
						p.setAuthor(Author);
						p.setTitle(Title);
						p.setTimestamp(timestamp);
					}
				}
			}	
			
		}
	
		//rebuild the user database
		rebuild(crawledpairs, reconwriter);
		
	}
//end }
	//this method loop through the hashmap of a user and write to his file 
	private void rebuild(HashMap<String, Page> crawledpairs2, FileWriter reconwriter) {
		try{
			Set<?> set = crawledpairs2.entrySet();
			Iterator<?> iterator = set.iterator();
		    while(iterator.hasNext()) {
		    @SuppressWarnings("rawtypes")
			Map.Entry x = (Map.Entry)iterator.next();
		       Page i=crawledpairs2.get(x.getKey());
		       reconwriter.write(x.getKey()+newline+i.Title+newline+i.Author+newline+i.imgPath+newline+i.timestamp+newline);
		    }
		    reconwriter.close();
		}
		catch (NullPointerException | IOException e){
			System.out.println("database can't be null");
		}
		
		
	}

	
}
