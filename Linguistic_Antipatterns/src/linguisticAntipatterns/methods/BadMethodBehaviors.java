package linguisticAntipatterns.methods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sie.db.entity.CodeComment;
import sie.db.entity.Method;
import sie.db.entity.SType;
import sie.db.entity.Variable;

/**
 * Questa classe offre i metodi per trovare eventuali anti-paterns linguistici legati ad i metodi di 
 * una classe.
 * @author Daniele Iannone
 *
 */
public class BadMethodBehaviors {
	
	/**
	 * Controlla che il metodo analizzato non faccia pi� di quello che dice, ossia controlla che:
	 * <ol>
	 * <li>Un metodo <b>getSomething</b> non faccia altro che restituire la variabile d'istanza 
	 * <b>something</b>;</li>
	 * <li>Un metodo <b>setSomething</b> non faccia altro che settare la variabile d'istanza 
	 * <b>something</b> col valore specificato;</li>
	 * <li>Un metodo <b>isSomething</b> non faccia altro che restituire <b>true</b> o <b>false</b>
	 * se la condizione <b>something</b> viene rispettata.</li>
	 * </ol>
	 * @param mb Un oggetto {@link Method} contenente il metodo da esaminare.
	 * @return <b>true</b> se il metodo fa pi� di quel che dice, <b>false</b> altrimenti.
	 */
	public static boolean doesMoreThanItSays(Method mb) {
		boolean doesMoreThanSays = false;
		String methodName = mb.getName();
		String methodReturnType = mb.getReturnType().getName();
		
		/*
		 * Se il nome del metodo inizia con "get", controllo che la funzione di tale metodo sia solo quella
		 * di restituire un valore; se comincia con "is", invece, controllo che il valore restituito sia un booleano; 
		 * se invece comincia con set, controllo che il metodo non restituisca alcun valore.  
		 */
		
		if(methodName.toLowerCase().contains("get")) {
			String code = mb.getTextContent();
			
			/*
			 * Controllo che non venga instanziato nessun nuovo oggetto
			 * nel metodo get. 
			 */
			String regex = "new";
			
			if (checkPattern(regex, code, false)) {
//					System.out.println("\tNel metodo " + methodName 
//							+ " vengono allocati uno o pi� oggetti: si consiglia di "
//							+ "rinominare il metodo in una maniera differente da \"getSomething\".");
				doesMoreThanSays = true;
			}

			/*
			 * Controllo che il metodo restituisca effettivamente qualcosa.
			 * In caso affermativo, vado a prendermi la parte del codice in cui 
			 * � contenuta l'istruzione return, quindi controllo che nel nome del
			 * metodo sia contenuto un riferimento al nome dell'oggetto. 
			 */
			if (!methodReturnType.equals("void")) {
				/*
				 * Procedo con il controllo solo se le righe di codice sono pi� di una 
				 * (i.e. in caso contrario, la probabilt� che una variabile locele sia 
				 * stata allocata � pari a 0).
				 */
				if(mb.getLinesCount() > 1) {
					/* Controllo che con return venga restituito effettivamente 
					 * quello che dice il metodo. 
					 * N.B.: l'istruzione return pu� estendersi su pi� righe.
					 */
					String[] C = code.split("return");
					int len = C.length;
					C = C[len-1].split(";");
					String return_object = C[0].trim();
					
					/*
					 * Controllo che l'oggetto restituito sia un variabile di classe 
					 * e non una variabile locale.
					 */
					Variable v = null;
					boolean isLocalVar = false;
					Set<Variable> locVars = mb.getLocalVariables();
					for(Iterator<Variable> VI = locVars.iterator(); VI.hasNext(); ) {
						v = VI.next();
						if(v.getName().equalsIgnoreCase(return_object)) {
							doesMoreThanSays = true;
							isLocalVar = true;
							break;
						}
					}
					if(isLocalVar) {
	//						System.out.println("\tL'oggetto " + return_object + " restituito dal metodo " + methodName 
	//								+ " � una variabile locale: si consiglia di "
	//							+ "rinominare il metodo in una maniera differente da \"getSomething\".");
						doesMoreThanSays = true;
					}
				}
			} 
		} else if(methodName.toLowerCase().startsWith("is")) {
			if(!methodReturnType.equalsIgnoreCase("boolean") && !methodReturnType.equalsIgnoreCase("void")) {
//					System.out.println("\tIl metodo " + methodName 
//							+ " dovrebbe restituire un oggetto o un valore booleano,"
//							+ " invece il tipo di ritorno � " + methodReturnType + ".");
				doesMoreThanSays = true;
			}
		} else if(methodName.toLowerCase().startsWith("set")) {
			if(!methodReturnType.equalsIgnoreCase("void")) {
//					System.out.println("\tIl metodo " + methodName 
//							+ " non dovrebbe restituire alcun oggetto o valore,"
//							+ " invece il tipo di ritorno � " + methodReturnType + ".");
				doesMoreThanSays = true;
			}
		}
		
		
		/*
		 * Se il metodo restituisce una lista di oggetti, controllo se il nome del 
		 * metodo suggerisce che venga restituita una lista di oggetti, o un unico oggetto.
		 */
		Set<SType> superClasses = mb.getReturnType().getSuperclasses();
		if(superClasses != null && (superClasses.contains("Collection")
				|| superClasses.contains("Map") || superClasses.contains("Arrays"))) {
			
			/*
			 * Il metodo deve terminare per 's' (i.e. plurale) o, comunque, deve 
			 * contenere un termine che suggerisca, come tipo di ritorno, una lista
			 * di oggetti. 
			 */
			if(!methodName.endsWith("s") && !checkPattern(collectionRegex, methodName, false)) {
//					System.out.println("\tIl nome del metodo " + methodName 
//							+ " suggerisce che debba essere restituito un unico oggetto, mentre il "
//							+ "tipo di ritorno � una lista di oggetti.");
				doesMoreThanSays = true;
			}
		} 
		
		
//		System.out.println("Does more than it says = " + doesMoreThanSays);

		return doesMoreThanSays;
	}

