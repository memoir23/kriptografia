package ppke.kripto;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Record {
	
	public static final String ROOT_TAG = "record";
	public static final String URL_ATTR = "url";
	public static final String USERNAME_ATTR = "username";
	public static final String PASSWORD_ATTR = "passwd";
	public static final String RECORDSALT_ATTR = "recordsalt";
	private static final String XML_FORMAT = "<record url=\"%s\" username=\"%s\" passwd=\"%s\" recordsalt=\"%s\"/>";
	
	private String url;
	private String username;
	private String password;
	private String recordSalt;
	
	public Record(String url, String userName, String password,
			String recordSalt) {
		this.url = url;
		this.username = userName;
		this.password = password;
		this.recordSalt = recordSalt;
	}
	
	public Record(String xml) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
		doc.getDocumentElement().normalize();
	 
		NodeList nodeList = doc.getElementsByTagName(ROOT_TAG);
		for (int temp = 0; temp < nodeList.getLength(); ++temp) {
			Node node = nodeList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				url = element.getAttribute(URL_ATTR);
				username = element.getAttribute(USERNAME_ATTR);
				password = element.getAttribute(PASSWORD_ATTR);
				recordSalt = element.getAttribute(RECORDSALT_ATTR);
			}
		}
	}
	
	public String toXML() {
		return String.format(XML_FORMAT, url, username, password, recordSalt);
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUserName(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRecordSalt() {
		return recordSalt;
	}
	public void setRecordsalt(String recordSalt) {
		this.recordSalt = recordSalt;
	}


}
