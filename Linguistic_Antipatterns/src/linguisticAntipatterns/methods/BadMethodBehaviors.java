package linguisticAntipatterns.methods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import linguisticAntipatterns.wordsManipulation.MainWordsManipulation;
import sie.db.entity.CodeComment;
import sie.db.entity.Method;
import sie.db.entity.SType;
import sie.db.entity.Variable;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 * Questa classe offre i metodi per trovare eventuali anti-paterns linguistici legati ad i 
 * metodi di una classe.
 * @author Daniele Iannone
 *
 */
public class BadMethodBehaviors {
	
	/**
	 * Controlla che il metodo analizzato non faccia più di quello che dice, ossia controlla che:
	 * <ol>
	 * <li>Un metodo <b>getSomething</b> non faccia altro che restituire la variabile d'istanza 
	 * <b>something</b>;</li>
	 * <li>Un metodo <b>setSomething</b> non faccia altro che settare la variabile d'istanza 
	 * <b>something</b> col valore specificato;</li>
	 * <li>Un metodo <b>isSomething</b> non faccia altro che restituire <b>true</b> o <b>false</b>
	 * se la condizione <b>something</b> viene rispettata.</li>
	 * </ol>
	 * @param mb Un oggetto {@link Method} contenente il metodo da esaminare.
	 * @return <b>true</b> se il metodo fa più di quel che dice, <b>false</b> altrimenti.
	 */
	public static boolean doesMoreThanItSays(Method mb) {
		boolean doesMoreThanSays = false;
		String methodName = extractMethodName(mb.getName());
		String methodNameLowerCase = methodName.toLowerCase();
		String methodReturnType = mb.getReturnType().getName();
		
		/*
		 * Se il nome del metodo inizia con "get", controllo che la funzione di tale metodo sia solo quella
		 * di restituire un valore; se comincia con "is", invece, controllo che il valore restituito sia un booleano; 
		 * se invece comincia con set, controllo che il metodo non restituisca alcun valore.  
		 */
		
		if(methodNameLowerCase.contains("get")) {
			String code = mb.getTextContent();
			
			/*
			 * Controllo che non venga instanziato nessun nuovo oggetto
			 * nel metodo get. 
			 */
			String regex = "new";
			
			if (MainWordsManipulation.checkPattern(regex, code, false)) {
//					System.out.println("\tNel metodo " + methodName 
//							+ " vengono allocati uno o più oggetti: si consiglia di "
//							+ "rinominare il metodo in una maniera differente da \"getSomething\".");
				doesMoreThanSays = true;
			}

			/*
			 * Controllo che il metodo restituisca effettivamente qualcosa.
			 * In caso affermativo, vado a prendermi la parte del codice in cui 
			 * è contenuta l'istruzione return, quindi controllo che nel nome del
			 * metodo sia contenuto un riferimento al nome dell'oggetto. 
			 */
			if (!methodReturnType.equals("void")) {
				/*
				 * Procedo con il controllo solo se le righe di codice sono più di una 
				 * (i.e. in caso contrario, la probabiltà che una variabile locele sia 
				 * stata allocata è pari a 0).
				 */
				if(mb.getLinesCount() > 1) {
					/* Controllo che con return venga restituito effettivamente 
					 * quello che dice il metodo. 
					 * N.B.: l'istruzione return può estendersi su più righe.
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
	//								+ " è una variabile locale: si consiglia di "
	//							+ "rinominare il metodo in una maniera differente da \"getSomething\".");
						doesMoreThanSays = true;
					}
				}
			} 
		} else if(methodNameLowerCase.startsWith("is")) {
			if(!methodReturnType.equalsIgnoreCase("boolean") && !methodReturnType.equalsIgnoreCase("void")) {
//					System.out.println("\tIl metodo " + methodName 
//							+ " dovrebbe restituire un oggetto o un valore booleano,"
//							+ " invece il tipo di ritorno è " + methodReturnType + ".");
				doesMoreThanSays = true;
			}
		} else if(methodNameLowerCase.startsWith("set")) {
			if(!methodReturnType.equalsIgnoreCase("void")) {
//					System.out.println("\tIl metodo " + methodName 
//							+ " non dovrebbe restituire alcun oggetto o valore,"
//							+ " invece il tipo di ritorno è " + methodReturnType + ".");
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
			if(!methodName.endsWith("s") && 
					!MainWordsManipulation.checkPattern(MainWordsManipulation.collectionRegex, methodName, false)) {
//					System.out.println("\tIl nome del metodo " + methodName 
//							+ " suggerisce che debba essere restituito un unico oggetto, mentre il "
//							+ "tipo di ritorno è una lista di oggetti.");
				doesMoreThanSays = true;
			}
		} 
		
		
//		System.out.println("Does more than it says = " + doesMoreThanSays);

		return doesMoreThanSays;
	}

	/**
	 * Controlla che il metodo analizzato non dica più di quello che fa, ossia controlla che:
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
	 * @return <b>true</b> se il metodo dice più di quel che fa, <b>false</b> altrimenti.
	 */
	public static boolean saysMoreThanItDoes(Method mb) {
		boolean saysMoreThanItDoes = false;
		String methodName = extractMethodName(mb.getName());
		String methodNameLowerCase = methodName.toLowerCase();
		String methodReturnType = mb.getReturnType().getName();
		String comment = "";
		
		for (Iterator<CodeComment> iterator = mb.getComments().iterator(); iterator.hasNext();) {
			CodeComment cc = (CodeComment) iterator.next();
			comment = comment.concat(cc.getComment());
		}
		
		/*
		 * Controllo che, nel caso in cui nel commento venga menzionato il controllo di una 
		 * condizione, la condizione stessa venga effettivamente implementata tramite if, switch, 
		 * ecc...
		 */
		ArrayList<String> comRegexsList = new ArrayList<String>();
		ArrayList<String> codeRegexsList = new ArrayList<String>();
		
		comRegexsList.add("if");
		comRegexsList.add("se");
		comRegexsList.add("else");
		comRegexsList.add("altrimenti");
		
		codeRegexsList.add("if");
		codeRegexsList.add("switch");
		
		if(MainWordsManipulation.checkPatterns(comRegexsList, comment, true)) {
//				System.out.println("\t\t" + comment.getComment());
			if(!MainWordsManipulation.checkPatterns(codeRegexsList, mb.getTextContent(), false)) {
				saysMoreThanItDoes = true;
			}
		}
		
		/*
		 * Per controllare se la particolare condizione citata nel commento (nel caso seguente 
		 * il while) sia stata implementata, le seguenti linee di codice sono indispensabili 
		 * (i.e., per ciascuna istruzione java è necessario effettuare un controllo per volta,
		 * altrimenti potrebbe esservi un match anche nel caso in cui nel commento si facesse
		 * riferimento ad un while mentre viene implementato un if). 
		 */
		comRegexsList.clear();
		codeRegexsList.clear();
		
		comRegexsList.add("while");
		comRegexsList.add("finche'");
		
		codeRegexsList.add("while");
		
		if(MainWordsManipulation.checkPatterns(comRegexsList, comment, true)) {
//				System.out.println("\t\t" + comment.getComment());
			if(!MainWordsManipulation.checkPatterns(codeRegexsList, mb.getTextContent(), false)) {
				saysMoreThanItDoes = true;
			}
		} 
		
		/*
		 * Se il nome del metodo inizia con "check", controllo che il tipo restituito sia boolean, o 
		 * che venga lanciata almeno un'eccezione; se comincia con "get", controllo che restituisca 
		 * qualcosa e, se il nome dopo "get" suggerisce che venga restituita una lista di oggetti,
		 * che venga restituita effettivamente una lista di oggetti; se invece comincia con "is",
		 * controllo che venga restituito un boolean; infine, se il nome contiene la parola "to"
		 * (nè all'inizio nè alla fine della parola stessa), controllo che venga restituito qualcosa. 
		 *  
		 */
		if(methodNameLowerCase.startsWith("check")) {
			if(!methodReturnType.equalsIgnoreCase("boolean") && mb.getThrowedException().isEmpty())
				saysMoreThanItDoes = true;
		} else if(methodNameLowerCase.startsWith("get")) {
			if(methodReturnType.equals("void"))
				saysMoreThanItDoes = true;
			else {
				/*
				 * Se il metodo termina per 's' (i.e. plurale) o, comunque, 
				 * contiene un termine che suggerisce, come tipo di ritorno, una lista
				 * di oggetti, controllo che restituisca effettivamente una collezione di oggetti.
				 */
				if(!methodName.endsWith("s") && 
						!MainWordsManipulation.checkPattern(MainWordsManipulation.collectionRegex, methodName, false)) {
					Set<SType> superClasses = mb.getReturnType().getSuperclasses();
					if(superClasses != null && (superClasses.contains("Collection")
							|| superClasses.contains("Map") || superClasses.contains("Arrays"))) {
						saysMoreThanItDoes = true;
					}
				} 
			}
		} else if(methodNameLowerCase.startsWith("is")) {
			if(!methodReturnType.equalsIgnoreCase("boolean"))
				saysMoreThanItDoes = true;
		} else if((methodName.contains("to") || methodName.contains("To")) &&
				(!methodNameLowerCase.startsWith("to") && !methodNameLowerCase.endsWith("to"))) {
			if(methodReturnType.equals("void")) {
				String tmp = "";
				int toIndex = methodName.indexOf("to");
				WordNetDatabase database = WordNetDatabase.getFileInstance();
				Synset[] synsets = null;
				for(int i = 0; i < toIndex; i++) {
					for(int j = toIndex + 2; j < methodName.length(); j++) {
						tmp = methodName.substring(i, j);
						synsets = database.getSynsets(tmp);
						if(synsets.length > 0) {
							saysMoreThanItDoes = false;
							break;
						} else
							saysMoreThanItDoes = true;
					}
				}
			}
		}
		
		return saysMoreThanItDoes;
	}

	/**
	 * Controlla che il metodo analizzato non faccia l'opposto di quello che dice, ossia controlla che:
	 * <ol>
	 * <li>L'intento del metodo, suggerito dal suo nome, è in contrapposizione con ciò che 
	 * restituisce;</li>
	 * <li>La documentazione di un metodo non sia in contrapposizione con la sua dichiarazione 
	 * (e.g., nome, tipo di ritorno).</li>
	 * </ol>
	 * @param mb Un oggetto {@link Method} contenente il metodo da esaminare.
	 * @return <b>true</b> se il metodo fa l'opposto di quello che dice, <b>false</b> altrimenti.
	 * @throws Exception 
	 */
	public static boolean doesTheOpposite(Method mb) {
		boolean doesTheOpposite = false;
		String metName = extractMethodName(mb.getName());
		
		if(CommonFeature.checkCommentAntonym(metName, mb.getComments()))
			doesTheOpposite = true;
		
		if(CommonFeature.checkTypeAntonym(metName, mb.getReturnType().getName()))
			doesTheOpposite = true;
		
		return doesTheOpposite;
	}

	/**
	 * Estrae solo il nome del metodo, cancellando tutto ciò che segue la parentesi d'apertura
	 * (parentesi compresa).
	 * @param metName Il nome del metodo ottenuto tramite il metodo {@link Method#getName()}.
	 * @return Solo il nome del metodo.
	 */
	private static String extractMethodName(String metName) {
		int i = 0, metLen = metName.length();
		/*
		 * Estraggo solo il nome del metodo.
		 */
		while(i < metLen && metName .charAt(i) != '(') {
			i++;
		}
//		System.out.println("\tNome metodo prima: " + methodName);
		metName = metName.substring(0, i);
//		System.out.println("\tNome metodo dopo: " + methodName);
		
		return metName;
	}
}
