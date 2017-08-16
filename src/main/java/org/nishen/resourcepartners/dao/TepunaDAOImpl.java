package org.nishen.resourcepartners.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchPartnerAddress;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.model.Address.Country;
import org.nishen.resourcepartners.model.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class TepunaDAOImpl implements TepunaDAO
{
	private static final Logger log = LoggerFactory.getLogger(TepunaDAOImpl.class);

	private static final String NUC_PREFIX = "NLNZ";

	private ObjectFactory of = new ObjectFactory();

	private WebTarget tepunaTarget;

	@Inject
	public TepunaDAOImpl(@Named("ws.tepuna") Provider<WebTarget> tepunaTargetProvider)
	{
		this.tepunaTarget = tepunaTargetProvider.get();

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public Map<String, ElasticSearchPartner> getData() throws ClientErrorException
	{
		String data = tepunaTarget.request(MediaType.TEXT_PLAIN).get(String.class);

		Map<String, ElasticSearchPartner> tepunaPartners = new HashMap<String, ElasticSearchPartner>();

		try (CSVParser parser = CSVParser.parse(data, CSVFormat.DEFAULT.withHeader()))
		{
			for (CSVRecord record : parser)
			{
				ElasticSearchPartner partner = new ElasticSearchPartner();
				partner.setAddresses(new ArrayList<ElasticSearchPartnerAddress>());
				partner.setNuc(NUC_PREFIX + ":" + record.get(0));
				partner.setEnabled(true);
				partner.setStatus("active");

				if (record.get(2) != null && !"".equals(record.get(2).trim()))
					partner.setName(record.get(2));

				if (record.get(6) != null && !"".equals(record.get(6).trim()))
					partner.setEmailMain(record.get(6));

				if (record.get(16) != null && !"".equals(record.get(16).trim()))
					partner.setEmailIll(record.get(16));

				if (record.get(7) != null && !"".equals(record.get(7).trim()))
					partner.setPhoneMain(record.get(7));

				if (record.get(15) != null && !"".equals(record.get(15).trim()))
					partner.setPhoneIll(record.get(15));

				String s = record.get(4);
				if (s != null && !"".equals(s.trim()))
				{
					Address a = getAddress(s);

					ElasticSearchPartnerAddress address = new ElasticSearchPartnerAddress();
					address.setAddressType("main");
					address.setAddressStatus("active");
					address.setAddressDetail(a);
					partner.getAddresses().add(address);
				}

				s = record.get(5);
				if (s != null && !"".equals(s.trim()))
				{
					Address a = getAddress(s);

					ElasticSearchPartnerAddress address = new ElasticSearchPartnerAddress();
					address.setAddressType("main");
					address.setAddressStatus("active");
					address.setAddressDetail(a);
					partner.getAddresses().add(address);
				}

				tepunaPartners.put(partner.getNuc(), partner);
			}
		}
		catch (IOException ioe)
		{
			log.error("unable to parse data: {}", tepunaTarget.getUri());
		}

		return tepunaPartners;
	}

	public Address getAddress(String s)
	{
		Address address = of.createAddress();

		if (s == null || s.trim().length() == 0)
			return address;

		List<String> tmpl = Arrays.asList(s.split(" *, *"));
		List<String> addr = new ArrayList<String>();
		for (String tli : tmpl)
			if (tli != null && !"".equals(tli))
				addr.add(0, tli);

		if (addr.size() == 0)
			return address;

		address.setLine1(addr.get(addr.size() - 1));

		Country country = new Country();
		switch (addr.get(0))
		{
			case "Australia":
				country.setValue("AUS");
				country.setDesc("Australia");
				addr.remove(0);
				address.setCountry(country);
				break;

			case "New Zealand":
				country.setValue("NZL");
				country.setDesc("New Zealand");
				addr.remove(0);
				address.setCountry(country);
				break;

			default:
		}

		if (addr.size() == 0)
			return address;

		if (addr.get(0).matches("\\d{4}"))
		{
			address.setPostalCode(addr.get(0));
			addr.remove(0);

			if (addr.size() == 0)
				return address;

			address.setCity(addr.get(0));
			addr.remove(0);

			if (addr.size() == 0)
				return address;
		}
		else
		{
			address.setCity(addr.get(0));
			addr.remove(0);

			if (addr.size() == 0)
				return address;
		}

		Collections.reverse(addr);

		address.setLine1(addr.get(0));
		addr.remove(0);
		if (addr.size() == 0)
			return address;

		address.setLine2(addr.get(0));
		addr.remove(0);
		if (addr.size() == 0)
			return address;

		address.setLine3(addr.get(0));
		addr.remove(0);
		if (addr.size() == 0)
			return address;

		address.setLine4(addr.get(0));
		addr.remove(0);
		if (addr.size() == 0)
			return address;

		address.setLine5(addr.get(0));
		addr.remove(0);

		return address;
	}
}
