package linguisticAntipatterns.methods;

import sie.db.entity.Field;

public class BadAttributeBehaviors {
	
	/**
	 * Controlla che l'attributo analizzato non contenga più di quello che dice, ossia controlla che:
	 * <ol>
	 * <li>Un attributo, il cui nome suggerisce che contenga una singola istanza,
	 * non contenga invece una lista di oggetti;</li>
	 * <li>Un attributo <b>isSomething</b> contenga un valore booleano.</li>
	 * </ol>
	 * @param f Un oggetto {@link Field} contenente l'attributo da esaminare.
	 * @return <b>true</b> se il metodo contiene l'opposto di quello che dice, <b>false</b> altrimenti.
	 */
	public static boolean containsMoreThanItSays(Field f) {
		
		return false;
	}
	
	/**
	 * Controlla che l'attributo analizzato non dica più di quello che contiene, ossia controlla che
	 * un attributo, il cui nome suggerisce che contenga istanze multiple, non contenga invece una 
	 * singola istanza oppure niente.
	 * @param f Un oggetto {@link Field} contenente l'attributo da esaminare.
	 * @return <b>true</b> se il metodo dice l'opposto di quello che contiene, <b>false</b> altrimenti.
	 */
	public static boolean saysMoreThanItContains(Field f) {
		
		return false;
	}

	/**
	 * Controlla che l'attributo analizzato non contenga l'opposto di quello che dice, ossia controlla 
	 * che:
	 * <ol>
	 * <li>Il nome dell'attributo non sia in contrapposizione con il suo tipo;</li>
	 * <li>La documentazione di un attributo non sia in contrapposizione con la sua dichiarazione 
	 * (e.g., nome, tipo di ritorno).</li>
	 * </ol>
	 * @param f Un oggetto {@link Field} contenente l'attributo da esaminare.
	 * @return <b>true</b> se l'attributo contiene l'opposto di quello che dice, <b>false</b>
	 * altrimenti.
	 */
	public static boolean containsTheOpposite(Field f) {

		return false;
	}
	
}
