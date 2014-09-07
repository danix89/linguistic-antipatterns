package linguisticAntipatterns.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import linguisticAntipatterns.methods.BadAttributeBehaviors;

import org.junit.Test;

import sie.db.entity.Field;
import sie.db.entity.SType;


public class ContainsMoreThanItSaysTest {
	
	@Test
	public void testContainsMoreThanItSays() {
		
		SType abcol=new SType();
		abcol.setName("AbstractCollection");
		
		SType ablist=new SType();
		ablist.setName("AbstractList");
		
		SType cvector=new SType();
		cvector.setName("Vector");
		Set<SType> setlist = new HashSet<SType>();
		setlist.add(abcol);
		setlist.add(ablist);
		cvector.setSuperclasses(setlist);
		
		SType ccoll=new SType();
		ccoll.setName("Collection");
		
		SType cbool=new SType();
		cbool.setName("boolean");
		
		
		SType carray=new SType();
		carray.setName("Array");
		
		/**
		 * TC 4.1.01
		 */
		
		Field a1=new Field("_target");
		a1.setType(cvector);
		assertEquals(BadAttributeBehaviors.containsMoreThanItSays(a1),true);
		
		/**
		 * TC 4.1.02
		 */
		
		Field a2=new Field("element");
		a2.setType(ccoll);
		assertEquals(BadAttributeBehaviors.containsMoreThanItSays(a2),true);
		
		/**
		 * TC 4.1.03
		 */
		
		Field a3=new Field("elements");
		a3.setType(ccoll);
		assertEquals(BadAttributeBehaviors.containsMoreThanItSays(a3),false);
		
		/**
		 * TC 4.1.04
		 */
		Field a4=new Field("isValid");
		a4.setType(cbool);
		assertEquals(BadAttributeBehaviors.containsMoreThanItSays(a4),false);
		
		/** 
		 * TC 4.1.05
		 */
		
		Field a5=new Field("isValid");
		a5.setType(cvector);
		assertEquals(BadAttributeBehaviors.containsMoreThanItSays(a5),true);
		
		/**
		 * TC 4.1.06
		 */
		
		Field a6=new Field("sequenceElements");
		a6.setType(carray);
		assertEquals(BadAttributeBehaviors.containsMoreThanItSays(a6),false);
		
		
		
		
		
		
		
		
		
		
	}

}
