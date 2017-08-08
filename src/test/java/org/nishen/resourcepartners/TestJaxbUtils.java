package org.nishen.resourcepartners;

import java.util.Arrays;

import org.junit.Test;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.model.Address.Country;
import org.nishen.resourcepartners.model.ObjectFactory;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.nishen.resourcepartners.util.JaxbUtilModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.equalTo;

public class TestJaxbUtils
{
	private static final Logger log = LoggerFactory.getLogger(TestJaxbUtils.class);

	private static final ObjectFactory of = new ObjectFactory();

	@Test
	public void testUnmarshalling()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		String json = "{\"line1\":\"Toowoomba Health Service\",\"line2\":\"Pechey St\"," +
		              "\"city\":\"TOOWOOMBA\",\"state_province\":\"QLD\",\"postal_code\":\"4350\"," +
		              "\"country\":{\"desc\":\"Australia\",\"value\":\"AUS\"}}";

		try
		{
			Country c = of.createAddressCountry();
			c.setValue("AUS");
			c.setDesc("Australia");

			Address expected = of.createAddress();
			expected.setLine1("Toowoomba Health Service");
			expected.setLine2("Pechey St");
			expected.setCity("TOOWOOMBA");
			expected.setStateProvince("QLD");
			expected.setPostalCode("4350");
			expected.setCountry(c);

			Address actual = JaxbUtilModel.get(json, Address.class);
			log.debug("address: {}", actual);
			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testMarshalling()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		String expected = "{\"line1\":\"Toowoomba Health Service\",\"line2\":\"Pechey St\"," +
		                  "\"city\":\"TOOWOOMBA\",\"state_province\":\"QLD\",\"postal_code\":\"4350\"," +
		                  "\"country\":{\"desc\":\"Australia\",\"value\":\"AUS\"}}";

		try
		{
			Country c = of.createAddressCountry();
			c.setValue("AUS");
			c.setDesc("Australia");

			Address address = of.createAddress();
			address.setLine1("Toowoomba Health Service");
			address.setLine2("Pechey St");
			address.setCity("TOOWOOMBA");
			address.setStateProvince("QLD");
			address.setPostalCode("4350");
			address.setCountry(c);

			String actual = JaxbUtilModel.format(address);
			log.debug("address: {}", actual);
			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testMarshalling02()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		String expected = "{\"nuc\":\"TEST\",\"updated\":\"123123123\",\"name\":\"Test Organisation\"," +
		                  "\"enabled\":true,\"suspension_start\":null,\"suspension_end\":null}";

		try
		{
			ElasticSearchPartner partner = new ElasticSearchPartner();
			partner.setNuc("TEST");
			partner.setEnabled(true);
			partner.setName("Test Organisation");
			partner.setUpdated("123123123");
			partner.setSuspensionEnd(null);
			partner.setSuspensionStart(null);

			String actual = JaxbUtil.format(partner);
			log.debug("address: {}", actual);
			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
