/**
 * 
 */
package core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author mm
 *
 */
public class GoogleCrawlerTest {

	String searchTerm = "coca+cola+pepsi+health";
	String pageNumber = "100";
	
	private GoogleCrawler gc;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		gc = new GoogleCrawler();
	}

	@Test
	public void testGetUrls(){
		try {
			CopyOnWriteArrayList<String> urls = gc.getUrls(searchTerm, pageNumber);
			
			assertNotNull(urls);
			
			assertEquals("checking if urls returned are 101 - one for images", 101, urls.size());
			
			
		} catch (IOException | SAXException | ParserConfigurationException e) {
			fail("Exception occured during test of GoogleCrawler");
			e.printStackTrace();
		}
		
	}

}
