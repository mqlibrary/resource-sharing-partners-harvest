package org.nishen.resourcepartners;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.util.JaxbUtilModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestJaxbUtilModel
{
	private static final Logger log = LoggerFactory.getLogger(TestJaxbUtilModel.class);

	@Test
	public void testILRSHarvest()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		String json = "{\"line1\":\"Toowoomba Health Service\",\"line2\":\"Pechey St\"," +
		              "\"city\":\"TOOWOOMBA\",\"state_province\":\"QLD\",\"postal_code\":\"4350\"," +
		              "\"country\":{\"desc\":\"Australia\",\"value\":\"AUS\"}}";

		try
		{
			Address a = JaxbUtilModel.get(json, Address.class);
			log.debug("address: {}", a);
			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
