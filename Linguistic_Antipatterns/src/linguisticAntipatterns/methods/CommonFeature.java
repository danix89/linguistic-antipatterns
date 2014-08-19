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
		HashMap<String, Boolean> cleanedComment = null;
		
		for (Iterator<CodeComment> iterator = commentsSet.iterator(); iterator.hasNext();) {
			CodeComment cc = (CodeComment) iterator.next();
			comment = comment.concat(cc.getComment());
		}
		
		/*
		 * Se non ci sono commenti - ovviamente - non può esserci nessun anti-pattern relativo 
		 * ai commenti.
		 */
		if(comment.isEmpty())
			return false;
			
		cleanedComment = MainWordsManipulation.cleanComment(comment);
		
		if(checkAntonym1(name, cleanedComment))
			return true;
		
		return false;
	}
	
	/**
	 * Controlla se il nome del metodo o della variabile contiene parole che corrispondono 
	 * al contrario di una delle parole estrapolate -rispettivamente - dal nome del tipo di 
	 * ritorno o della variabile. 
	 * @param typeName Il nome del metodo o della variabile.
	 * @param typeName Il nome del tipo di ritorno o della variabile. 
	 * @return <b>true</b> se <b>name</b> contiene almeno un contrario di una delle parole 
	 * estrapolate da <b>typeName</b>, <b>false</b> altrimenti.
	 */
	public static boolean checkTypeAntonym(String name, String typeName) {
		boolean underscoreFlag = false, upperCaseFlag = false;
		String tmp = "";
		int i = 0, c = 0, sugSize = 0, typeNameLen = 0;
		//Conterrà tutte le possibili parole di senso compiuto, associate al nome del tipo.  
		HashMap<String, Boolean> typeNameWords = new HashMap<String, Boolean>(); 
		List<String> sugList = null;
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = null;
		
		if(typeName.contains("_")) {
			underscoreFlag = true;
			
			for(String str : typeName.split("_")) {
				synsets = database.getSynsets(tmp);
				if(synsets.length > 0)
					typeNameWords.put(str, false);
				else {
					sugList = 
							MainWordsManipulation.cleanList(MainWordsManipulation.wordSuggestion(str));
					sugSize = sugList.size(); 
					if(sugSize > 0) {
						for (int j = 0; j < sugSize; j++) {
							typeNameWords.put(sugList.get(j), false);
						}
					}
				}
			}
		}
		
		typeNameLen = typeName.length();
		if(underscoreFlag) {
			i = 0;
			/*
			 * Memorizzo ciascuna parola che precede una lettera maiuscola (ad esempio, nel caso in 
			 * cui name sia uguale a aSimpleExample, memorizzo le stringhe "a", "Simple" e "Example").  
			 */
			while(i < typeNameLen) {
				while(i < typeNameLen && 
						(Character.isLowerCase(typeName.charAt(i)) ||
								Character.isDigit(typeName.charAt(i)))) {
					i++;
				}
				
				if(i < typeNameLen && Character.isUpperCase(typeName.charAt(i))) {
					upperCaseFlag = true;
					if(i > 0) {
						tmp = typeName.substring(c, i);
						
						database = WordNetDatabase.getFileInstance();
						synsets = database.getSynsets(tmp);
						
						/*
						 * Se tmp contiene una parola di senso compiuto, la memorizzo; altrimenti 
						 * memorizzo tutte le parole più probabili, ottenibili a partire da tmp.   
						 */
						if(synsets.length > 0) {
							typeNameWords.put(tmp, false);
						} else {
							for(CompleteSuggestion s : MainWordsManipulation.wordSuggestion(tmp)) {
								typeNameWords.put(s.toString(), false);
							}
						}
					}
					c = i;
					i++;
				}
			}
			
			/*
			 * Se in typeName non sono contenute lettere maiuscole (e, quindi, non è stata ancora 
			 * analizzata la stringa contenuta in name), procedo con il memorizzare ciascuna parola 
			 * estrapolata da tutte le sotto-stringhe, di lunghezza maggiore di uno, ottenibili 
			 * da typeName. 
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
				if(MainWordsManipulation.getSynonyms(typeName).size() > 0) {
					typeNameWords.put(typeName, false);
				} else {
					for(i = 0; i < typeNameLen; i++) {
						for (int j = typeNameLen; j > i; j--) {
							if(j - i > 1) {
								tmp = typeName.substring(i, j);
								typeNameWords.put(tmp, false);
							}
						}
					}
				}
			}
			
			if(checkAntonym1(name, typeNameWords))
				return true;
		} else {
			if(checkAntonym1(name, typeNameWords))
				return true;
		}
		return false;
	}
	
	private static boolean checkAntonym1(String str, HashMap<String, Boolean> words) {
		boolean upperCaseFlag = false;
		String tmp = "";
		int i = 0, c = 0, nameLen = 0; 
		WordNetDatabase database = null;
		Synset[] synsets = null;
		
		/*
		 * Se il nome contiene underscore estraggo ciascuna parola separata da tale carattere,
		 * quindi controllo (rispetto a ciascuna di tali parole) se sono contenuti dei contrari 
		 * delle parole contenute nei commenti.
		 */
		if(str.contains("_")) {
			for(String s : str.split("_")) {
				/*
				 * Se str non contiene una parola di senso compiuto, il seguente metodo
				 * restituisce una lista vuota.  
				 */
				List<String> ant = MainWordsManipulation.getAntonyms(s);
				int antSize = ant.size();
				for(i = 0; i < antSize; i++) {
					if(ant.size() > 0 && words.containsKey(ant.get(i))) {
						System.out.println("1. Does the opposite");
						return true;
					}
				}
			}
			/*
			 * Elimino tutti gli underscore, per evitare problemi col ciclo successivo.
			 */
			str = str.replace("_", "");
		}
		nameLen = str.length();
		
		/*
		 * Nel caso in cui la prima lettere del nome sia una maiuscola, la converto in minuscola,
		 * al fine di evitare problemi col ciclo successivo.
		 */
		if(Character.isUpperCase(str.charAt(0)))
			str = str.replaceFirst("" + str.charAt(0), "" + Character.toLowerCase(str.charAt(0)));
		
		i = 0;
		/*
		 * Estraggo ciascuna parola che precede una lettera maiuscola (ad esempio, nel caso in 
		 * cui name sia uguale a aSimpleExample, estraggo le stringhe "a", "Simple" e "Example"), 
		 * quindi, per ciascuna di tali parole, controllo se corrisponde ad uno dei contrari.  
		 */
		while(i < nameLen) {
			while(i < nameLen && 
					(Character.isLowerCase(str.charAt(i)) ||
							Character.isDigit(str.charAt(i)))) {
				i++;
			}
			
			if(i < nameLen && Character.isUpperCase(str.charAt(i))) {
				upperCaseFlag = true;
				if(i > 0) {
					tmp = str.substring(c, i);
					
					database = WordNetDatabase.getFileInstance();
					synsets = database.getSynsets(tmp);
					
					/*
					 * Se tmp contiene una parola di senso compiuto, cerco direttamente per tale
					 * parola la lista dei contrari; altrimenti cerco fra tutte la lista delle
					 * parole più probabili, ottenibili a partire da tmp, se vi è almeno un 
					 * contrario.   
					 */
					if(synsets.length > 0) {
						if(MainWordsManipulation.checkAntonym(words, tmp)) {
							System.out.println("2. Does the opposite");
							return true;
						}
					} else {
						if(checkAntonym(tmp, words))
							System.out.println("3. Does the opposite\n");
					}
				}
				c = i;
				i++;
			}
		}
		
		/*
		 * Se in name non sono contenute lettere maiuscole (e, quindi, non è stata ancora 
		 * analizzata la stringa contenuta in name), procedo con l'analizzare ciascuna parola 
		 * estrapolata da tutte le sotto-stringhe, di lunghezza maggiore di uno, ottenibili 
		 * da name. 
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
			if(MainWordsManipulation.getSynonyms(str).size() > 0) {
				if(MainWordsManipulation.checkAntonym(words, tmp)) {
					System.out.println("4. Does the opposite");
					return true;
				}
			} else {
				for(i = 0; i < nameLen; i++) {
					for (int j = nameLen; j > i; j--) {
						if(j - i > 1) {
							tmp = str.substring(i, j);
							if(checkAntonym(tmp, words))
								System.out.println("5. Does the opposite\n");
						}
					}
				}
			}
		}
		return false;
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
	private static boolean checkAntonym(String str, HashMap<String, Boolean> words) {
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
