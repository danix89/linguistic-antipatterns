package linguisticAntipatterns.methods;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import linguisticAntipatterns.wordsManipulation.MainWordsManipulation;
import linguisticAntipatterns.wordsManipulation.xml.CompleteSuggestion;
import sie.db.entity.CodeComment;
import sie.db.entity.Method;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class CommonFeature {
	/**
	 * Controlla se il nome del metodo o della variabile contiene parole che corrispondono 
	 * al contrario di una delle parole del commento. 
	 * @param name Il nome del metodo o della variabile.
	 * @param commentsSet L'insieme dei commenti associati al metodo o alla variabile, ottenuto
	 * chiamando - rispettivamente - il metodo {@link Method#getComments()} e 
	 * {@link sie.db.entity.Field#getComments()}. 
	 * @return <b>true</b> se <b>name</b> contiene almeno un contrario di una delle parole 
	 * contenute in <b>comment</b>, <b>false</b> altrimenti.
	 */
	public static boolean checkCommentAntonym(String name, Set<CodeComment> commentsSet) {
		String comment = "";
		HashMap<String, Boolean> nameWords = null, cleanedComment = null;
		
		for (Iterator<CodeComment> iterator = commentsSet.iterator(); iterator.hasNext();) {
			CodeComment cc = (CodeComment) iterator.next();
			comment = comment.concat(cc.getComment());
		}
			
		nameWords = getAllWords(name);
		cleanedComment = MainWordsManipulation.cleanComment(comment);
		
		/*
		 * Se non ci sono commenti - ovviamente - non può esserci nessun anti-pattern relativo 
		 * ai commenti.
		 */
		if(cleanedComment.isEmpty())
			return false;
		
		if(checkAntonym(nameWords, cleanedComment))
			return true;
		
		return false;
	}
	
	/**
	 * Controlla se il nome del metodo o della variabile contiene parole che corrispondono 
	 * al contrario di una delle parole estrapolate -rispettivamente - dal nome del tipo di 
	 * ritorno o della variabile. 
	 * @param name Il nome del metodo o della variabile.
	 * @param typeName Il nome del tipo di ritorno o della variabile. 
	 * @return <b>true</b> se <b>name</b> contiene almeno un contrario di una delle parole 
	 * estrapolate da <b>typeName</b>, <b>false</b> altrimenti.
	 */
	public static boolean checkTypeAntonym(String name, String typeName) {
		HashMap<String, Boolean> nameWords = getAllWords(name);
		HashMap<String, Boolean> typeNameWords = getAllWords(extractTypeName(typeName));
		
		if(checkAntonym(nameWords, typeNameWords))
			return true;
		else
			return false;
	}
	
	private static HashMap<String, Boolean> getAllWords(String name) {
		boolean underscoreFlag = false, upperCaseFlag = false;
		String tmp = "";
		int i = 0, c = 0, sugSize = 0, typeNameLen = 0;
		//Conterrà tutte le possibili parole di senso compiuto, associate a name.  
		HashMap<String, Boolean> nameWords = new HashMap<String, Boolean>(); 
		List<String> sugList = null;
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = null;
		
		if(name.contains("_")) {
			underscoreFlag = true;
			
			for(String str : name.split("_")) {
				synsets = database.getSynsets(tmp);
				if(synsets.length > 0)
					nameWords.put(str, false);
				else {
					sugList = 
							MainWordsManipulation.cleanList(MainWordsManipulation.wordSuggestion(str));
					sugSize = sugList.size(); 
					if(sugSize > 0) {
						for (int j = 0; j < sugSize; j++) {
							nameWords.put(sugList.get(j), false);
						}
					}
				}
			}
		}
		
		typeNameLen = name.length();
		if(!underscoreFlag) {
			i = 1;
			/*
			 * Memorizzo ciascuna parola che precede una lettera maiuscola (ad esempio, 
			 * nel caso in cui name sia uguale a aSimpleExample, memorizzo le stringhe "a", 
			 * "Simple" e "Example").  
			 */
			while(i < typeNameLen) {
				while(i < typeNameLen && 
						(Character.isLowerCase(name.charAt(i)) ||
								Character.isDigit(name.charAt(i)))) {
					i++;
				}
				
				if(i < typeNameLen && Character.isUpperCase(name.charAt(i))) {
					upperCaseFlag = true;
					if(i > 0) {
						tmp = name.substring(c, i);
						
						database = WordNetDatabase.getFileInstance();
						synsets = database.getSynsets(tmp);
						
						/*
						 * Se tmp contiene una parola di senso compiuto, la memorizzo; 
						 * altrimenti memorizzo tutte le parole più probabili, ottenibili a 
						 * partire da tmp.   
						 */
						if(synsets.length > 0) {
							nameWords.put(tmp, false);
						} else {
							for(CompleteSuggestion s : MainWordsManipulation.wordSuggestion(tmp)) {
								nameWords.put(s.toString(), false);
							}
						}
					}
					c = i;
					i++;
				}
			}
			
			/*
			 * Se in name non sono contenute lettere maiuscole (e, quindi, non è stata ancora 
			 * analizzata la stringa contenuta in name), procedo con il memorizzare ciascuna 
			 * parola estrapolata da tutte le sotto-stringhe (di lunghezza maggiore di uno) 
			 * ottenibili da name. 
			 * Ad esempio, se ho la stringa "prov", verranno analizzate le seguenti sue 
			 * sotto-stringhe:
			 * 	1. prov;
			 * 	2. pro;
			 * 	3. pr;
			 *  4. rov;
			 *  5. ro;
			 *  6. ov
			 */
			if(!upperCaseFlag) {
				if(MainWordsManipulation.getSynonyms(name).size() > 0) {
					nameWords.put(name, false);
				} else {
					for(i = 0; i < typeNameLen; i++) {
						for (int j = typeNameLen; j > i; j--) {
							if(j - i > 1) {
								tmp = name.substring(i, j);
								nameWords.put(tmp, false);
							}
						}
					}
				}
			}
		}
		return nameWords;
	}

	private static boolean checkAntonym(HashMap<String, Boolean> nameWords, 
			HashMap<String, Boolean> words) {
		for(String name : nameWords.keySet()) {
			if(MainWordsManipulation.checkAntonym(words, name))
				return true;
		}
		
		return false;
	}

	/**
	 * Estrae solo il nome del metodo, cancellando tutto ciò che segue la parentesi d'apertura
	 * (parentesi compresa).
	 * @param metName Il nome del metodo ottenuto tramite il metodo {@link Method#getName()}.
	 * @return Solo il nome del metodo.
	 */
	public static String extractMethodName(String metName) {
		int i = 0, metLen = metName.length();
		/*
		 * Estraggo solo il nome del metodo.
		 */
		while(i < metLen && metName.charAt(i) != '(') {
			i++;
		}
//		System.out.println("\tNome metodo prima: " + methodName);
		metName = metName.substring(0, i);
//		System.out.println("\tNome metodo dopo: " + methodName);
		
		return metName;
	}
	
	/**
	 * Estrae solo il nome del tipo, cancellando tutto ciò che segue la parentesi d'apertura
	 * (parentesi compresa).
	 * @param typeName Il nome del tipo ottenuto tramite il metodo 
	 * {@link Method#getReturnType()}, oppure {@link sie.db.entity.Field#getType()}.
	 * @return Solo il nome del tipo.
	 */
	public static String extractTypeName(String typeName) {
		int i = 0, j = 0, metLen = typeName.length();
		
		/*
		 * Estraggo solo il nome del tipo.
		 */
		while(i < metLen && (typeName.charAt(i) != '[' && typeName.charAt(i) != '<')) {
			i++;
			
			/*
			 * Gestisco il caso in cui il tipo dell'oggetto appartenga ad una classe interna
			 * (i.e., definita all'interno di un'altra classe) e, quindi, il nome è strutturato
			 * nel modo seguente: 
			 * <NomeClasseEsterna>.<NomeClasseInterna>. 
			 */
			if(i < metLen && typeName.charAt(i) == '.')
				j = i;
		}
		
//		System.out.println("\tNome metodo prima: " + typeName);
		typeName = typeName.substring(j, i);
//		System.out.println("\tNome metodo dopo: " + typeName);
		
		return typeName;
	}
	
	/**
	 * Cerca una serie di parole che si avvicinano di più alla parola contenuta in 
	 * <b>str</b>; quindi, per ogni parola trovata, controlla se corrisponde ad uno dei 
	 * contrari trovati per ciascuna parola contenuta in <b>words</b>.
	 * @param str Il nome del metodo o della variabile, per cui controllare se contiene 
	 * il contrario di almeno una delle parole contenute in <b>words</b>.
	 * @param words La frase rispetto alla quale controllare se sono contenuti contrari 
	 * delle parole estrapolate da <b>str</b>.
	 * @return <b>true</b> se <b>str</b> contiene almeno un contrario di una delle parole 
	 * contenute in <b>words</b>, <b>false</b> altrimenti.
	 */
	private static boolean checkStringSuggAntonym(String str, HashMap<String, Boolean> words) {
		List<String> sugList = 
				MainWordsManipulation.cleanList(MainWordsManipulation.wordSuggestion(str));
		int sugSize = sugList.size(); 
		if(sugList.size() > 0) {
			for(int i = 0; i < sugSize; i++) {
//				System.out.println("parola: " + tmp + " -> " + sugList.get(k));
				
				if(MainWordsManipulation.checkAntonym(words, sugList.get(i))) {
					return true;
				}
			}
		} /*else {
			System.out.println("parola: " + tmp + " -> " + "nessun suggerimento trovato");
		}*/
		return false;
	}
}
