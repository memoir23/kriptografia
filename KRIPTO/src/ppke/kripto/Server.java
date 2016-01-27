package ppke.kripto;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Server implements Runnable {
	public static final int PORT_NUMBER = 42424;
	private int modulus;
	private long Cresult;
	private BigInteger key;
	private DH dh;
	protected ServerSocket serverSocket;
	static State state;

	static public enum State {
		dh, close, mod, req
	}

	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT_NUMBER);
	}

	public void close() throws IOException {
		serverSocket.close();
	}

	public void run() {
		state = State.dh;
		try {
			System.out.println("Wait for client");
			Socket clientSocket = serverSocket.accept();
			System.out.println("Accept Client");
			DataInputStream serverInput = new DataInputStream(
					clientSocket.getInputStream());
			DataOutputStream serverOutput = new DataOutputStream(
					clientSocket.getOutputStream());

			config(serverInput, serverOutput);
			getRequest(serverInput, serverOutput);

			System.out.println("Operation is closing");

			while (state != State.close)

				clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getRequest(DataInputStream serverInput,
			DataOutputStream serverOutput) throws IOException {
		
		String username = "";
		String verifier;
		
		int size = serverInput.readInt();
		byte[] a = new byte[size];
		byte[] b = new byte[a.length - 16];
		byte[] crypted = new byte[b.length];
		byte[] iv = new byte[16];

		System.arraycopy(a, 0, iv, 0, 16);
		System.arraycopy(a, iv.length, crypted, 0, crypted.length);

		serverInput.read(a);

		b = Crypting.readCiphered(crypted, iv, key.toByteArray());

		try {

			Document d = obtenerDocumentDeByte(b);
			User user_temp = new User(d.toString());

			if (!addUser(user_temp)) {
				if (!authentication(user_temp))
					System.err.println("not correct authentication");
			}

			// d.getDocumentElement().normalize();
			// NodeList nList = d.getElementsByTagName("users");
			// Node nNode = nList.item(0);
			// NodeList ghChildren = nNode.getChildNodes();
			//
			// for (int temp = 0; temp < ghChildren.getLength(); temp++) {
			// Node node = ghChildren.item(temp);
			// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			// node.getChildNodes();
			// Element eElement = (Element) node;
			// if (eElement.getNodeName().equals("name")) {
			// username = eElement.getTextContent();
			// } else if (eElement.getNodeName().equals("verifier")) {
			// verifier = eElement.getTextContent();
			// }
			// }
			// }
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			state = State.close;
		}

		byte[] fileData = null;
		try {
			File file = new File(actualUser.getName() + ".xml");
			fileData = new byte[(int) file.length()];
			FileInputStream in = new FileInputStream(file);
			in.read(fileData);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			state = State.close;
		}

		byte[] encryptedData = Crypting.writeCiphered(fileData,
				key.toByteArray());

		serverOutput.writeInt(encryptedData.length);
		serverOutput.write(encryptedData);
		serverOutput.flush();
	}

	private void config(DataInputStream serverInput,
			DataOutputStream serverOutput) throws IOException {
		int step = -1;
		
		while (step < 3) {
			byte[] b = new byte[serverInput.readInt()];
			serverInput.read(b);
			try {

				Document d = obtenerDocumentDeByte(b);
				d.getDocumentElement().normalize();
				NodeList nList = d.getElementsByTagName("dh");
				Node nNode = nList.item(0);
				NodeList ghChildren = nNode.getChildNodes();

				for (int temp = 0; temp < ghChildren.getLength(); temp++) {
					Node node = ghChildren.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						node.getChildNodes();
						Element eElement = (Element) node;
						if (eElement.getNodeName().equals("step")) {
							step = Integer.parseInt(eElement.getTextContent());
						} else if (eElement.getNodeName().equals("modulus")) {
							modulus = Integer.parseInt(eElement
									.getTextContent());
						} else if (eElement.getNodeName().equals("myresult")) {
							Cresult = Integer.parseInt(eElement
									.getTextContent());
						}
					}
				}
				if (step == 1)
					try {
						dh = new DH(2, modulus);
						byte[] secondStep = ConfigKey(Long.toHexString(dh
								.getMyresult()));
						serverOutput.writeInt(secondStep.length);
						serverOutput.write(secondStep);
						serverOutput.flush();

					} catch (Exception e) {
						e.printStackTrace();
					}
				else if (step == 3) {
					try {
						key = dh.createKey(Cresult);
						// System.out.println(key);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
			}
		}
	}

	private byte[] ConfigKey(String hexString) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("dh");
			doc.appendChild(rootElement);
			Element record = doc.createElement("step");
			record.setTextContent("2");
			rootElement.appendChild(record);
			Element modulus = doc.createElement("myresult");
			modulus.setTextContent(hexString);
			rootElement.appendChild(modulus);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("outexample2.xml"));
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		byte[] fileData = null;
		try {
			File file = new File("outexample2.xml");
			fileData = new byte[(int) file.length()];
			FileInputStream in = new FileInputStream(file);
			in.read(fileData);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileData;
	}

	public static void main(String[] args) {
		try {
			new Server().run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Document obtenerDocumentDeByte(byte[] doc2Xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(doc2Xml));
	}

	private HashSet<User> users = new HashSet<User>();

	private User actualUser;

	public void loadAllUser() {
		try {
			File xmlFile = new File("allUser.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("user");
			for (int temp = 0; temp < nodeList.getLength(); temp++) {
				Node node = nodeList.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;

					String name = element.getAttribute("name");
					String verifier = element.getAttribute("verifier");
					users.add(new User(name, verifier));
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean authentication(User user_) {
		for (User users : users) {
			if (user_.getName().equals(users.getName())) {
				if (user_.getVerifier().equals(users.getVerifier())) {
					try {
						File xmlFile = new File(user_.getName() + ".xml");
						DocumentBuilderFactory dbFactory = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder dBuilder;
						dBuilder = dbFactory.newDocumentBuilder();
						Document doc = dBuilder.parse(xmlFile);
						Element user = doc.getDocumentElement();
						String name = user.getAttribute("name");
						NodeList nodeList = doc.getElementsByTagName("record");
						for (int i = 0; i < nodeList.getLength(); i++) {
							Node node = nodeList.item(i);
							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element element = (Element) node;
								String url = element.getAttribute("url");
								String username = element
										.getAttribute("username");
								String password = element
										.getAttribute("password");
								String recordsalt = element
										.getAttribute("recordsalt");

								Record record = new Record(url, username,
										password, recordsalt);
								actualUser.addRecord(record);
							}
						}

					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}

				return false;
			}
		}
		return false;
	}

	private void addRecord(Record record) {
		actualUser.addRecord(record);
	}

	public void saveUserRecords(User user) throws IOException {
		File outputFile = new File(user.getName() + ".xml");
		if (!outputFile.exists())
			outputFile.createNewFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(
				outputFile.getAbsoluteFile()));
		writer.write("<users>");
		writer.write(user.toXML());
		writer.write("</users>");
		writer.close();
	}

	public boolean addUser(User user) throws IOException {
		for (User users : users) {
			if (user.getName().equals(users.getName())) {
				if (!user.getVerifier().equals(users.getVerifier()))
					return false;
				return false;
			}
		}
		users.add(user);
		File outputFile = new File("allUser.xml");
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				outputFile.getAbsoluteFile()));
		writer.write("<users>");
		for (User users : users) {
			writer.write(users.toXMLOnlyUser());
		}
		writer.write("</users>");
		writer.close();

		return true;
	}

}
