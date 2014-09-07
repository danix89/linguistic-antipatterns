package linguisticAntipatterns.tests;


import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;



import linguisticAntipatterns.methods.BadMethodBehaviors;

import org.junit.Test;

import sie.db.entity.CodeComment;
import sie.db.entity.Method;
import sie.db.entity.SType;


public class DoesMoreThanItSaysTest {
	
	@Test
	public void testDoesMoreThanItSays() {
		
		Set<CodeComment> comments = new HashSet<CodeComment>();
		CodeComment cc1 = new CodeComment();
		cc1.setComment("/** "
				+ "* Fa più di quello che dice."
				+ "* @return the ob"
				+ "*/");
		
		
		String code0 = "Object object=new Object(); "
				+ "	return 0";
		String code1 = "return 0 ";
		String code2=  "return true";
		
		
		
		
		SType cint= new SType();
		cint.setName("int");
		SType cbool=new SType();
		cbool.setName("boolean");
		SType cvoid= new SType();
		cvoid.setName("void");
		SType ccoll=new SType();
		ccoll.setName("Collection");
		
		/**
		 * TC 2.1.02
		 */
		
		Method m1=new Method("getSomething");
		m1.setReturnType(cint);
		comments.add(cc1);
		m1.setComments(comments);
		m1.setTextContent(code0);
		assertEquals(BadMethodBehaviors.doesMoreThanItSays(m1), true);
		comments.clear();
		
		/**
		 * TC 2.1.01
		 */
		
		Method m2=new Method("getSomething");
		m2.setReturnType(cint);
		comments.add(cc1);
		m2.setComments(comments);
		m2.setTextContent(code1);
		assertEquals(BadMethodBehaviors.doesMoreThanItSays(m2), false);
		comments.clear();
		
		/**
		 * TC 2.1.03
		 */
		
		Method m5=new Method("setSomething");
		m5.setReturnType(cbool);
		comments.add(cc1);
		m5.setComments(comments);
		m5.setTextContent(code2);
		assertEquals(BadMethodBehaviors.doesMoreThanItSays(m5), true);		
		comments.clear();
		
		/**
		 * TC 2.1.04
		 */
		Method m6=new Method("setSomething");
		m6.setReturnType(cvoid);
		comments.add(cc1);
		m6.setComments(comments);
		m6.setTextContent(code2);
		assertEquals(BadMethodBehaviors.doesMoreThanItSays(m6),false);		
		comments.clear();
		
		/**
		 * TC 2.1.05
		 */
		
		Method m3=new Method("isSomething");
		m3.setReturnType(cbool);
		comments.add(cc1);
		m3.setComments(comments);
		m3.setTextContent(code2);
		assertEquals(BadMethodBehaviors.doesMoreThanItSays(m3), false);		
		comments.clear();
		
		/**
		 * TC 2.1.06
		 */
		
		Method m4=new Method("isSomething");
		m4.setReturnType(cint);
		comments.add(cc1);
		m4.setComments(comments);
		m4.setTextContent(code2);
		assertEquals(BadMethodBehaviors.doesMoreThanItSays(m4), true);		
		comments.clear();
		
		/**
		 * TC 2.1.07
		 */
		
		
		Method m7=new Method("getObjects");
		m7.setReturnType(ccoll);
		comments.add(cc1);
		m7.setComments(comments);
		m7.setTextContent(code2);
		assertEquals(BadMethodBehaviors.doesMoreThanItSays(m7),false);		
		comments.clear();
		
		
		/**
		 * TC 2.1.08
		 */
		
		
		Method m8=new Method("getObject");
		m8.setReturnType(ccoll);
		comments.add(cc1);
		m8.setComments(comments);
		m8.setTextContent(code2);
		assertEquals(BadMethodBehaviors.doesMoreThanItSays(m8),true);		
		comments.clear();		
		
		
		
	}

}
