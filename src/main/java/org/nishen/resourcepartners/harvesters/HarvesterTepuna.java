package org.nishen.resourcepartners.harvesters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.dao.TepunaDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchPartnerAddress;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author nishen
 *
 */
public class HarvesterTepuna implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterTepuna.class);

	private static final String SOURCE_SYSTEM = "TEPUNA";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private TepunaDAO tepuna;

	@Inject
	public HarvesterTepuna(TepunaDAO tepuna)
	{
		this.tepuna = tepuna;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public String getSource()
	{
		return SOURCE_SYSTEM;
	}

	@Override
	public Map<String, ElasticSearchPartner> harvest()
	{
		Map<String, ElasticSearchPartner> tepunaPartners = tepuna.getData();

		return tepunaPartners;
	}

	@Override
	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> partners,
	                                                Map<String, ElasticSearchPartner> latest,
	                                                List<ElasticSearchChangeRecord> changes)
	{
		Map<String, ElasticSearchPartner> updated = new HashMap<String, ElasticSearchPartner>();

		List<String> removeList = new ArrayList<String>();
		for (String s : partners.keySet())
			if (s.startsWith(NZ_NUC_PREFIX))
				removeList.add(s);

		for (String nuc : latest.keySet())
		{
			removeList.remove(nuc);

			ElasticSearchPartner l = latest.get(nuc);
			ElasticSearchPartner p = partners.get(nuc);

			boolean requiresUpdate = false;

			if (p == null)
			{
				l.setUpdated(sdf.format(new Date()));
				updated.put(nuc, l);
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "partner", null, JaxbUtil.format(l)));

				continue;
			}

			if (p.isEnabled() != l.isEnabled())
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "enabled",
				                                          Boolean.toString(p.isEnabled()),
				                                          Boolean.toString(l.isEnabled())));
				p.setEnabled(l.isEnabled());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getStatus(), l.getStatus()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "status", p.getStatus(), l.getStatus()));
				p.setStatus(l.getStatus());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getName(), l.getName()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "name", p.getName(), l.getName()));
				p.setName(l.getName());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getEmailMain(), l.getEmailMain()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "email_main", p.getEmailMain(),
				                                          l.getEmailMain()));
				p.setEmailMain(l.getEmailMain());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getEmailIll(), l.getEmailIll()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "email_ill", p.getEmailIll(),
				                                          l.getEmailIll()));
				p.setEmailIll(l.getEmailIll());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getPhoneMain(), l.getPhoneMain()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "phone_main", p.getPhoneMain(),
				                                          l.getPhoneMain()));
				p.setPhoneMain(l.getPhoneMain());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getPhoneIll(), l.getPhoneIll()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "phone_ill", p.getPhoneIll(),
				                                          l.getPhoneIll()));
				p.setPhoneIll(l.getPhoneIll());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getPhoneFax(), l.getPhoneFax()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "phone_fax", p.getPhoneFax(),
				                                          l.getPhoneFax()));
				p.setPhoneFax(l.getPhoneFax());
				requiresUpdate = true;
			}

			Map<String, ElasticSearchPartnerAddress> pAddresses = new HashMap<String, ElasticSearchPartnerAddress>();
			for (ElasticSearchPartnerAddress ea : p.getAddresses())
				pAddresses.put(ea.getAddressType(), ea);

			for (ElasticSearchPartnerAddress la : l.getAddresses())
			{
				ElasticSearchPartnerAddress pa = pAddresses.remove(la.getAddressType());
				if (pa == null)
				{
					changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "address:" + la.getAddressType(),
					                                          null, JaxbUtil.format(la)));
					p.getAddresses().add(la);
					requiresUpdate = true;
				}
				else if (!la.equals(pa))
				{
					changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "address:" + la.getAddressType(),
					                                          JaxbUtil.format(pa), JaxbUtil.format(la)));
					p.getAddresses().remove(pa);
					p.getAddresses().add(la);
					requiresUpdate = true;
				}
			}

			for (String type : pAddresses.keySet())
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "address:" + type + ":status", "active",
				                                          "inactive"));
				pAddresses.get(type).setAddressStatus("inactive");
				requiresUpdate = true;
			}

			if (requiresUpdate)
			{
				p.setUpdated(sdf.format(new Date()));
				updated.put(nuc, p);
			}
		}

		for (String nuc : removeList)
		{
			ElasticSearchPartner p = partners.get(nuc);
			if (p != null && p.isEnabled())
			{
				p.setUpdated(sdf.format(new Date()));
				p.setEnabled(false);
				updated.put(nuc, p);
			}
		}

		return updated;
	}
}
