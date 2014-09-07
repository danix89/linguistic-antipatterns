package linguisticAntipatterns.tests;

import static org.junit.Assert.assertEquals;


import linguisticAntipatterns.methods.BadAttributeBehaviors;

import org.junit.Test;

import sie.db.entity.CodeComment;
import sie.db.entity.Field;
import sie.db.entity.SType;



public class SaysMoreThanItContainsTest {
	
	@Test
	public void testDoesTheOpposite()
	{
		CodeComment cc10 = new CodeComment(); 
		cc10.setComment("/** "+ ""
				+ "*/");
		
		CodeComment cc11 = new CodeComment();
		cc11.setComment("/** "
				+ "enable the object"
				+ "*/");
			
		
		SType cbool=new SType();
		cbool.setName("boolean");
		SType cstr=new SType();
		cstr.setName("String");
		SType ccontrol=new SType();
		ccontrol.setName("close");
		SType cenable=new SType();
		cenable.setName("inactiveState");
		SType ccoll=new SType();
		ccoll.setName("Collection");
		SType cvector=new SType();
		cvector.setName("Vector");
		SType cmap=new SType();
		cmap.setName("Map");
		
		
		/**
		 * TC 6.1.01
		 */		 
		
		Field a1=new Field("elements");
		a1.setType(cstr);
		assertEquals(BadAttributeBehaviors.saysMoreThanItContains(a1),true);
		
		/**
		 * TC 6.1.02
		 */	
		
		Field a2=new Field("elements");
		a2.setType(ccoll);
		assertEquals(BadAttributeBehaviors.saysMoreThanItContains(a2),false);
		
		/**
		 * TC 6.1.03
		 */	
		
		Field a3=new Field("vectorOfCars");
		a3.setType(cvector);
		assertEquals(BadAttributeBehaviors.saysMoreThanItContains(a3),false);
		
		/**
		 * TC 6.1.04
		 */	
		
		Field a4=new Field("typeOfFilms");
		a4.setType(cstr);
		assertEquals(BadAttributeBehaviors.saysMoreThanItContains(a4),true);
		
		/**
		 * TC 6.1.05
		 */	
		
		Field a5=new Field("typeOfFilms");
		a5.setType(ccoll);
		assertEquals(BadAttributeBehaviors.saysMoreThanItContains(a5),false);
		
		/**
		 * TC 6.1.06
		 */	
		
		Field a6=new Field("mapOfObjects");
		a6.setType(cmap);
		assertEquals(BadAttributeBehaviors.saysMoreThanItContains(a6),false);
		
		/**
		 * TC 6.1.07
		 */	
		
		Field a7=new Field("enablesStates");
		a7.setType(cbool);
		assertEquals(BadAttributeBehaviors.saysMoreThanItContains(a7),true);
		
		
		
		
		
		
		
		
		
	}

}
