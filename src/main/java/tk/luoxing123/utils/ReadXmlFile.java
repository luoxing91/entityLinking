package tk.luoxing123.utils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReadXmlFile {

	public static void main(String[] args) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				boolean bfname = false;
				boolean blname = false;
				boolean bnname = false;
				String bsalary = null;

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					if (qName.equalsIgnoreCase("query")) {
						bfname = true;
					}
					if (qName.equalsIgnoreCase("name")) {
						blname = true;
					}
					if (qName.equalsIgnoreCase("docid")) {
						bnname = true;
					}
					if (qName.equalsIgnoreCase("beg")) {
						bsalary = null;
					}
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {

				}
				public void characters(char ch[], int start, int length)
						throws SAXException {
					if (bfname) {
						System.out.println("First Name : "
								+ new String(ch, start, length));
						bfname = false;
					}
					if (blname) {
						System.out.println("Last Name : "
								+ new String(ch, start, length));
						blname = false;
					}
					if (bnname) {
						System.out.println("Nick Name : "
								+ new String(ch, start, length));
						bnname = false;
					}
					if (bsalary == null) {
						bsalary = new String(ch, start, length);
						System.out.println("Salary : " + bsalary);
					}

				}

			};

			saxParser.parse(Constant.queryXmlFile, handler);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
