package linguisticAntipatterns.methods;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
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
	protected static boolean checkPattern(List<String> regexsList, String str, boolean wholeWord) {
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
	protected static boolean checkPattern(String regex, String str, boolean wholeWord) {
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
	
	protected static final String collectionRegex = "list | collection | map | table"; //da completare con altri nomi che suggeriscano una collezione di oggetti
}
