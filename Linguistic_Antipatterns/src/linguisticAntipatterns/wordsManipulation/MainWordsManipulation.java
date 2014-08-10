package linguisticAntipatterns.wordsManipulation;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import linguisticAntipatterns.wordsManipulation.interfaces.WordsSuggestion;
import linguisticAntipatterns.wordsManipulation.xml.CompleteSuggestion;
import linguisticAntipatterns.wordsManipulation.xml.SAXWordsSuggestion;

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
	 * @param cset L'insieme di caratteri rispetto al quale trovare una serie di suggerimenti.
	 * @return La lista di parole associate all'insieme di caratteri <b>cset</b>.
	 * @throws Exception
	 */
	public static List<CompleteSuggestion> wordSuggestion(Charset cset) throws Exception {
		
		/*
		 * Controllo che l'url sia valido (i.e., non è stato cambiato). In caso contrario,
		 * scelgo un altro algoritmo.
		 * 
		 */
		if(ws instanceof SAXWordsSuggestion) {
			if(!(SAXWordsSuggestion.isURLValid())) {
				return null; //per ora non sono disponibili altri algoritmi di ricerca in RepoMiner.
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
		}
		
		return ws != null ? ws.wordSuggestion(cset) : null;
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
}
