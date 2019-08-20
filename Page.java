package Phase3withGui;

public class Page {
	//this is the data stored in hashmap relating to ID
	String Title;
	String Author;
	String imgPath;
	String username;
	String timestamp;
	public Page(String bookTitle, String bookAuther, String imgpath2, String username2, String time) {
		this.Title=bookTitle;
		this.Author=bookAuther;
		this.imgPath=imgpath2;
		this.username=username2;
		this.timestamp=time;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getAuthor() {
		return Author;
	}
	public void setAuthor(String author) {
		Author = author;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	

}
