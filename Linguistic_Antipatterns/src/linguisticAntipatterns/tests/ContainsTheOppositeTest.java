package linguisticAntipatterns.tests;

import static org.junit.Assert.assertEquals;



import java.util.HashSet;
import java.util.Set;

import linguisticAntipatterns.methods.BadAttributeBehaviors;

import org.junit.Test;

import sie.db.entity.CodeComment;
import sie.db.entity.Field;
import sie.db.entity.SType;



public class ContainsTheOppositeTest {
	
	@Test
	public void ContainsTheTheOpposite()
	{
		Set<CodeComment> comments = new HashSet<CodeComment>();
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
		cenable.setName("enable");
		SType ccoll=new SType();
		ccoll.setName("Collection");
		SType cvector=new SType();
		cvector.setName("Vector");
		SType cmap=new SType();
		cmap.setName("Map");
		
		
		
		/**
		 * TC 5.1.01
		 */
		 
		
		Field a1=new Field("disable");
		a1.setType(cenable);
		assertEquals(BadAttributeBehaviors.containsTheOpposite(a1),true);
		
		/**
		 * TC 5.1.02
		 */

		Field a2=new Field("disable");
		a2.setType(cbool);
		comments.add(cc11);
		a2.setComments(comments);
		assertEquals(BadAttributeBehaviors.containsTheOpposite(a2),true);
		
		/**
		 * TC 5.1.03
		 */
		
		Field a3=new Field("disable");
		a3.setType(cbool);
		assertEquals(BadAttributeBehaviors.containsTheOpposite(a3),false);
		
		/**
		 * TC 5.1.04
		 */
		
		Field a4=new Field("active");
		a4.setType(cenable);
		assertEquals(BadAttributeBehaviors.containsTheOpposite(a4),false);
		
				
	}

}
