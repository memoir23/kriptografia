package ppke.kripto;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
 
public class WriteXMLFile {
 
	public static void main(String argv[]) {
 
	  try {
userwrite();
recordsave();
	
	} catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

public static void userwrite () throws TransformerException, ParserConfigurationException{

		 
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// users elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("users");
		doc.appendChild(rootElement);
 
		// user elements
		Element user = doc.createElement("user");
		rootElement.appendChild(user);
		Element record = doc.createElement("record");
		user.appendChild(record);
		// set attribute to user element
		Attr name = doc.createAttribute("name");
		name.setValue("example");
		user.setAttributeNode(name);
		
		
		Attr url = doc.createAttribute("url");
		url.setValue("valami.hu");
		record.setAttributeNode(url);
		
		Attr username = doc.createAttribute("username");
		username.setValue("base64encodedadsfasdfa");
		record.setAttributeNode(username);
 
		Attr password = doc.createAttribute("password");
		password.setValue("baseasdasd");
		record.setAttributeNode(password);
		
		Attr salt = doc.createAttribute("recordsalt");
		salt.setValue("saltsalt");
		record.setAttributeNode(salt);
		// shorten way
		// staff.setAttribute("id", "1");
  
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("D:\\filerecord.xml"));
		
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");
 
	  
	}
	
	
	public static void recordsave() throws Exception
	{

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// users elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("users");
		doc.appendChild(rootElement);
 
		// user elements
		Element user = doc.createElement("user");
		rootElement.appendChild(user);
 
		// set attribute to user element
		Attr name = doc.createAttribute("name");
		name.setValue("example");
		user.setAttributeNode(name);
		
 
		// shorten way
		// staff.setAttribute("id", "1");
  
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("D:\\file.xml"));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");

	}
}