	/**
	 * Controlla che il metodo analizzato non dica pi� di quello che fa, ossia controlla che:
	 * <ol>
	 * <li>La condizione specificata nei commenti del metodo sia implementata;</li>
	 * <li>Un metodo <b>checkSomething</b> restituisca un valore booleano, oppure lanci un'eccezione 
	 * qualora, nel controllo della condizione <b>something</b>, si verifichi un'anomalia;</li>
	 * <li>Un metodo <b>getSomething</b> restituisca qualcosa;</li>
	 * <li>Un metodo <b>isSomething</b> restituisca un valore booleano;</li>
	 * <li>Un metodo di trasformazione (e.g., <b>atoi</b>, <b>itoa</b>) restituisca qualcosa;</li>
	 * <li>Un metodo, il cui nome suggerisce che venga restituita una lista di oggetti,
	 * non restituisca un singolo oggetto oppure niente.</li>
	 * </ol>
	 * @param mb Un oggetto {@link Method} contenente il metodo da esaminare.
	 * @return <b>true</b> se il metodo dice pi� di quel che fa, <b>false</b> altrimenti.
	 */
	public static boolean saysMoreThanItDoes(Method mb) {
		boolean saysMoreThanItDoes = false;
		String methodName = mb.getName().toLowerCase();
		String methodReturnType = mb.getReturnType().getName();
		
		/*
		 * Controllo che, nel caso in cui nel commento venga menzionato il controllo di una 
		 * condizione, la condizione stessa venga effettivamente implementata tramite if, switch, 
		 * ecc...
		 */
		for(CodeComment comment : mb.getComments()) {
			ArrayList<String> comRegexsList = new ArrayList<String>();
			ArrayList<String> codeRegexsList = new ArrayList<String>();
			
			comRegexsList.add("if");
			comRegexsList.add("se");
			comRegexsList.add("else");
			comRegexsList.add("altrimenti");
			
			codeRegexsList.add("if");
			codeRegexsList.add("switch");
			
			if(checkPattern(comRegexsList, comment.getComment(), true)) {
//				System.out.println("\t\t" + comment.getComment());
				if(!checkPattern(codeRegexsList, mb.getTextContent(), false)) {
					saysMoreThanItDoes = true;
					break;
				}
			}
			
			/*
			 * Per controllare se la particolare condizione citata nel commento (nel caso seguente 
			 * il while) sia stata implementata, le seguenti linee di codice sono indispensabili 
			 * (i.e., per ciascuna istruzione java � necessario effettuare un controllo per volta,
			 * altrimenti potrebbe esservi un match anche nel caso in cui nel commento si facesse
			 * riferimento ad un while mentre viene implementato un if). 
			 */
			comRegexsList.clear();
			codeRegexsList.clear();
			
			comRegexsList.add("while");
			comRegexsList.add("finche'");
			
			codeRegexsList.add("while");
			
			if(checkPattern(comRegexsList, comment.getComment(), true)) {
//				System.out.println("\t\t" + comment.getComment());
				if(!checkPattern(codeRegexsList, mb.getTextContent(), false)) {
					saysMoreThanItDoes = true;
					break;
				}
			} 
		}
		
		/*
		 * Se il nome del metodo inizia con "check", controllo che il tipo restituito sia boolean, o 
		 * che venga lanciata almeno un'eccezione; se comincia con "get", controllo che restituisca 
		 * qualcosa e, se il nome dopo "get" suggerisce che venga restituita una lista di oggetti,
		 * che venga restituita effettivamente una lista di oggetti; se invece comincia con "is",
		 * controllo che venga restituito un boolean; infine, se il nome contiene la parola "to"
		 * (n� all'inizio n� alla fine della parola stessa), controllo che venga restituito qualcosa. 
		 *  
		 */
		if(methodName.startsWith("check")) {
			if(!methodReturnType.equalsIgnoreCase("boolean") && mb.getThrowedException().isEmpty())
				saysMoreThanItDoes = true;
		} else if(methodName.startsWith("get")) {
			if(methodReturnType.equals("void"))
				saysMoreThanItDoes = true;
			else {
				/*
				 * Se il metodo termina per 's' (i.e. plurale) o, comunque, 
				 * contiene un termine che suggerisce, come tipo di ritorno, una lista
				 * di oggetti, controllo che restituisca effettivamente una collezione di oggetti.
				 */
				if(!methodName.endsWith("s") && !checkPattern(collectionRegex, methodName, false)) {
					Set<SType> superClasses = mb.getReturnType().getSuperclasses();
					if(superClasses != null && (superClasses.contains("Collection")
							|| superClasses.contains("Map") || superClasses.contains("Arrays"))) {
						saysMoreThanItDoes = true;
					}
				} 
			}
		} else if(methodName.startsWith("is")) {
			if(!methodReturnType.equalsIgnoreCase("boolean"))
				saysMoreThanItDoes = true;
		} else if(methodName.contains("to") &&
				(!methodName.startsWith("to") && !methodName.endsWith("to"))) {
			if(methodReturnType.equals("void"))
				saysMoreThanItDoes = true;
		}
		
		return saysMoreThanItDoes;
	}

