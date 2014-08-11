package linguisticAntipatterns.wordsManipulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import linguisticAntipatterns.wordsManipulation.interfaces.WordsSuggestion;
import linguisticAntipatterns.wordsManipulation.xml.CompleteSuggestion;
import linguisticAntipatterns.wordsManipulation.xml.SAXWordsSuggestion;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

/**
 * Fornisce una serie di metodi e variaibili utili per la manipolazione delle parole. 
 * @author Daniele Iannone
 *
 */
public class MainWordsManipulation {
	private static WordsSuggestion ws = null;
	public static final String collectionRegex = "list | collection | map | table"; //da completare con altri nomi che suggeriscano una collezione di oggetti	

	/**
	 * Setta l'algoritmo di ricerca delle parole da utilizzare.
	 * @param algorithm Un algoritmo di ricerca che implementi l'interfaccia 
	 * {@link WordsSuggestion}.
	 */
	public void setWordSuggestionAlgorithm(WordsSuggestion algorithm) {
		ws = algorithm;
	}
	
	/**
	 * Restituisce una serie di suggerimenti per un dato insieme di caratteri, sfruttando 
	 * l'algoritmo di ricerca delle parole specificato tramite il metodo  
	 * {@link MainWordsManipulation#setWordSuggestionAlgorithm(WordsSuggestion)}. Ad esempio, se
	 * al metodo viene dato in input l'insieme di caratteri "bo", il metodo restituisce la 
	 * seguente lista di parole:
	 * <ol>
	 * <li>booking</li>
	 * <li>bonprix</li>
	 * <li>bologna</li>
	 * </ol>
	 * @param str Una stringa che rappresenta l'insieme di caratteri rispetto ad i quali
	 * trovare una serie di suggerimenti.
	 * @return La lista di parole associate all'insieme di caratteri <b>cset</b> (nel caso 
	 * non ci siano suggerimenti la lista è vuota), se l'URL è valida; una lista vuota, 
	 * altrimenti.
	 * @throws Exception
	 */
	public static List<CompleteSuggestion> wordSuggestion(String str) throws Exception {
		
		/*
		 * Se è stato scelto l'algoritmo di Google, controllo che l'url sia valida 
		 * (i.e., non è stata cambiata); se invece non è stato selezionato nessun altro 
		 * algoritmo, scelgo quello di Google; altrimenti, scelgo un altro algoritmo. 
		 */
		if(ws instanceof SAXWordsSuggestion) {
			if(!(SAXWordsSuggestion.isURLValid())) {
				return new ArrayList<CompleteSuggestion>(); //per ora non sono disponibili altri algoritmi di ricerca in RepoMiner.
			} else {
				/* 
				 * Questo controllo viene fatto solo per sicurezza (ws potrebbe non 
				 * risultare inizializzato), in quanto non vi sono 
				 * ancora altri algoritmi: quando sarà implementato un altro algoritmo, si 
				 * potrà modificare questo controllo in modo che venga usato quest'ultimo 
				 * al posto di SAXWordsSuggestion, nel caso in cui l'url non sia valida (e 
				 * quindi risulta impossibile usare l'algoritmo SAXWordsSuggestion). 
				 */
				ws = new SAXWordsSuggestion();
			}
		} else if(ws == null && SAXWordsSuggestion.isURLValid()) {
			ws = new SAXWordsSuggestion();
		} /* else {
			
		} */
		
		return ws != null ? ws.wordSuggestion(str) : new ArrayList<CompleteSuggestion>();
	}
	
	/**
	 * Controlla se la stringa <b>str</b> contiene almeno una espressione regolare contenuta in 
	 * <b>regexsList</b>. Se <b>wholeWord</b> è <b>true</b>, in <b>str</b>, viene effettuata 
	 * un'operazione di pulizia iniziale della stringa, al fine di isolare eventuali parole (nel caso
	 * di stringhe formate da più parole) rispetto le quali andare a controllare il match con le 
	 * espressioni regolari.
	 * @param regexsList Una lista di espressioni regolari. 
	 * @param str La stringa su cui andare a controllare il match. 
	 * @param wholeWord Se viene settata a <b>true</b>, nel caso in cui in <b>str</b> vi siano più 
	 * parole, vengono effettuate delle operazioni di pulitura della stringa, per migliorare la 
	 * qualità della ricerca.  
	 * @return <b>true</b> se il pattern viene trovato, <b>false</b> altrimenti.
	 */
	public static boolean checkPatterns(List<String> regexsList, String str, boolean wholeWord) {
		if(wholeWord) {
			str.replace(".", " ");
			str.replace(":", " ");
			str.replace(",", " ");
			str.replace(";", " ");
			str.replace("-", " ");
			str.replace("_", " ");
			str.replace("/", " ");
		}
		
		for(String regex : regexsList) {
			/*
			 * Se wholeWord è uguale a true, isolo l'espressione regolare e la singola parola 
			 * nella stringa, al fine di gestire, ad esempio, il caso in cui non venga inserito lo 
			 * spazio dopo la punteggiatura.  
			 */
			if(wholeWord) {
				regex = "_".concat(regex);
				regex = regex.concat("_");
			}
			
			for(String cs : str.split(" ")) {
				if(wholeWord) {
					cs = "_".concat(cs);
					cs = cs.concat("_");
				}
				
				if(checkPattern(regex, cs, false))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Controlla se la stringa <b>str</b> contiene l'espressione regolare <b>regex</b>. 
	 * Se <b>wholeWord</b> è <b>true</b>, in <b>str</b>, viene effettuata 
	 * un'operazione di pulizia iniziale della stringa, al fine di isolare eventuali parole (nel caso
	 * di stringhe formate da più parole) rispetto le quali andare a controllare il match con 
	 * l'espressione regolare.
	 * @param regex Una espressione regolare. 
	 * @param str La stringa su cui andare a controllare il match. 
	 * @param wholeWord Se viene settata a <b>true</b>, nel caso in cui in <b>str</b> vi siano più 
	 * parole, vengono effettuate delle operazioni di pulitura della stringa, per migliorare la 
	 * qualità della ricerca.  
	 * @return <b>true</b> se il pattern viene trovato, <b>false</b> altrimenti.
	 */
	public static boolean checkPattern(String regex, String str, boolean wholeWord) {
		if(wholeWord) {
			str.replace(".", " ");
			str.replace(":", " ");
			str.replace(",", " ");
			str.replace(";", " ");
			str.replace("-", " ");
			str.replace("_", " ");
			str.replace("/", " ");
		}
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str.toString().toLowerCase());
		
		if(matcher.find())
			return true;
		else
			return false;
	}
	
	/**
	 * Restituisce la lista dei contrari della parola <b>word</b>.
	 * @param word La parola per cui trovare i contrari.
	 * @return La lista di contrari.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String getAntonyms(String word) throws IOException, InterruptedException {
		if(word.length() <= 0 || word.startsWith(" "))
			return null;

		word = word.toLowerCase();
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(word);
		
		int len = synsets.length;
		for(int i = 0; i < len; i++) {
			WordSense[] ant = synsets[i].getAntonyms(word);
			if(ant.length > 0) {
				return ant[0].getWordForm();
			}
		}
		
		return null;
	}
}
