package linguisticAntipatterns.wordsManipulation.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
	private static final int MAX_QUERIES = 100;
	private static final long SLEEP_TIME = 60000;

	private static int conectionCounter = 0;
	private static String googleSuggestQueryURL = "http://suggestqueries.google.com/complete/search?client=toolbar&q=";

	/**
	 * Controlla se l'url � ancora valida. 
	 * @return <b>true</b> se l'url � valida, <b>false</b> altrimenti.
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
	 * non ci siano suggerimenti, la lista sar� vuota.
	 */
	public List<CompleteSuggestion> wordSuggestion(String str) {
		SAXParserFactory parserFactor = SAXParserFactory.newInstance();
		SAXAutocompleteHandler handler = null;
		SAXParser parser = null;
		
		try {
			/**
			 * Se sono state eseguite pi� di MAX_QUERIES attendo per un minuto, quindi riprendo 
			 * l'esecuzione. 
			 */
			if(conectionCounter >= MAX_QUERIES) {
				System.out.println("Maximum allowed queries number reached. Wait for " 
						+ SLEEP_TIME/60000 + " minute");
				Thread.sleep(SLEEP_TIME);
				
				conectionCounter = 0;
			}
			
			parser = parserFactor.newSAXParser();
			handler = new SAXAutocompleteHandler();
			if(str != null && str.length() != 0) {
				URL url;
				url = new URL(googleSuggestQueryURL	+ str);
				/*
				 * Serve per il riconoscimento automatico della codifica, 
				 * usata per il file XML. 
				 */
				Reader isr = new InputStreamReader(url.openStream());
				conectionCounter++;
				InputSource is = new InputSource();
				is.setCharacterStream(isr);
				parser.parse(is, handler);
			}
		} catch (ParserConfigurationException | SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return handler.getComSugestionList();
	}
}