	/**
	 * Controlla che il metodo analizzato non faccia l'opposto di quello che dice, ossia controlla che:
	 * <ol>
	 * <li>L'intento del metodo, suggerito dal suo nome, � in contrapposizione con ci� che 
	 * restituisce;</li>
	 * <li>La documentazione di un metodo non sia in contrapposizione con la sua dichiarazione 
	 * (e.g., nome, tipo di ritorno).</li>
	 * </ol>
	 * @param mb Un oggetto {@link Method} contenente il metodo da esaminare.
	 * @return <b>true</b> se il metodo fa l'opposto di quello che dice, <b>false</b> altrimenti.
	 */
	public static boolean doesTheOpposite(Method mb) {

		return false;
	}
	
	/**
	 * Controlla se la stringa <b>str</b> contiene almeno una espressione regolare contenuta in 
	 * <b>regexsList</b>. Se <b>wholeWord</b> � <b>true</b>, in <b>str</b>, viene effettuata 
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
	private static boolean checkPattern(List<String> regexsList, String str, boolean wholeWord) {
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
	 * Se <b>wholeWord</b> � <b>true</b>, in <b>str</b>, viene effettuata 
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
	private static boolean checkPattern(String regex, String str, boolean wholeWord) {
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
	
	private static final String collectionRegex = "list | collection | map | table"; //da completare con altri nomi che suggeriscano una collezione di oggetti
}