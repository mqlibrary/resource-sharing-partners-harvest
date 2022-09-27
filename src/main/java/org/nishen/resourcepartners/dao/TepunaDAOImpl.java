package org.nishen.resourcepartners.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerAddress;
import org.nishen.resourcepartners.entity.ResourcePartnerSuspension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * @author nishen
 *
 */
public class TepunaDAOImpl implements TepunaDAO
{
	private static final Logger log = LoggerFactory.getLogger(TepunaDAOImpl.class);

	private static final String NZ_NUC_PREFIX = "NLNZ";

	private WebTarget tepunaTarget;

	@Inject
	public TepunaDAOImpl(@Named("ws.tepuna") Provider<WebTarget> tepunaTargetProvider)
	{
		this.tepunaTarget = tepunaTargetProvider.get();

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public Map<String, ResourcePartner> getData() throws ClientErrorException
	{
		String data = tepunaTarget.request(MediaType.TEXT_PLAIN).get(String.class);

		Map<String, ResourcePartner> tepunaPartners = new HashMap<String, ResourcePartner>();

		CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
		try (CSVParser parser = CSVParser.parse(data, csvFormat))
		{
			for (CSVRecord record : parser)
			{
				ResourcePartner partner = new ResourcePartner();
				partner.setNuc(NZ_NUC_PREFIX + ":" + record.get(0));
				partner.setEnabled(true);
				partner.setStatus(ResourcePartnerSuspension.NOT_SUSPENDED);

				if (record.get(4) != null && !"".equals(record.get(4).trim()))
					partner.setName(record.get(4));

				if (record.get(8) != null && !"".equals(record.get(8).trim()))
					partner.setEmailMain(record.get(8));

				if (record.get(18) != null && !"".equals(record.get(18).trim()))
					partner.setEmailIll(record.get(18));

				if (record.get(9) != null && !"".equals(record.get(9).trim()))
					partner.setPhoneMain(record.get(9));

				if (record.get(17) != null && !"".equals(record.get(17).trim()))
					partner.setPhoneIll(record.get(17));

				String s = record.get(6);
				if (s != null && !"".equals(s.trim()))
				{
					ResourcePartnerAddress address = getAddress(s);
					address.setAddressType("main");
					address.setAddressStatus("active");
					partner.getAddresses().add(address);
				}

				s = record.get(7);
				if (s != null && !"".equals(s.trim()))
				{
					ResourcePartnerAddress address = getAddress(s);
					address.setAddressType("postal");
					address.setAddressStatus("active");
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

	public ResourcePartnerAddress getAddress(String s)
	{
		ResourcePartnerAddress address = new ResourcePartnerAddress();

		if (s == null || s.trim().length() == 0)
			return address;

		List<String> tmpl = Arrays.asList(s.split(" *\r?\n *"));
		List<String> addr = new ArrayList<String>();
		for (String tli : tmpl)
			if (tli != null && !"".equals(tli))
				addr.add(0, tli);

		if (addr.size() == 0)
			return address;

		address.setLine1(addr.get(addr.size() - 1));

		if (addr.get(0).equalsIgnoreCase("New Zealand"))
		{
			address.setCountry("New Zealand");
			addr.remove(0);
		}
		else if (addr.get(0).equalsIgnoreCase("Australia"))
		{
			address.setCountry("Australia");
			addr.remove(0);
		}
		else if (addr.get(0).equalsIgnoreCase("Samoa"))
		{
			address.setCountry("Samoa");
			addr.remove(0);
		}

		if (addr.size() == 0)
			return address;

		Pattern p = Pattern.compile("^(.+) (\\d{3,4})$");
		for (int pos = 0; pos < addr.size(); pos++)
		{
			Matcher m = p.matcher(addr.get(pos));
			if (m.matches())
			{
				address.setCity(m.group(1));
				address.setPostalCode(m.group(2));
				addr.remove(pos);
				break;
			}
		}

		if (addr.size() == 0)
			return address;

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
