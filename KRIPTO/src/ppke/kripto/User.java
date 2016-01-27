package ppke.kripto;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class User {
	
		
		public static final String ROOT_TAG = "user";
		public static final String NAME_TAG = "name";
		public static final String VERIFIER_TAG = "verifier";
		public static final String RECORD_TAG = "record";
		
		private String name;
		private String verifier;
		private HashMap<String, Record> records;
		
		{
			verifier = "";
			records = new HashMap<String, Record>();
		}
		
		public User(String name, String verifier, ArrayList<Record> records) {
			this.name = name;
			this.verifier = verifier;
			for (Record record : records) {
				this.records.put(record.getUrl(), record);
			}
		}
		
		public User(String name, String verifier) {
			this(name, verifier, new ArrayList<Record>());
		}
		
		public User(String xml) throws ParserConfigurationException, SAXException, IOException {
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
					name = element.getAttribute(NAME_TAG);
					verifier = element.getAttribute(VERIFIER_TAG);
				}
			}
			NodeList recordList = doc.getElementsByTagName(RECORD_TAG);
			for (int i = 0; i < recordList.getLength(); ++i) {
				Node node = recordList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					Record record = new Record(element.getAttribute(Record.URL_ATTR),
							element.getAttribute(Record.USERNAME_ATTR),
							element.getAttribute(Record.PASSWORD_ATTR),
							element.getAttribute(Record.RECORDSALT_ATTR));
					records.put(record.getUrl(), record);
				}
			}
		}
		
		public void addRecord(Record record) {
			records.put(record.getUrl(), record);
		}
		
		public Record getRecord(String url) {
			return records.get(url);
		}
		
		public boolean hasRecords() {
			return !records.isEmpty();
		}
		
	
		public String toXML() {
			String xmlBaseFormat = "<%1$s %2$s=\"%3$s\" %4$s=\"%5$s\">%%s</%1$s>";
			String xmlFormat = String.format(xmlBaseFormat, ROOT_TAG, NAME_TAG, name, VERIFIER_TAG, verifier);
			StringBuilder recordXMLs = new StringBuilder();
			for (Record record : records.values()) {
				recordXMLs.append(record.toXML());
			}
			return String.format(xmlFormat, recordXMLs.toString());
		}
		
		public String toXMLOnlyUser() {
			String xmlBaseFormat = "<%1$s %2$s=\"%3$s\" %4$s=\"%5$s\">%%s</%1$s>";
			String xmlFormat = String.format(xmlBaseFormat, ROOT_TAG, NAME_TAG, name, VERIFIER_TAG, verifier);
			
			return xmlFormat;
		}
		
		public String getName() {
			return name;
		}
		
		public String getVerifier() {
			return verifier;
		}
		
		public HashMap<String, Record> getRecords() {
			return records;
		}

	
	

	

}