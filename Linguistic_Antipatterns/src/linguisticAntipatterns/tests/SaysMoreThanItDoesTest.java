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
		
		SType cl = new SType();
		cl.setName("void");

		/**
		 * TC_2_00
		 */
		Method m0 = new Method("method0");
		m0.setReturnType(cl);
		comments.add(cc0);
		m0.setComments(comments);
		m0.setTextContent(code0);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m0), false);
		comments.clear();
		
		/**
		 * TC_2_11
		 */
		Method m1 = new Method("method1");
		m1.setReturnType(cl);
		comments.add(cc1);
		m1.setComments(comments);
		m1.setTextContent(code1);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m1), false);
		comments.clear();
		
		/**
		 * TC_2_22
		 */
		Method m2 = new Method("method2");
		m2.setReturnType(cl);
		comments.add(cc0);
		m2.setComments(comments);
		m2.setTextContent(code2);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m2), true);
		comments.clear();
		
		/**
		 * TC_2_33
		 */
		Method m3 = new Method("method3");
		m3.setReturnType(cl);
		comments.add(cc3);
		m3.setComments(comments);
		m3.setTextContent(code3);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m3), true);
		comments.clear();

		/**
		 * TC_2_41
		 */
		Method m4 = new Method("method4");
		m4.setReturnType(cl);
		comments.add(cc4);
		m4.setComments(comments);
		m4.setTextContent(code1);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m4), true);
		comments.clear();
		
		/**
		 * TC_2_52
		 */
		Method m5 = new Method("method5");
		m5.setReturnType(cl);
		comments.add(cc5);
		m5.setComments(comments);
		m5.setTextContent(code2);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m5), true);
		comments.clear();

		/**
		 * TC_2_63
		 */
		Method m6 = new Method("method6");
		m6.setReturnType(cl);
		comments.add(cc6);
		m6.setComments(comments);
		m6.setTextContent(code3);
		assertEquals(BadMethodBehaviors.saysMoreThanItDoes(m6), false);
		comments.clear();
	}

}
