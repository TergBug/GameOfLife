package terg.evolife;

import java.io.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Setup {
	public static String temp(String in) throws IOException {
		String s = ClassLoader.getSystemResource(in).getFile().replace(".jar!", "").replace("file:", "");
		File f = new File(s);
		if(f.exists()) { return s; }
		else {
			f = new File(s.substring(0, s.indexOf(in)-1));
			if(!f.exists()) { f.mkdir(); }
			f = new File(s);
			f.createNewFile();
		}
		BufferedInputStream is = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(in));
		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
		int n = 0;
		while((n=is.read())!=-1) { os.write(n); }
		os.flush();
		is.close();
		os.close();
		return s;
	}
	private Player[] players = new Player[4];
	private int maxpop = 0;
	private int health = 0;
	private int maxgen = 0;
	private File f;
	public void writeSetup(int maxpop, int health, int maxgen, Player[] p) throws Exception {
		f = new File(Setup.temp("setup.xml"));
		if(!f.exists()) { throw new Exception("There is no file setup.xml"); }
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			WriterXML write = new WriterXML(maxpop, health, maxgen, p);
			saxParser.parse(f, write);
			BufferedOutputStream fw = new BufferedOutputStream(new FileOutputStream(f));
			fw.write(write.getWriteStr().getBytes());
			fw.flush();
			fw.close();
		} catch(IOException e) { e.printStackTrace(); }
	}
	public void check() throws Exception {
		f = new File(Setup.temp("setup.xml"));
		if(!f.exists()) { throw new Exception("There is no file setup.xml"); }
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			ReaderXML read = new ReaderXML();
			saxParser.parse(f, read);
		} catch(IOException e) { e.printStackTrace(); }
	}
	public Player[] getPlayers() {
		return players;
	}
	public int getMaxpop() {
		return maxpop;
	}
	public int getHealth() {
		return health;
	}
	public int getMaxgen() {
		return maxgen;
	}
	private class ReaderXML extends DefaultHandler {
		private boolean bMaxPop = false;
		private boolean bHealth = false;
		private boolean bMaxGen = false;
		private boolean bPlayer_Name = false;
		private boolean bPlayer_Color = false;
		private String name = "";
		private String color = "";
		private int iPlayer = 0;
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if(qName.equalsIgnoreCase("maxpop")) { bMaxPop = true; }
			else if(qName.equalsIgnoreCase("health")) { bHealth = true; }
			else if(qName.equalsIgnoreCase("maxgen")) { bMaxGen = true; }
			else if(qName.equalsIgnoreCase("name")) { bPlayer_Name = true; }
			else if(qName.equalsIgnoreCase("color")) { bPlayer_Color = true; }
		}
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if(qName.equalsIgnoreCase("maxpop")) { bMaxPop = false; }
			else if(qName.equalsIgnoreCase("health")) { bHealth = false; }
			else if(qName.equalsIgnoreCase("maxgen")) { bMaxGen = false; }
			else if(qName.equalsIgnoreCase("player")) { players[iPlayer++] = new Player(name, color); }
			else if(qName.equalsIgnoreCase("name")) { bPlayer_Name = false; }
			else if(qName.equalsIgnoreCase("color")) { bPlayer_Color = false; }
		}
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if(bMaxPop) { maxpop = Integer.parseInt(new String(ch, start, length)); }
			else if(bHealth) { health = Integer.parseInt(new String(ch, start, length)); }
			else if(bMaxGen) { maxgen = Integer.parseInt(new String(ch, start, length)); }
			else if(bPlayer_Name) { name = new String(ch, start, length); }
			else if(bPlayer_Color) { color = new String(ch, start, length); }
		}
	}
	private class WriterXML extends DefaultHandler {
		private boolean bMaxPop = false;
		private boolean bHealth = false;
		private boolean bMaxGen = false;
		private boolean bPlayer_Name = false;
		private boolean bPlayer_Color = false;
		private Player[] players;
		private int iPlayer;
		private int maxpop;
		private int health;
		private int maxgen;
		private String s;
		public WriterXML(int maxpop, int health, int maxgen, Player[] p) {
			this.players = p;
			this.maxpop = maxpop;
			this.health = health;
			this.maxgen = maxgen;
			iPlayer = 0;
			s = "";
		}
		@Override
		public void startDocument() throws SAXException {
			s += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		}
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if(qName.equalsIgnoreCase("maxpop")) { bMaxPop = true; }
			else if(qName.equalsIgnoreCase("health")) { bHealth = true; }
			else if(qName.equalsIgnoreCase("maxgen")) { bMaxGen = true; }
			else if(qName.equalsIgnoreCase("name")) { bPlayer_Name = true; }
			else if(qName.equalsIgnoreCase("color")) { bPlayer_Color = true; }
			s += "<"+qName+">";
		}
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if(qName.equalsIgnoreCase("maxpop")) { bMaxPop = false; }
			else if(qName.equalsIgnoreCase("health")) { bHealth = false; }
			else if(qName.equalsIgnoreCase("maxgen")) { bMaxGen = false; }
			else if(qName.equalsIgnoreCase("player")) { iPlayer++; }
			else if(qName.equalsIgnoreCase("name")) { bPlayer_Name = false; }
			else if(qName.equalsIgnoreCase("color")) { bPlayer_Color = false; }
			s += "</"+qName+">";
		}
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if(bMaxPop) { s += String.valueOf(maxpop); }
			else if(bHealth) { s += String.valueOf(health); }
			else if(bMaxGen) { s += String.valueOf(maxgen); }
			else if(bPlayer_Name) { s += players[iPlayer].getNickname(); }
			else if(bPlayer_Color) { s += players[iPlayer].getColorStr(); }
		}
		public String getWriteStr() {
			return s;
		}
	}
}
