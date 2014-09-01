package linguisticAntipatterns.main;

import java.util.Iterator;

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
	 * @param cb Un oggetto {@link SType} contenente metodi e variabili da analizzare, estratti
	 * durante il parsing del progetto.
	 * @return <b>true</b> se viene trovato almeno un anti-pattern linguistico, 
	 * <b>false</b> altrimenti.
	 */
	public static boolean getLAS(SType cb) {
		long start = System.currentTimeMillis();
		int methodCnt = 0, fieldCnt = 0;
		Method method = null;
		Field field = null;
		boolean LASFlag = false;
		
		System.out.println("_______________________________________________________");
		System.out.println("Class name: " + cb.getBelongingPackage().getName() 
				+ "." + cb.getName());
		
		for(Iterator<Method> MI = cb.getMethods().iterator(); MI.hasNext(); ) {
			method = MI.next();
			methodCnt++;
			System.out.println(methodCnt + ". Method name: " + method.getName());
			
			if(BadMethodBehaviors.doesMoreThanItSays(method)) {
				LASFlag = true;
				System.out.println("\tDoes More Than It Says");
			}
			if(BadMethodBehaviors.saysMoreThanItDoes(method)) {
				LASFlag = true;
				System.out.println("\tSays More Than It Does");
			}
			if(BadMethodBehaviors.doesTheOpposite(method)) {
				LASFlag = true;
				System.out.println("\tDoes The Opposite");
			}
		}
		System.out.println();
				
		for(Iterator<Field> FI = cb.getInstanceVariables().iterator(); FI.hasNext(); ) {
			field = FI.next();
			fieldCnt++;
			System.out.println(fieldCnt + ". Field name: " + field.getName());
			
			if(BadAttributeBehaviors.containsMoreThanItSays(field)) {
				LASFlag = true;
				System.err.println("Contains More Than It Says");
			}
			if(BadAttributeBehaviors.saysMoreThanItContains(field)) {
				LASFlag = true;
				System.err.println("Says More Than It Contains");
			}
			if(BadAttributeBehaviors.containsTheOpposite(field)) {
				LASFlag = true;
				System.err.println("Contains The Opposite");
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Linguistic AntiPatterns detection took: " + ((end - start) / 1000) + "s");
		System.out.println("_______________________________________________________");
		
		return LASFlag;
	}

}
