package org.nishen.resourcepartners.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.nishen.resourcepartners.entity.Address;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxbUtil
{
	private static final Logger log = LoggerFactory.getLogger(JaxbUtil.class);

	private static final String JAXB_PACKAGE = "org.nishen.resourcepartners.entity";

	public static String formatAddress(Address address)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		formatAddress(address, out);

		return new String(out.toByteArray());
	}

	public static void formatAddress(Address address, OutputStream out)
	{
		try
		{
			JAXBContext c = JAXBContext.newInstance(Address.class);
			Marshaller m = c.createMarshaller();
			m.setProperty("jaxb.formatted.output", Boolean.TRUE);
			m.setProperty("eclipselink.media-type", "application/json");
			m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			QName qName = new QName(JAXB_PACKAGE, "address");
			JAXBElement<Address> addressType = new JAXBElement<Address>(qName, Address.class, address);

			m.marshal(addressType, out);
		}
		catch (Exception e)
		{
			log.error("failed to obtain Address representation: {}", e.getMessage(), e);
		}
	}

	public static String formatElasticSearchPartner(ElasticSearchPartner elasticSearchPartner)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		formatElasticSearchPartner(elasticSearchPartner, out);

		return new String(out.toByteArray());
	}

	public static void formatElasticSearchPartner(ElasticSearchPartner elasticSearchPartner, OutputStream out)
	{
		try
		{
			JAXBContext c = JAXBContext.newInstance(ElasticSearchPartner.class);
			Marshaller m = c.createMarshaller();
			m.setProperty("jaxb.formatted.output", Boolean.TRUE);
			m.setProperty("eclipselink.media-type", "application/json");
			m.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			m.marshal(elasticSearchPartner, out);
		}
		catch (Exception e)
		{
			log.error("failed to obtain Address representation: {}", e.getMessage(), e);
		}
	}
}