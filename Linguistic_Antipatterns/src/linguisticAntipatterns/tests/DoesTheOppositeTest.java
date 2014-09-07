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
	SType cint=new SType();
	cint.setName("int");
	
	/**
	 * TC 3.1.01
	 */
	
	Method m19=new Method("disableObject");
	m19.setReturnType(cenable);
	comments.add(cc11);
	m19.setComments(comments);
	assertEquals(BadMethodBehaviors.doesTheOpposite(m19),true);
	comments.clear();
	
	
	/**
	 * TC 3.1.02
	 */
	
	Method m20=new Method("activeDB");
	m20.setReturnType(cenable);
	comments.add(cc11);
	m20.setComments(comments);
	assertEquals(BadMethodBehaviors.doesTheOpposite(m20),true);
	comments.clear();
	
	/**
	 *  TC 3.1.03
	 */
	
	Method m21=new Method("activeDB");
	m21.setReturnType(cbool);
	comments.add(cc11);
	m21.setComments(comments);
	assertEquals(BadMethodBehaviors.doesTheOpposite(m21),false);
	comments.clear();
	
	/**
	 * TC 3.1.04 Il test ha successo perchè non è stata implementata la funzionalità
	 */
	/*
	Method m22=new Method("getString");
	m22.setReturnType(cint);
	comments.add(cc11);
	m22.setComments(comments);
	assertEquals(BadMethodBehaviors.doesTheOpposite(m22),true);
	comments.clear();
	*/
	
	
	
	}
}
