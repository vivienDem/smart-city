package test.stubs;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import components.interfaces.SAMUCorrelatorI;
import event.classes.EventBase;
import event.classes.HealthEvent;
import rule.classes.RuleBase;
import rule.classes.RuleS1;
import rule.classes.RuleS2;
import rule.classes.RuleS3;
import rule.classes.RuleS4;

/**
 * La classe <code> TestEventBaseSamu </code> permet de realiser des tests JUnit
 * sur le SAMU
 * 
 * @author Adan Bougherara et Vivien Demeulenaere
 *
 */
public class TestEventBaseSamu {
	private EventBase eb;
	private Map<String, Serializable> properties;
	private Map<String, Serializable> properties2;
	private RuleBase rb;
	private SAMUCorrelatorI c;

	@BeforeEach
	public void init() {
		eb = new EventBase();
		properties = new HashMap<String, Serializable>();
		properties.put("type", "emergency");
		properties.put("area", "Ve");
		HealthEvent he1 = new HealthEvent(properties);
		HealthEvent he2 = new HealthEvent(properties);
		properties2 = new HashMap<String, Serializable>();
		properties2.put("type", "medical");
		properties2.put("area", "Ve");
		HealthEvent he3 = new HealthEvent(properties2);
		HealthEvent he4 = new HealthEvent(properties2);
		eb.addEvent(he1);
		eb.addEvent(he2);
		eb.addEvent(he3);
		eb.addEvent(he4);
		rb = new RuleBase();
		c = new SAMUCorrelatorStub();
	}

	@Test
	public void testS1() throws Exception {
		RuleS1 r1 = new RuleS1();
		rb.addRule(r1);
		assertEquals(eb.numberOfEvents(), 4);
		assertTrue(rb.fireAllOn(eb, c));
		assertEquals(eb.numberOfEvents(), 3);
	}

	@Test
	public void testS2() throws Exception {
		RuleS2 r2 = new RuleS2();
		rb.addRule(r2);
		assertEquals(eb.numberOfEvents(), 4);
		assertFalse(rb.fireFirstOn(eb, c));
		assertEquals(eb.numberOfEvents(), 4);
		assertTrue(rb.fireFirstOn(eb, c));
		assertEquals(eb.numberOfEvents(), 3);
	}

	@Test
	public void TestS3() throws Exception {
		RuleS3 r3 = new RuleS3();
		rb.addRule(r3);
		assertEquals(eb.numberOfEvents(), 4);
		assertTrue(rb.fireAllOn(eb, c));
		assertEquals(eb.numberOfEvents(), 3);
	}

	@Test
	public void TestS4() throws Exception {
		System.out.println("debut test4");
		RuleS4 r4 = new RuleS4();
		rb.addRule(r4);
		assertEquals(eb.numberOfEvents(), 4);
		assertTrue(rb.fireAllOn(eb, c));
		assertEquals(eb.numberOfEvents(), 2);
	}

}
