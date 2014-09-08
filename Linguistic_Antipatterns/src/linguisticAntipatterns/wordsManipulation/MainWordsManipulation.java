package linguisticAntipatterns.wordsManipulation;

import java.util.ArrayList;
import java.util.HashMap;
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

	/**
	 * Restituisce una lista di parole che possono essere usate per suggerire una collezione 
	 * di oggetti.
	 * @return La lista di parole.
	 */
	public static List<String> getCollectionRegex() {
		ArrayList<String> collectionRegex = new ArrayList<String>();
		//da completare con altri nomi che suggeriscano una collezione di oggetti
		collectionRegex.add("collection");
		collectionRegex.add("list");
		collectionRegex.add("map");
		collectionRegex.add("array");
		collectionRegex.add("vector");
		collectionRegex.add("table");
		
		return collectionRegex;
	}
	
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
	 * non ci siano suggerimenti la lista � vuota), se l'URL � valida; una lista vuota, 
	 * altrimenti.
	 */
	public static List<CompleteSuggestion> wordSuggestion(String str) {
		
		/*
		 * Se � stato scelto l'algoritmo di Google, controllo che l'url sia valida 
		 * (i.e., non � stata cambiata); se invece non � stato selezionato nessun altro 
		 * algoritmo, scelgo quello di Google; altrimenti, scelgo un altro algoritmo. 
		 */
		if(ws instanceof SAXWordsSuggestion) {
			if(!(SAXWordsSuggestion.isURLValid())) {
				return new ArrayList<CompleteSuggestion>(); //per ora non sono disponibili altri algoritmi di ricerca in RepoMiner.
			} else {
				/* 
				 * Questo controllo viene fatto solo per sicurezza (ws potrebbe non 
				 * risultare inizializzato), in quanto non vi sono 
				 * ancora altri algoritmi: quando sar� implementato un altro algoritmo, si 
				 * potr� modificare questo controllo in modo che venga usato quest'ultimo 
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
	 * <b>regexsList</b>. Se <b>wholeWord</b> � <b>true</b>, in <b>str</b> viene effettuata 
	 * un'operazione di pulizia iniziale della stringa, al fine di isolare eventuali parole (nel caso
	 * di stringhe formate da pi� parole) rispetto le quali andare a controllare il match con le 
	 * espressioni regolari.
	 * @param regexsList Una lista di espressioni regolari. 
	 * @param str La stringa su cui andare a controllare il match. 
	 * @param wholeWord Se viene settata a <b>true</b>, nel caso in cui in <b>str</b> vi siano pi� 
	 * parole, vengono effettuate delle operazioni di pulitura della stringa, per migliorare la 
	 * qualit� della ricerca.  
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
			 * Se wholeWord � uguale a true, isolo l'espressione regolare e la singola parola 
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
	 * Se <b>wholeWord</b> � <b>true</b>, in <b>str</b> viene effettuata 
	 * un'operazione di pulizia iniziale della stringa, al fine di isolare eventuali parole (nel caso
	 * di stringhe formate da pi� parole) rispetto le quali andare a controllare il match con 
	 * l'espressione regolare.
	 * @param regex Una espressione regolare. 
	 * @param str La stringa su cui andare a controllare il match. 
	 * @param wholeWord Se viene settata a <b>true</b>, nel caso in cui in <b>str</b> vi siano pi� 
	 * parole, vengono effettuate delle operazioni di pulitura della stringa, per migliorare la 
	 * qualit� della ricerca.  
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
	 * Restituisce la lista dei sinonimi della parola <b>word</b>.
	 * @param word La parola per cui trovare i sinonimi.
	 * @return La lista di sinonimi.
	 */
	public static List<String> getSynonyms(String word) {
		List<String> synList = new ArrayList<String>();
		String[] wordForms = null; 
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(word);
				
		int synsetsLen = synsets.length, wordFormsLen = 0;
		for(int i = 0; i < synsetsLen; i++) {
			wordForms = synsets[i].getWordForms();
			wordFormsLen = wordForms.length;
			for(int j = 0; j < wordFormsLen; j++) {
				synList.add(wordForms[j]);
			}
		}
		return synList;
	}
	
	/**
	 * Restituisce la lista dei contrari della parola <b>word</b>.
	 * @param word La parola per cui trovare i contrari.
	 * @return La lista di contrari se ce ne sono; una lista vuota altrimenti.
	 */
	public static List<String> getAntonyms(String word) {
		if(word.length() <= 0 || word.startsWith(" "))
			return new ArrayList<String>();
		word = word.toLowerCase();

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		/*
		 * Non posso usare il metodo getSynonyms(), poich� mi serve un oggetto Synset per 
		 * chiamare il metodo getAntonyms().  
		 */
		Synset[] synsets = database.getSynsets(word);
		
		List<String> antList = new ArrayList<String>();
		int antLen = 0;
		int len = synsets.length;
		for(int i = 0; i < len; i++) {
			WordSense[] ant = synsets[i].getAntonyms(word);
			if(ant.length > 0) {
				antLen = ant.length;
				for(int j = 0; j < antLen; j++)
					antList.add(ant[j].getWordForm());
			}
		}
		
		return antList;
	}
	
	/**
	 * Controlla se tra le parole contenute in <b>wordsList</b> � contenuto almeno un 
	 * contrario della parola contenuta in <b>word</b> effettuando i seguenti controlli:
	 * <ol>
	 * <li>Se <b>word</b> � contenuta in <b>wordsList</b> e il valore associato 
	 * alla chiave relativa a <b>word</b> � <b>true</b>, allora in <b>wordsList</b> � 
	 * contenuto il contrario di <b>word</b>: nella frase da cui � stata estrapolata 
	 * <b>wordsList</b>, <b>word</b> risulta essere contenuta in forma negata;</li>
	 * <li>Se il contrario di <b>word</b> � contenuto in <b>wordsList</b> e il valore 
	 * associato alla chiave relativa a tale parole � <b>false</b>, allora in <b>wordsList</b> � 
	 * contenuto il contrario di <b>word</b>: nella frase da cui � stata estrapolata 
	 * <b>wordsList</b>, <b>word</b> risulta essere contenuta in forma non negata.</li>
	 * </ol>
	 * <br>
	 * <b>Nota:</b> Per funzionare correttamente, tale metodo richiede che la {@link HashMap}
	 * <b>wordsList</b> sia generata tramite il metodo {@link MainWordsManipulation#cleanComment(String)}.
	 * @param wordsList La lista delle parole tra cui cercare eventuali contrari della parola
	 * contenuta in <b>word</b>.
	 * @param word La parola rispetto alla quale controllare se in <b>wordsList</b> sono 
	 * contenuti suoi contrari.
	 * @return <b>true</b> se in <b>wordsList</b> � contenuto almeno un contrario di <b>word</b>, <b>false</b> altrimenti.
	 */
	public static boolean checkAntonym(HashMap<String, Boolean> wordsList, String word) {
		List<String> antList = new ArrayList<String>();
		int antSize = 0;
		Boolean isNegated = wordsList.get(word);
		
		if(isNegated != null && isNegated)
			return true;
		
		antList = MainWordsManipulation.getAntonyms(word);
		if(antList.size() > 0) { 
			antSize = antList.size();
			for(int k = 0; k < antSize; k++) {
				isNegated = wordsList.get(antList.get(k));
				if(isNegated != null && !isNegated)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Crea una nuova lista, effettuando un'operazione di pulitura della lista <b>sugList</b>, 
	 * ossia:
	 * <ol>
	 * <li>In caso di suggerimenti composti da due o pi� parole, vengono cancellate tutte le 
	 * parole che seguono la prima;</li>
	 * <li>Vengono eliminate eventuali parole duplicate, a causa dell'eliminazione delle 
	 * parole successive alla prima (qualora vi fossero);</li>
	 * <li>Se nella lista sono presenti parole non contenute nel dizionario WordNet, 
	 * quest'ultime vengono cancellate;</li>
	 * <li>Gli oggetti {@link CompleteSuggestion} (contenuti in <b>sugList</b>) vengono 
	 * convertiti in {@link String}.</li>
	 * </ol>
	 * @param sugList La lista di suggerimenti restituita dal metodo 
	 * {@link SAXWordsSuggestion#wordSuggestion(String)}.
	 * @return La lista <b>sugList</b> "pulita".
	 */
	public static List<String> cleanList(List<CompleteSuggestion> sugList) {
		List<String> tmpSugList = new ArrayList<String>();
		
		int size = sugList.size();
		for (int i = 0; i < size; i++) {
			String tmp = sugList.get(i).toString().split(" ")[0];
			if(!tmpSugList.contains(tmp) 
					&& MainWordsManipulation.getSynonyms(tmp).size() > 0)
				tmpSugList.add(tmp);
		} 
		
		return tmpSugList;
	}

	/**
	 * Crea una {@link HashMap} usando, come chiave, tutte le parole contenute in <b>comment</b> 
	 * e, come valore, un oggetto {@link Boolean}, che indica se una data parola - che viene 
	 * inserita, appunto, nella nuova {@link HashMap} - risulta essere negata (<b>true</b>) 
	 * o meno (<b>false</b>) in <b>comment</b>.  
	 * @param comment La frase da cui estrapolare tutte le parole da inserire nella 
	 * {@link HashMap}.
	 * @return La {@link HashMap} di cui sopra.
	 */
	public static HashMap<String, Boolean> cleanComment(String comment) {
		List<String> negationRegexList = new ArrayList<String>(); 
		
//		System.out.println("\tCommento prima: " + comment);
		comment = comment.replace("/", "");
		comment = comment.replace("**", "");
		comment = comment.replace("*", " ");
//		System.out.println("\tCommento dopo: " + comment);
		
		/*
		 * Da completare con altre forme di negazione
		 */
		negationRegexList.add("n't");
		negationRegexList.add("no");
		negationRegexList.add("not");
		negationRegexList.add("never");
		
		HashMap<String, Boolean> commentWordsHashMap = new HashMap<String, Boolean>();
		String[] words = comment.split(" ");
		int len = words.length;
		for(int i = 0; i < len; i++) {
			if(!words[i].equals(",") && !words[i].equals(";") && 
					!words[i].equals(".") && !words[i].equals(":") &&
					!words[i].equals("_") && !words[i].equals("-") && 
					!words[i].equals("(") && !words[i].equals(")") &&
					!commentWordsHashMap.containsKey(words[i])) {
				Boolean isNegated = false;
				
				if((i > 0 && checkPatterns(negationRegexList, words[i-1], false)) || 
						(i > 1 && checkPatterns(negationRegexList, words[i-2], false)))
					isNegated = true;
				
				commentWordsHashMap.put(words[i], isNegated);
			}
		}
		return commentWordsHashMap;
	}
}
