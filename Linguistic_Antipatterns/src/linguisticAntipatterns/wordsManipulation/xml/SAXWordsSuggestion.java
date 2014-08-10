package linguisticAntipatterns.wordsManipulation.xml;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import linguisticAntipatterns.wordsManipulation.interfaces.WordsSuggestion;

import org.apache.commons.validator.routines.UrlValidator;
import org.xml.sax.InputSource;

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
	 * @param cset L'insieme di caratteri rispetto al quale trovare una serie di suggerimenti.
	 * @return La lista di parole associate all'insieme di caratteri <b>cset</b>.
	 * @throws Exception
	 */
	public List<CompleteSuggestion> wordSuggestion(Charset cset) throws Exception {
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
		SAXParser parser = parserFactor.newSAXParser();
		SAXAutocompleteHandler handler = new SAXAutocompleteHandler();
		URL url = new URL(googleSuggestQueryURL	+ cset);
		parser.parse(new InputSource(url.openStream()), 
				handler);
		
		return handler.getComSugestionList();
	}
}
