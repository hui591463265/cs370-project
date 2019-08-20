package Phase3withGui;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
//https://myaccount.google.com/lesssecureapps?pli=1
//Allow less secure apps :ON
public class sendEmail {
	public void run(String userEmail, String userPassword){
		//user send to himself
		 String to = userEmail;
		 String from=userEmail;
		 //host is gmail.com
		 String host="smtp.gmail.com";
		 
		 Properties props = System.getProperties();
		 props.put("mail.smtp.auth", "true");
		 props.put("mail.smtp.starttls.enable", "true");
		 props.setProperty("mail.smtp.host", host);
	     props.put("mail.smtp.port", "587");
	     
	     // Get the Session object.
		 // Session session = Session.getDefaultInstance(props);
	     Session session = Session.getInstance(props,
	         new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(userEmail, userPassword);
	            }
	         });	 
	             
	      try {
	          // Create a default MimeMessage object.
	          Message message = new MimeMessage(session);

	          // Set From: header field of the header.
	          message.setFrom(new InternetAddress(from));

	          // Set To: header field of the header.
	          message.addRecipient(Message.RecipientType.TO,
	             new InternetAddress(to));

	          // Set Subject: header field
	          message.setSubject("Database file");

	          // Create the message part
	          BodyPart messageBodyPart = new MimeBodyPart();

	          // Now set the actual message
	          messageBodyPart.setText("Here is the database ");

	          // Create a multipar message
	          Multipart multipart = new MimeMultipart();

	          // Set text message part
	          multipart.addBodyPart(messageBodyPart);

	          // Part two is attachment
	          messageBodyPart = new MimeBodyPart();
	          String filename = ""+Main.oFile.getAbsolutePath();
	          System.out.println(filename);
	          DataSource source = new FileDataSource(filename);
	          messageBodyPart.setDataHandler(new DataHandler(source));
	          messageBodyPart.setFileName(filename);
	          multipart.addBodyPart(messageBodyPart);

	          // Send the complete message parts
	          message.setContent(multipart);

	          // Send message
	          Transport.send(message);

	          System.out.println("Sent message successfully....");
	   
	       } catch (MessagingException e) {
	          throw new RuntimeException(e);
	       }
	    }
}
