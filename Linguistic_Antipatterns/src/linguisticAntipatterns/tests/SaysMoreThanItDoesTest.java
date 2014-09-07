package linguisticAntipatterns.tests;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;


import linguisticAntipatterns.methods.BadMethodBehaviors;

import org.junit.Test;

import sie.db.entity.CodeComment;
import sie.db.entity.Method;
import sie.db.entity.SType;


/**
 * Junit Test per il metodo saysMoreThanItDoes.
 * @author Daniele Iannone
 *
 */
public class SaysMoreThanItDoesTest { 
	/** 
	 * Test method for {@link BadMethodBehaviors#saysMoreThanItDoes(Method)}.
	 */
	@Test
	public void testSaysMoreThanItDoes() {
		Set<CodeComment> comments = new HashSet<CodeComment>();
		CodeComment cc0 = new CodeComment();
		cc0.setComment("/** "
				+ "* Fa più di quello che dice. Se qualcosa allora qualcosa, altrimenti altro. "
				+ "* @return the ob"
				+ "*/");
		CodeComment cc1 = new CodeComment();
		cc1.setComment("/** "
				+ "* Fa più di quello che dice."
				+ "* @return the ob"
				+ "*/");
		CodeComment cc2 = new CodeComment();
		cc2.setComment("/** "
				+ "@param o the ob to set"
				+ "*/");
		CodeComment cc3 = new CodeComment();
		cc3.setComment("/** "
				+ "Se qualcosa"
				+ "*/");
		CodeComment cc4 = new CodeComment();
		cc4.setComment("/** "
				+ "Finche' qualcosa"
				+ "*/");
		CodeComment cc5 = new CodeComment();
		cc5.setComment("/** "
				+ "Altrimenti qualcosa"
				+ "*/");
		CodeComment cc6 = new CodeComment();
		cc6.setComment("/** "
				+ "blablabla.se qualcosa"
				+ "*/");
		CodeComment cc7 = new CodeComment();
		cc7.setComment("/** "
				+ "while qualcosa"
				+ "*/");
		CodeComment cc8 = new CodeComment();
		cc8.setComment("/** "
				+ "WHILE qualcosa"
				+ "*/");
		CodeComment cc9 = new CodeComment();
		cc9.setComment("/** "
				+ "Finchè qualcosa"
				+ "*/");
		CodeComment cc10 = new CodeComment();
		cc10.setComment("/** "
				+ ""
				+ "*/");
		
		CodeComment cc11 = new CodeComment();
		cc11.setComment("/** "
				+ "@return int"
				+ "*/");
		
		
		String code0 = "if (p.getOb().equals(\"prova1\")) "
				+ "		System.out.println(\"OK\");"
				+ "else"
				+ "		p.setOb(\"prova1\");";

		String code1 = "switch (month) {"
				+ "		case 1:  monthString = \"January\";"
				+ "         break;"
				+ "     case 2:  monthString = \"February\";"
				+ "         break;"
				+ "		case 3:  monthString = \"March\";"
				+ "     	break;"
				+ "     default: monthString = \"Invalid month\";"
				+ "     	break;"
				+ "}";
		String code2 = "while (count < 11) {"
				+ "		System.out.println(\"Count is: \" + count);"
				+ "		count++;"
				+ "}";
		String code3 = "for(int i=1; i<11; i++){"
				+ "     System.out.println(\"Count is: \" + i);"
				+ "}";
		
		String code4="System.out.println(\" xxx \")";
		
		
		
		Set<SType> exceptions = new HashSet<SType>();
		
		SType exc1=new SType();
		exc1.setName("Exception");
		
		exceptions.add(exc1);
		
		SType cvoid = new SType();
		cvoid.setName("void");
		SType cbool=new SType();
		cbool.setName("boolean");
		SType cint=new SType();
		cint.setName("int");
		SType cobj=new SType();
		cobj.setName("Object");
		SType clist=new SType();
		clist.setName("List");
		SType cmap=new SType();
		cmap.setName("Map");
		
		
		/**
		 * TC_2_00
		 */
		Method m0 = new Method("method0");
		m0.setReturnType(cvoid);
		comments.add(cc0);
		m0.setComments(comments);
		m0.setTextContent(code0);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m0), false);
		comments.clear();
		
		/**
		 * TC_2_11
		 */
		Method m1 = new Method("method1");
		m1.setReturnType(cvoid);
		comments.add(cc1);
		m1.setComments(comments);
		m1.setTextContent(code1);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m1),false);
		comments.clear();
		
		/**
		 * TC_2_22
		 */
		Method m2 = new Method("method2");
		m2.setReturnType(cvoid);
		comments.add(cc0);
		m2.setComments(comments);
		m2.setTextContent(code2);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m2), true);
		comments.clear();
		
		/**
		 * TC_2_33
		 */
		Method m3 = new Method("method3");
		m3.setReturnType(cvoid);
		comments.add(cc3);
		m3.setComments(comments);
		m3.setTextContent(code3);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m3), true);
		comments.clear();

		/**
		 * TC_2_41
		 */
		Method m4 = new Method("method4");
		m4.setReturnType(cvoid);
		comments.add(cc4);
		m4.setComments(comments);
		m4.setTextContent(code1);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m4), true);
		comments.clear();
		
		/**
		 * TC_2_52
		 */
		Method m5 = new Method("method5");
		m5.setReturnType(cvoid);
		comments.add(cc5);
		m5.setComments(comments);
		m5.setTextContent(code2);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m5), true);
		comments.clear();

		/**
		 * TC_2_63
		 */
		Method m6 = new Method("method6");
		m6.setReturnType(cvoid);
		comments.add(cc6);
		m6.setComments(comments);
		m6.setTextContent(code3);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m6), false);
		comments.clear();
		
		/**
		*TC 1.1.02
		*/
		
		Method m7=new Method("check");
		m7.setReturnType(cbool);
		m7.setTextContent(code4);
		comments.add(cc10);
		m7.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m7),false);
		comments.clear();
		
		/**
		*TC 1.1.03
		*/		
		
		Method m8=new Method("check");
		m8.setReturnType(cvoid);
		m8.setThrowedException(exceptions);
		m8.setTextContent(code4);
		comments.add(cc10);
		m8.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m8),false);
		comments.clear();
		
		/**
		 * TC 1.1.01
		 */
		
		Method m9=new Method("check");
		m9.setReturnType(cvoid);
		m9.setTextContent(code4);
		comments.add(cc10);
		m9.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m9),true);
		comments.clear();
		
		/**
		 * TC 1.1.04
		 */
		
		Method m10=new Method("getSomething");
		m10.setReturnType(cvoid);
		comments.add(cc10);
		m10.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m10),true);
		comments.clear();
		
		/**
		 * TC 1.1.05
		 */
		Method m11=new Method("getSomething");
		m11.setReturnType(cbool);
		comments.add(cc10);
		m11.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m11),false);
		comments.clear();
		
		/**
		 * TC 1.1.06
		 */
		
		Method m12=new Method("isSomething");
		m12.setReturnType(cbool);
		comments.add(cc10);
		m12.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m12),false);
		comments.clear();
		
		/**
		 * TC 1.1.07
		 */
		
		Method m13=new Method("isSomething");
		m13.setReturnType(cint);
		comments.add(cc10);
		m13.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m13),true);
		comments.clear();
		
		/**
		 * TC 1.1.08
		 */
		
		Method m14=new Method("getObjects");
		m14.setReturnType(cobj);
		comments.add(cc10);
		m14.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m14),true);
		
		/**
		 * TC 1.1.09
		 */
		
		Method m15=new Method("getObjects");
		m15.setReturnType(clist);
		comments.add(cc10);
		m15.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m15),false);
		comments.clear();
		
		/**
		 *  TC 1.1.10
		 */
		
		Method m16=new Method("getCars");
		m16.setReturnType(cmap);
		comments.add(cc10);
		m16.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m16),false);
		comments.clear();
		
		/**
		 * TC 1.1.11
		 */
		
		Method m17=new Method("getCars");
		m17.setReturnType(cobj);
		comments.add(cc10);
		m17.setComments(comments);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m17),true);
		comments.clear();
			
		
	}

}
