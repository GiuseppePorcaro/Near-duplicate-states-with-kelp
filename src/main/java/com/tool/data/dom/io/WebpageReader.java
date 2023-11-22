package com.tool.data.dom.io;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.tool.data.dom.DomNode;
import com.tool.data.dom.DomRepresentation;
import org.jsoup.nodes.Node;

/**
 * Builds a DomRepresentation for a given web page.
 * 
 * @author Luigi Libero Lucio Starace
 *
 */
public class WebpageReader {
	
	private DomRepresentationStrategy strategy;
	public static final DomRepresentationStrategy REPRESENT_DOM_AS_IS = new DomRepresentationAsIs();
	public static final DomRepresentationStrategy REPRESENT_DOM_ONLY_BODY = new DomRepresentationOnlyBody();
	public static final DomRepresentationStrategy REPRESENT_DOM_ONLY_BODY_NO_SCRIPTS = new DomRepresentationOnlyBodyNoScripts();
	
	public WebpageReader() {}
	
	public WebpageReader(DomRepresentationStrategy strategy) {
		this.strategy = strategy;
	}

	public WebpageReader(String domRepresentationStrategy) {
		if ("plain".equals(domRepresentationStrategy)) {
			this.strategy = WebpageReader.REPRESENT_DOM_AS_IS;
		} else if ("only_body".equals(domRepresentationStrategy)) {
			this.strategy = WebpageReader.REPRESENT_DOM_ONLY_BODY;
		} else if ("only_body_no_scripts".equals(domRepresentationStrategy)) {
			this.strategy = WebpageReader.REPRESENT_DOM_ONLY_BODY_NO_SCRIPTS;
		} else {
			throw new IllegalArgumentException("Unknown DOM representation strategy: " + domRepresentationStrategy);
		}
	}
	
	/**
	 * Builds a DomRepresentation object for the html file 
	 * <code>file</code>, using the current DOM representation
	 * strategy.
	 * 
	 * @param htmlFile File object corresponding to some html file.
	 * @return a DomRepresentation for the file at <code>filePath</code>
	 * @throws IOException 
	 */
	public DomRepresentation getDOMRepresentationFromFile(File htmlFile) throws IOException {
		Document html = Jsoup.parse(htmlFile,"UTF-8");
		return strategy.getDomRepresentationFromDocument(html);
	}
	
	
	
	public DomRepresentation getDOMRepresentationFromString(String htmlString) {
		Document html = Jsoup.parse(htmlString);		
		return strategy.getDomRepresentationFromDocument(html);
	}
	
	
	public static interface DomRepresentationStrategy {
		public DomRepresentation getDomRepresentationFromDocument(Document html);
	}
	
	public static class DomRepresentationAsIs implements DomRepresentationStrategy {

		public DomRepresentation getDomRepresentationFromDocument(Document html) {
			Element root = html.root().child(0);
			DomNode domRoot = new DomNode(0, root, null);
			return new DomRepresentation(domRoot);
		}
		
	}
	
	public static class DomRepresentationOnlyBody implements DomRepresentationStrategy {

		public DomRepresentation getDomRepresentationFromDocument(Document html) {
			Element root = html.getElementsByTag("body").first();
			DomNode domRoot = new DomNode(0, root, null);
			return new DomRepresentation(domRoot);
		}
		
	}
	
	public static class DomRepresentationOnlyBodyNoScripts implements DomRepresentationStrategy {

		public DomRepresentation getDomRepresentationFromDocument(Document html) {
			html.select("script").remove(); //remove all script elements
			Element root = html.getElementsByTag("body").first();
			DomNode domRoot = new DomNode(0, root, null);
			return new DomRepresentation(domRoot);
		}
		
	}
}
