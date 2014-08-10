package linguisticAntipatterns.wordsManipulation.xml;

/**
 * Classe bean per il parsing del file XML scaricato in seguito all'esecuzione della query al
 * server Google.
 * @author Daniele Iannone
 *
 */
public class CompleteSuggestion {
	String suggestion ;

	@Override
	public String toString() {
		return suggestion;
	}
}
