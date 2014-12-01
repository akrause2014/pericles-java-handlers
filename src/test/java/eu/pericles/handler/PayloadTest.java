package eu.pericles.handler;

import junit.framework.TestCase;

import org.junit.Test;

public class PayloadTest {

	@Test
	public void testCorrect() throws Exception 
	{
		Payload payload = new Payload("ls", "-l");
		String result = payload.call();
		TestCase.assertEquals("completed", result);
	}

	@Test
	public void testFail() throws Exception 
	{
		Payload payload = new Payload("_x_0csxcfwe");
		String result = payload.call();
		TestCase.assertTrue(result.startsWith("error"));
	}
}
