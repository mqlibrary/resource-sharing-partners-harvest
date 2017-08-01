package org.nishen.resourcepartners;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.util.JaxbUtilExLibris;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestJaxbUtilExLibris
{
	private static final Logger log = LoggerFactory.getLogger(TestJaxbUtilExLibris.class);

	@Test
	public void testILRSHarvest()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		String json = "{\"line1\":\"Toowoomba Health Service\",\"line2\":\"Pechey St\"," +
		              "\"city\":\"TOOWOOMBA\",\"state_province\":\"QLD\",\"postal_code\":\"4350\"," +
		              "\"country\":{\"desc\":\"Australia\",\"value\":\"AUS\"}}";

		try
		{
			Address a = JaxbUtilExLibris.get(json, Address.class);
			log.debug("address: {}", a);
			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
