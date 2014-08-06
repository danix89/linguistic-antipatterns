package linguisticAntipatterns.methods;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(code);
			
			if (matcher.find()) {
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
		} else if(methodName.toLowerCase().startsWith("is")) {
			if(!methodReturnType.equalsIgnoreCase("boolean") && !methodReturnType.equalsIgnoreCase("void")) {
//					System.out.println("\tIl metodo " + methodName 
//							+ " dovrebbe restituire un oggetto o un valore booleano,"
//							+ " invece il tipo di ritorno è " + methodReturnType + ".");
				doesMoreThanSays = true;
			}
		} else if(methodName.toLowerCase().startsWith("set")) {
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
			Pattern pattern = Pattern.compile(collectionRegex);
			Matcher matcher = pattern.matcher(methodName);
			
			/*
			 * Il metodo deve terminare per 's' (i.e. plurale) o, comunque, deve 
			 * contenere un termine che suggerisca, come tipo di ritorno, una lista
			 * di oggetti. 
			 */
			if(!methodName.endsWith("s") && !matcher.find()) {
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
	 */
	public static boolean doesTheOpposite(Method mb) {

		return false;
	}

	private static final String collectionRegex = "list | collection | map | table"; //da completare con altri nomi che suggeriscano una collezione di oggetti
}
