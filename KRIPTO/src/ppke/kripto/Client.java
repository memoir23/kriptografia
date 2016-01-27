package ppke.kripto;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException; // Ha a kommunikacioban valami balul sul el
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException; // Ha rossz cimre probalunk csatlakozni
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Client implements Runnable {
	private final static String PASSWORD_SALT = "password"; 
	private final static String USERNAME_SALT = "userid";  
	protected Socket clientSocket;
	private Integer myModulus;
	private long Sresult;
	private BigInteger key;
	private DH dh;
	private static int PORT_NUMBER=42424;
	
	
	//EZEKET ÁLLÍTJA A GUI
	public static String username, masterkey, urlUsername, urlAddress, urlPassword; 
	public static State state;
	
	
	public static byte[] recordsalt;
	private static String encodeBuffer;
	
	static public enum State{
		dh, close, mod, req
	}
	 
	public Client() throws UnknownHostException, IOException {
		clientSocket = new Socket(InetAddress.getLocalHost(),
			PORT_NUMBER);
	}

	public void close() throws IOException {
		clientSocket.close();
	}

	public void run() {
		state=State.dh;
		try {
			DataInputStream serverInput = new DataInputStream(
					clientSocket.getInputStream());
			DataOutputStream serverOutput = new DataOutputStream(
					clientSocket.getOutputStream());
			
			config(serverInput,serverOutput );
			try {	
				while(!state.equals("req"))
				{
					
					if(state.compareTo(State.req)==0)
						break;
					if(state==State.req)
						break;
//					if(state.equals("req"))
//							break;
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println(state);
				}
				System.out.println("sending request");
				sendRequest(serverInput, serverOutput);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void config(DataInputStream serverInput, DataOutputStream serverOutput) throws IOException{
		byte[] modXML = getMod();
		try {
			dh = new DH(2, myModulus);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		serverOutput.writeInt(modXML.length);
		serverOutput.write(modXML);
		serverOutput.flush();
		int hossz =serverInput.readInt();
		byte[] b = new byte[hossz];
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
						if (2 != Integer
								.parseInt(eElement.getTextContent()))
							return;
					} else if (eElement.getNodeName().equals("myresult")) {
						Sresult = Long
								.parseLong(eElement.getTextContent());
						System.out.println("result ="+Sresult);
						try {
							key= dh.createKey(Sresult);
							System.out.println(key);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			try {
				byte[] resultXML = getResult(Long.toHexString(dh
						.getMyresult()));
				serverOutput.writeInt(resultXML.length);
				serverOutput.write(resultXML);
				serverOutput.flush();
				
			
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private byte[] getMod() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("dh");
			doc.appendChild(rootElement);
			Element record = doc.createElement("step");
			record.setTextContent("1");
			rootElement.appendChild(record);
			Element modulus = doc.createElement("modulus");
			Random rand = new Random();
			
			 
			myModulus = DH.MOD_NUMS[rand
									.nextInt(DH.MOD_NUMS.length)]; 
			modulus.setTextContent(IETF.get(myModulus).toString());
			rootElement.appendChild(modulus);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("outexample.xml"));
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
			File file = new File("outexample.xml");
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

	
	private byte[] getResult(String hexString) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("dh");
			doc.appendChild(rootElement);
			Element record = doc.createElement("step");
			record.setTextContent("3");
			rootElement.appendChild(record);
			Element modulus = doc.createElement("myresult");
			modulus.setTextContent(hexString);
			rootElement.appendChild(modulus);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("outexample.xml"));
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
			File file = new File("outexample.xml");
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
	
	
	// új rekordot küld a userhez a szervernek
	private void newUser(DataInputStream clientInput, DataOutputStream clientOutput) throws ParserConfigurationException, TransformerException{		
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
		name.setValue(username);
		user.setAttributeNode(name);

		Attr url = doc.createAttribute("url");
		url.setValue(urlAddress);
		record.setAttributeNode(url);
		
		Attr username = doc.createAttribute("username");
		username.setValue(urlUsername);
		record.setAttributeNode(username);

		Attr password = doc.createAttribute("password");
		password.setValue(urlPassword);
		record.setAttributeNode(password);
		

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		StreamResult result = new StreamResult(new File("outexample.xml"));
		transformer.transform(source, result);
		
		byte[] fileData = null;
		try {
			File file = new File("outexample.xml");
			fileData = new byte[(int) file.length()];
			FileInputStream in = new FileInputStream(file);
			in.read(fileData);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			fileData = Crypting.writeCiphered(fileData, key.toByteArray());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			clientOutput.writeInt(fileData.length);
			clientOutput.write(fileData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	    public String decryptRecord(String data, String salt, int iterCount) throws IOException {
//	    	String Result="";
//	    	byte[] recordsaltByte =Base64.getDecoder().decode(recordsalt);
//			
//			try {
//				SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
//
//				PBEKeySpec recordspec = new PBEKeySpec(masterkey.toCharArray(), recordsaltByte, iterCount, 128);
//				SecretKey recordKey = secretKeyFactory.generateSecret(recordspec);
//
//				PBEKeySpec dataspec = new PBEKeySpec(data.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), iterCount, 128);
//				SecretKeySpec dataKey = new SecretKeySpec(secretKeyFactory.generateSecret(dataspec).getEncoded(), "AES");
//
//				cipher.init(Cipher.DECRYPT_MODE,  new SecretKeySpec(recordKey.getEncoded(), "AES"), new IvParameterSpec(recordsalt));
//				 Result = new String(cipher.doFinal(Base64.getDecoder().decode(data)), StandardCharsets.UTF_8);
//
//			} catch(Exception e) {
//				throw new IOException(String.format("Could not decrypt record: %s", e.getMessage()), e);
//			}
//
//			return Result;
//		}
//		
	
	private byte[] getAnswerDecoded(DataInputStream clientInput,
			DataOutputStream clientOutput) throws IOException {
		byte[] a = new byte[clientInput.readInt()];
		byte[] b = new byte[clientInput.readInt() - 16];
		byte[] crypted = new byte[clientInput.readInt() - 16];
		byte[] iv = new byte[16];

		System.arraycopy(a, 0, iv, 0, 16);
		System.arraycopy(a, iv.length, crypted, 0, crypted.length);

		clientInput.read(a);

		b = Crypting.readCiphered(crypted, iv, key.toByteArray());
		return b;
	}
	
		private static byte[] makeSalt() throws NoSuchAlgorithmException  {
	        SecureRandom randomGenerator = SecureRandom.getInstance("SHA1PRNG");
	        byte[] salt = new byte[16];
	        randomGenerator.nextBytes(salt);
	        return salt;
	    }
		public static char[] bytesToChars(byte[] bytes) {
			char[] chars = new char[bytes.length];
			for (int i = 0; i < chars.length; i++) {
				chars[i] = (char) bytes[i];
			}
			return chars;
		}
		private static byte[] PBKDF2(byte[] plain, String salt, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException {        
	        PBEKeySpec spec = new PBEKeySpec(bytesToChars(plain), hexStringToByteArray(salt), iterations, 64 * 8);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        return skf.generateSecret(spec).getEncoded();
	    }
		
		public static byte[] ensureLength(byte[] bytes, int length) {
			if (bytes.length == length)
				return bytes;
			byte[] newBytes = new byte[length];
			if (bytes.length > length) {
				for (int i = 0; i < length; ++i)
					newBytes[i] = bytes[i];
			} else {
				for (int i = 0; i < bytes.length; ++i)
					newBytes[i] = bytes[i];
				for (int i = bytes.length; i < length; ++i)
					newBytes[i] = 0;
			}
			return newBytes;
		}

		public static byte[] hexStringToByteArray(String s) {
		    int len = s.length();
		    byte[] data = new byte[len / 2];
		    for (int i = 0; i < len; i += 2) {
		        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
		                             + Character.digit(s.charAt(i+1), 16));
		    }
		    return data;
		}
		
		
		private static String encodeBase64(byte[] plain) {
			return bytesToString( Base64.getEncoder().encode(plain));
		}
		
		private static byte[] decodeBase64(String hexString) {
			return Base64.getDecoder().decode(hexStringToByteArray(hexString));
		}
		
		private static byte[] encryptAES(byte[] plainText, byte[] key, byte[] iv) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			SecretKeySpec keySpec = new SecretKeySpec(ensureLength(key, 128/8), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ensureLength(iv, 16)));
			return cipher.doFinal(plainText);
		}
		
		private static char[] decryptAES(byte[] encryptedText, byte[] key, byte[] iv) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			SecretKeySpec keySpec = new SecretKeySpec(ensureLength(key, 128/8), "AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ensureLength(iv, 16)));
			return bytesToChars((cipher.doFinal(encryptedText)));
		}
	    
	private void sendRequest(DataInputStream serverInput, DataOutputStream serverOutput) throws IOException, TransformerException, ParserConfigurationException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
//		String username = "";
//		String verifierString = "";
		masterkey="masterkey";
		byte[] masterKey ;
		byte[] recordKey;
		byte[] recordIV;
		byte[] Key = SHA1(key.toByteArray(), 1);
		masterKey = SHA1(masterkey.getBytes(), 1);
		recordIV = makeSalt();
		String recordSalt = encodeBase64(recordIV);
		recordKey = PBKDF2(masterKey, recordSalt, 1);
	    byte[] usernameKey = PBKDF2(recordKey, "userid", 1);
	    // Encryption
	    byte[] encryptedUsername = encryptAES(username.getBytes(), usernameKey, recordIV);
		
	    String verifier = encodeBase64(SHA1(masterKey, 2));
	    String encryptedBase64Username = encodeBase64(encryptedUsername);
		
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
		name.setValue(username);
		user.setAttributeNode(name);
		
		Attr verifier_ = doc.createAttribute("verifier");
		verifier_.setValue(verifier);
		user.setAttributeNode(verifier_);
		
		
		// shorten way
		// staff.setAttribute("id", "1");
  
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("Outfile.xml"));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
		
		
		byte[] fileData = null;
		try {
			File file = new File("Outfile.xml");
			fileData = new byte[(int) file.length()];
			FileInputStream in = new FileInputStream(file);
			in.read(fileData);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

				
		byte[]encryptedData = Crypting.writeCiphered(fileData, key.toByteArray());
		
		serverOutput.writeInt(encryptedData.length);
		
		serverOutput.write(encryptedData);
		serverOutput.flush();		
	
	}
	
	
	
	private Document obtenerDocumentDeByte(byte[] doc2Xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(doc2Xml));
	}
	
	public static byte[] SHA1(byte[] bytes, int n) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
        for (int i = 0; i < n; ++i) {
        	digest.update(bytes);
        	bytes = digest.digest();
        }
        return bytes;
	}
	
	public static String bytesToString(byte[] bytes) {
		StringBuffer hexBuffer = new StringBuffer();
    	for (int i = 0; i < bytes.length; ++i) {
    		String hex = Integer.toHexString(0xff & bytes[i]);
   	     	if (hex.length() == 1) hexBuffer.append('0');
   	     	hexBuffer.append(hex);
    	}
		return hexBuffer.toString();
	}		
	
	public static void main(String[] args) {
		try {
			Client c = new Client();
			new Thread(c).start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ClientGUI cg =new ClientGUI();
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		Client.username = username;
	}

	public static String getMasterkey() {
		return masterkey;
	}

	public static void setMasterkey(String masterkey) {
		Client.masterkey = masterkey;
	}

	public static String getUrlUsername() {
		return urlUsername;
	}

	public static void setUrlUsername(String urlUsername) {
		Client.urlUsername = urlUsername;
	}

	public static String getUrlAddress() {
		return urlAddress;
	}

	public static void setUrlAddress(String urlAddress) {
		Client.urlAddress = urlAddress;
	}

	public static String getUrlPassword() {
		return urlPassword;
	}

	public static void setUrlPassword(String urlPassword) {
		Client.urlPassword = urlPassword;
	}


}