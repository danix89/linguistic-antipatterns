package linguisticAntipatterns.wordsManipulation.xml;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import linguisticAntipatterns.wordsManipulation.interfaces.WordsSuggestion;

import org.apache.commons.validator.routines.UrlValidator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Classe che implementa il metodo di ricerca dei suggerimenti - per una data sequenza di 
 * caratteri -, sfruttando l'algoritmo di ricerca di Google. 
 * @author Daniele Iannone
 *
 */
public class SAXWordsSuggestion implements WordsSuggestion {
	private static String googleSuggestQueryURL = "http://suggestqueries.google.com/complete/search?client=toolbar&q=";

	/**
	 * Controlla se l'url è ancora valida. 
	 * @return <b>true</b> se l'url è valida, <b>false</b> altrimenti.
	 */
	public static boolean isURLValid() {
		String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
		UrlValidator urlValidator = new UrlValidator(schemes);
		
		return urlValidator.isValid(googleSuggestQueryURL);
	}
	
	/**
	 * Restituisce una serie di suggerimenti per un dato insieme di caratteri, sfruttando 
	 * l'algoritmo di ricerca implementato da Google, effettuando una query al server di 
	 * Google stessa. In particolare viene effettuata una connessione http al server, 
	 * quindi si procede con il processare il file XML (scaricato in seguito a tale 
	 * connessione), al fine di ottenere la lista di suggerimenti. 
	 * @param str Una stringa che rappresenta l'insieme di caratteri rispetto ad i quali
	 * trovare una serie di suggerimenti.
	 * @return La lista di parole associate all'insieme di caratteri <b>cset</b>. Nel caso 
	 * non ci siano suggerimenti, la lista sarà vuota.
	 */
	public List<CompleteSuggestion> wordSuggestion(String str) {
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
		SAXParser parser = null;
		try {
			parser = parserFactor.newSAXParser();
		} catch (ParserConfigurationException | SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SAXAutocompleteHandler handler = new SAXAutocompleteHandler();
		if(str != null && str.length() != 0) {
			URL url;
			try {
				url = new URL(googleSuggestQueryURL	+ str);
				parser.parse(new InputSource(url.openStream()), 
						handler);
			} catch (SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return handler.getComSugestionList();
	}
}
