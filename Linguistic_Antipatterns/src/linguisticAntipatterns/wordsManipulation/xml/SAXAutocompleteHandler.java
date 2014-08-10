package linguisticAntipatterns.wordsManipulation.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Fornisce i metodi per il parsing del file XML scaricato in seguito all'esecuzione della 
 * query al server Google.
 * @author Daniele Iannone
 *
 */
public class SAXAutocompleteHandler extends DefaultHandler {
	private List<CompleteSuggestion> comSugestionList = new ArrayList<>();
	private CompleteSuggestion comSugestion = null;
	
	/**
	 * Restituisce la lista delle parole trovate nel file XML.
	 * @return La lista di suggerimenti.
	 */
	public List<CompleteSuggestion> getComSugestionList() {
		return comSugestionList;
	}

	@Override
	//Triggered when the start of tag is found.
	public void startElement(String uri, String localName, 
			String qName, Attributes attributes) 
					throws SAXException {

		switch(qName){
		//Create a new Employee object when the start tag is found
		case "suggestion":
			comSugestion = new CompleteSuggestion();
			comSugestion.suggestion = attributes.getValue("data");
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, 
			String qName) throws SAXException {
		switch(qName){
		//Add the employee to list once end tag is found
		case "CompleteSuggestion":
			getComSugestionList().add(comSugestion);       
			break;
			//For all other end tags the employee has to be updated.
		case "suggestion":
//			comSugestion.suggestion = content;
			break;
		}
	}

//	@Override
//	public void characters(char[] ch, int start, int length) 
//			throws SAXException {
//		content = String.copyValueOf(ch, start, length).trim();
//	}
	
//	private String content = null;
}
