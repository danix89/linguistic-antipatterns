package linguisticAntipatterns.main;

import java.util.Iterator;
import java.util.Set;

import linguisticAntipatterns.methods.BadAttributeBehaviors;
import linguisticAntipatterns.methods.BadMethodBehaviors;
import sie.db.entity.Field;
import sie.db.entity.Method;
import sie.db.entity.SType;

/**
 * Classe contenente il metodo principale per il controllo sulla metrica legata agli 
 * anti-paterns linguistici.
 * @author Daniele Iannone
 *
 */
public class MainLAS {
	
	/**
	 * Scorre la lista delle classi <b>listClasses</b>, quindi, per ogni metodo e attributo trovato 
	 * in ciascuna classe, chiama i metodi delle classi {@link BadMethodBehaviors} e 
	 * {@link BadAttributeBehaviors}, che si occupano di trovare gli anti-patterns linguistici legati 
	 * rispettivamente ai metodi ed agli attribuiti di una classe.
	 * @param listClasses Un oggetto {@link Set&lt;SType&gt;} contenente la lista delle classi 
	 * (con i rispettivi contenuti) estratte durante il parsing del progetto.
	 * @return <b>true</b> se viene trovato almeno un anti-pattern linguistico, 
	 * <b>false</b> altrimenti.
	 */
	public static boolean getLAS(Set<SType> listClasses) {
		long start = System.currentTimeMillis();
		Set<Method> Methods = null;
		boolean LASFlag = false;
		
		System.out.println("");
		
		for(Iterator<SType> MI = listClasses.iterator(); MI.hasNext(); ) {
			Methods = MI.next().getMethods();
			for(Method mb : Methods) {
				System.out.println("Method name: " + mb.getBelongingClass() 
						+ "." + mb.getName());
				if(BadMethodBehaviors.doesMoreThanItSays(mb)) {
					LASFlag = true;
					System.err.println("Does More Than It Says");
				}
				if(BadMethodBehaviors.saysMoreThanItDoes(mb)) {
					LASFlag = true;
					System.err.println("Says More Than It Does");
				}
				if(BadMethodBehaviors.doesTheOpposite(mb)) {
					LASFlag = true;
					System.err.println("Does The Opposite");
				}
			}
		}

		Set<Field> Fields = null;

		for(Iterator<SType> FI = listClasses.iterator(); FI.hasNext(); ) {
			Fields = FI.next().getInstanceVariables();
			for(Field f : Fields) {
				if(BadAttributeBehaviors.containsMoreThanItSays(f)) {
					LASFlag = true;
					System.err.println("Contains More Than It Says");
				}
				if(BadAttributeBehaviors.saysMoreThanItContains(f)) {
					LASFlag = true;
					System.err.println("Says More Than It Contains");
				}
				if(BadAttributeBehaviors.containsTheOpposite(f)) {
					LASFlag = true;
					System.err.println("Contains The Opposite");
				}
			}
		}
		
		long end = System.currentTimeMillis();
		System.out.println("Linguistic AntiPatterns detection took: " + ((end - start) / 1000) + "s");
		
		return LASFlag;
	}

}
