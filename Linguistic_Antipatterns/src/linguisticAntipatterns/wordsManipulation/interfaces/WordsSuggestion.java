package linguisticAntipatterns.wordsManipulation.interfaces;

import java.util.List;

import linguisticAntipatterns.wordsManipulation.xml.CompleteSuggestion;

/**
 * Interfaccia per le classi che implementano gli algoritmi di ricerca di una lista di parole,
 * dato in input un insieme di caratteri.
 * @author Daniele Iannone
 *
 */
public interface WordsSuggestion {
	/**
	 * Deve restituire una lista di suggerimenti per un dato insieme di caratteri. 
	 * Ad esempio, se al metodo viene dato in input l'insieme di caratteri "bo", il metodo 
	 * dovrebbe restituire la seguente lista di parole:
	 * <ol>
	 * <li>booking</li>
	 * <li>bonprix</li>
	 * <li>bologna</li>
	 * @param str Una stringa che rappresenta l'insieme di caratteri rispetto ad i quali
	 * trovare una serie di suggerimenti.
	 * @return La lista di parole associate all'insieme di caratteri <b>cset</b>. Nel caso 
	 * non ci siano suggerimenti, la lista sarà vuota.
	 * @throws Exception
	 */
	public List<CompleteSuggestion> wordSuggestion(String str) throws Exception;
}
