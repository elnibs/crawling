/**
 * Source code copyrighted to Nibal Sawaya
 * No usage of any sort allowed
 */
package core;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.crawljax.core.CrawlSession;

import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration.CrawljaxConfigurationBuilder;
import com.crawljax.core.state.StateFlowGraph;
import com.crawljax.core.state.StateVertex;

import com.google.common.collect.ImmutableSet;

/**
 * @author Nibal Sawaya
 */
public class GoogleCrawler {

	/**
	 * Makes a Google search using the api and gets the Urls returned by the
	 * result page to be crawled and mined.
	 * 
	 * @param searchTerm
	 *            the text to search for example: coca cola health
	 * @param pageNumber
	 *            the number of results if none exist it means first 100 results
	 *            then it's 200 for second page etc..
	 * @return {@link CopyOnWriteArrayList}
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public CopyOnWriteArrayList<String> getUrls(String searchTerm,
			String pageNumber) throws IOException, SAXException,
			ParserConfigurationException {

		CrawljaxConfigurationBuilder builder = CrawljaxConfiguration
				.builderFor("https://www.google.com/search?num=100&gl=us&authuser=0&q=allintext%3A"
						+ searchTerm);

		builder.setOutputDirectory(new File(
				"/media/Work/MultimediaMonitoringDB/" + searchTerm));
		builder.setMaximumDepth(3);
		// builder.setMaximumStates(2);
		builder.crawlRules().followExternalLinks(true);
		// builder.setOutputDirectory(new File("/media/Work/Crawljax"));
		// *[@id="rso"]/li[2]/div/h3/a
		// *[@id="rso"]/li[2]/div/h3/a

		// *[@id="rso"]/li[1]/div/div/h3/a
		// *[@id="rso"]/li[1]/div/div/h3/a

		// *[@id="rso"]/li[4]/div/h3/a

		// skipping the search button
		// builder.crawlRules().dontClick("button").underXPath("//*[@id='gbqfb']");

		// skip the menu header elements (search, apps, signin)
		builder.crawlRules().dontClickChildrenOf("div").withId("mngb");

		// skip the settings and filters menu (news, images, etc.. , settings)
		builder.crawlRules().dontClickChildrenOf("div").withId("top_nav");

		// skip footer links
		builder.crawlRules().dontClickChildrenOf("div").withId("fbar");

		// don't click the next pages (for now)
		builder.crawlRules().dontClickChildrenOf("div").withId("foot");

		// skip cached elements and similar to links
		builder.crawlRules().dontClickChildrenOf("div").withClass("s");

		// no youtube videos crawled
		// NotUrlCondition notYoutube = new NotUrlCondition("youtube");

		// no google mail or relevant stuff crawled
		// NotUrlCondition noGoogle = new NotUrlCondition("search?");
		// NotUrlCondition noGoogleApps = new NotUrlCondition("/options/");
		// NotUrlCondition noPreferences = new NotUrlCondition("preferences?");
		//
		// //no google search page footer elements
		// NotUrlCondition noGoogleHelp = new NotUrlCondition("support.google");
		// NotUrlCondition noSetPrefDomain = new
		// NotUrlCondition("setprefdomain?");
		// NotUrlCondition noPrivacyAndTerms = new NotUrlCondition("policies");
		//
		//
		// CrawlCondition[] conditions = {
		// new CrawlCondition("no  google links", noGoogle)/*,*/
		// // new CrawlCondition("no  googleApps links", noGoogleApps),
		// // new CrawlCondition("no preferences links", noPreferences),
		// // new CrawlCondition("no google help", noGoogleHelp),
		// // new CrawlCondition("no about google links", noSetPrefDomain),
		// // new CrawlCondition("no privacy and terms", noPrivacyAndTerms),
		// // new CrawlCondition("no  youtube links", notYoutube)
		// };
		// builder.crawlRules().addCrawlCondition(conditions);

		builder.crawlRules().insertRandomDataInInputForms(false);

		// XPathCondition condition = new XPathCondition("//DIV[@class='r']");
		//
		// CrawlCondition cc = new CrawlCondition("only get url results' links",
		// condition);
		//
		// builder.crawlRules().addCrawlCondition(cc);

		// builder.addPlugin(new CrawlOverview());

		CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
		CrawlSession session = crawljax.call();

		StateFlowGraph sfg = session.getStateFlowGraph();

		// System.out.println("Number of states: " + sfg.getNumberOfStates());
		//
		// System.out.println("getMeanStateStringSize: "
		// + sfg.getMeanStateStringSize());

		// System.out.println("getAllEdges: " + sfg.getAllEdges());

		ImmutableSet<StateVertex> states = sfg.getAllStates();

		@SuppressWarnings("rawtypes")
		Iterator it = states.iterator();

		// two states exist, need only first
		StateVertex sv = (StateVertex) it.next();

		System.out
				.println("****************************************************************************\n\n");

		// String domString = sv.getDom();
		// System.out.println("Getting dom for sv " + sv.getName() + "\n\n"
		// + sv.getDom() + "\n\n");

		Document doc = sv.getDocument();

		// System.out.println(doc.getBaseURI());
		// System.out.println(doc.getDocumentURI());
		// System.out.println(doc.getTextContent());

		Element results = doc.getElementById("rso");

		NodeList anchors = results.getElementsByTagName("h3");

		NodeList finalUrls = anchors;
		// System.out.println(h3Tags.getLength());
		Node current = null;
		// NodeList = null;

		NamedNodeMap test = null;

		CopyOnWriteArrayList<String> myUrls = new CopyOnWriteArrayList<String>();

		for (int i = 0; i < finalUrls.getLength(); i++) {
			current = finalUrls.item(i);

			test = current.getFirstChild().getNextSibling().getAttributes();

			// System.out.println(test.getNamedItem("href").getTextContent());

			myUrls.add(test.getNamedItem("href").getTextContent());

		}

		System.out
				.println("****************************************************************************\n\n");
		return myUrls;
	}

}
