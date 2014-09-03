package linguisticAntipatterns.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;



import linguisticAntipatterns.methods.BadMethodBehaviors;

import org.junit.Test;

import sie.db.entity.CodeComment;
import sie.db.entity.Method;
import sie.db.entity.SType;


public class DoesTheOppositeTest {
	
	@Test
	public void testDoesTheOpposite()
	{
	
	Set<CodeComment> comments = new HashSet<CodeComment>();
	CodeComment cc10 = new CodeComment(); 
	cc10.setComment("/** "+ ""
			+ "*/");
	
	CodeComment cc11 = new CodeComment();
	cc11.setComment("/** "
			+ "@return int"
			+ "*/");
	
	
	
	SType cbool=new SType();
	cbool.setName("boolean");
	SType cstr=new SType();
	cstr.setName("String");
	SType ccontrol=new SType();
	ccontrol.setName("ControlEnableState");
	
	/**
	 * TC 3.1.01
	 */
	
	Method m18=new Method("getInt");
	m18.setReturnType(cstr);
	comments.add(cc10);
	m18.setComments(comments);
	assertEquals(BadMethodBehaviors.doesTheOpposite(m18),true);
	comments.clear();
	
	/**
	 * TC 3.1.02
	 */
	
	Method m19=new Method("disable");
	m19.setReturnType(ccontrol);
	comments.add(cc10);
	m19.setComments(comments);
	assertEquals(BadMethodBehaviors.doesTheOpposite(m19),true);
	comments.clear();
	
	
	/**
	 * TC 3.1.04
	 */
	
	Method m20=new Method("getValue");
	m20.setReturnType(cbool);
	comments.add(cc11);
	m20.setComments(comments);
	assertEquals(BadMethodBehaviors.doesTheOpposite(m20),true);
	comments.clear();
	
	
	}
}
