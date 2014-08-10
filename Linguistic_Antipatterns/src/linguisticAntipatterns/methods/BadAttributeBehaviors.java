package linguisticAntipatterns.methods;

import java.util.Set;

import linguisticAntipatterns.wordsManipulation.MainWordsManipulation;
import sie.db.entity.Field;
import sie.db.entity.SType;

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
		boolean containsMoreThanItSays = false;
		String attributeName = f.getName().toLowerCase();
		String attributeType = f.getType().getName();

		/*
		 * Se il tipo di un attributo è una lista di oggetti, controllo che il nome 
		 * dell'attributo suggerisca che contiene una collezione di oggetti, o un unico oggetto.
		 */
		Set<SType> superClasses = f.getType().getSuperclasses();
		if(superClasses != null && (superClasses.contains("Collection")
				|| superClasses.contains("Map") || superClasses.contains("Arrays"))) {
			
			/*
			 * Il nome dell'attributo deve terminare per 's' (i.e. plurale) o, comunque, deve 
			 * contenere un termine che suggerisca una lista di oggetti. 
			 */
			if(!attributeName.endsWith("s") && 
					!MainWordsManipulation.checkPattern(MainWordsManipulation.collectionRegex, attributeName, false)) {
				containsMoreThanItSays = true;
			}
		} 

		if(attributeName.startsWith("is") && !attributeType.equalsIgnoreCase("boolean"))
			containsMoreThanItSays = true;

		return containsMoreThanItSays;
	}

	/**
	 * Controlla che l'attributo analizzato non dica più di quello che contiene, ossia controlla che
	 * un attributo, il cui nome suggerisce che contenga istanze multiple, non contenga invece una 
	 * singola istanza oppure niente.
	 * @param f Un oggetto {@link Field} contenente l'attributo da esaminare.
	 * @return <b>true</b> se il metodo dice l'opposto di quello che contiene, <b>false</b> altrimenti.
	 */
	public static boolean saysMoreThanItContains(Field f) {
		boolean saysMoreThanItContains = false;
		String attributeName = f.getName().toLowerCase();

		/*
		 * Se il nome dell'attributo termina per 's' (i.e. plurale) o, comunque, 
		 * contiene un termine che suggerisce una lista
		 * di oggetti, controllo che contenga effettivamente una collezione di oggetti.
		 */
		if(!attributeName.endsWith("s") && 
				!MainWordsManipulation.checkPattern(MainWordsManipulation.collectionRegex, attributeName, false)) {
			Set<SType> superClasses = f.getType().getSuperclasses();
			if(superClasses != null && (superClasses.contains("Collection")
					|| superClasses.contains("Map") || superClasses.contains("Arrays"))) {
				saysMoreThanItContains = true;
			}
		} 

		return saysMoreThanItContains;
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
