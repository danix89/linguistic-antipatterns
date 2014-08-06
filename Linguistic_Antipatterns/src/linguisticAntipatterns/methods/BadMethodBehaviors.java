package linguisticAntipatterns.methods;

import sie.db.entity.Method;

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

		return false;
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

		return false;
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
	
}
