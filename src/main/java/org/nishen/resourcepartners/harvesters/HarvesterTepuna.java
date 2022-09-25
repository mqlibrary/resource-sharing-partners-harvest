package org.nishen.resourcepartners.harvesters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.nishen.resourcepartners.dao.TepunaDAO;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerAddress;
import org.nishen.resourcepartners.entity.ResourcePartnerChangeRecord;
import org.nishen.resourcepartners.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

/**
 * @author nishen
 *
 */
public class HarvesterTepuna implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterTepuna.class);

	private static final String SOURCE_SYSTEM = "TEPUNA";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private ObjectMapper om = new ObjectMapper();

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
	public Map<String, ResourcePartner> harvest()
	{
		Map<String, ResourcePartner> tepunaPartners = tepuna.getData();

		return tepunaPartners;
	}

	@Override
	public Map<String, ResourcePartner> update(Map<String, ResourcePartner> partners,
	                                           Map<String, ResourcePartner> latest,
	                                           List<ResourcePartnerChangeRecord> changes)
	{
		Map<String, ResourcePartner> updated = new HashMap<String, ResourcePartner>();

		List<String> removeList = new ArrayList<String>();
		for (String s : partners.keySet())
			if (s.startsWith(NZ_NUC_PREFIX))
				removeList.add(s);

		for (String nuc : latest.keySet())
		{
			removeList.remove(nuc);

			ResourcePartner l = latest.get(nuc);
			ResourcePartner p = partners.get(nuc);

			boolean requiresUpdate = false;

			try
			{
				if (p == null)
				{
					l.setUpdated(sdf.format(new Date()));
					updated.put(nuc, l);
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "partner", null,
					                                            om.writeValueAsString(l)));

					continue;
				}

				if (p.isEnabled() != l.isEnabled())
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "enabled",
					                                            Boolean.toString(p.isEnabled()),
					                                            Boolean.toString(l.isEnabled())));
					p.setEnabled(l.isEnabled());
					requiresUpdate = true;
				}

				if (!ObjectUtil.compareStrings(p.getStatus(), l.getStatus()))
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "status", p.getStatus(),
					                                            l.getStatus()));
					p.setStatus(l.getStatus());
					requiresUpdate = true;
				}

				if (!ObjectUtil.compareStrings(p.getName(), l.getName()))
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "name", p.getName(), l.getName()));
					p.setName(l.getName());
					requiresUpdate = true;
				}

				if (!ObjectUtil.compareStrings(p.getEmailMain(), l.getEmailMain()))
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "email_main", p.getEmailMain(),
					                                            l.getEmailMain()));
					p.setEmailMain(l.getEmailMain());
					requiresUpdate = true;
				}

				if (!ObjectUtil.compareStrings(p.getEmailIll(), l.getEmailIll()))
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "email_ill", p.getEmailIll(),
					                                            l.getEmailIll()));
					p.setEmailIll(l.getEmailIll());
					requiresUpdate = true;
				}

				if (!ObjectUtil.compareStrings(p.getPhoneMain(), l.getPhoneMain()))
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "phone_main", p.getPhoneMain(),
					                                            l.getPhoneMain()));
					p.setPhoneMain(l.getPhoneMain());
					requiresUpdate = true;
				}

				if (!ObjectUtil.compareStrings(p.getPhoneIll(), l.getPhoneIll()))
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "phone_ill", p.getPhoneIll(),
					                                            l.getPhoneIll()));
					p.setPhoneIll(l.getPhoneIll());
					requiresUpdate = true;
				}

				if (!ObjectUtil.compareStrings(p.getPhoneFax(), l.getPhoneFax()))
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "phone_fax", p.getPhoneFax(),
					                                            l.getPhoneFax()));
					p.setPhoneFax(l.getPhoneFax());
					requiresUpdate = true;
				}

				Map<String, ResourcePartnerAddress> pAddresses = new HashMap<String, ResourcePartnerAddress>();
				for (ResourcePartnerAddress ea : p.getAddresses())
					pAddresses.put(ea.getAddressType(), ea);

				if (l.getAddresses() == null || l.getAddresses().size() == 0)
				{
					pAddresses.clear();
					if (p.getAddresses() != null && p.getAddresses().size() > 0)
					{
						p.getAddresses().clear();
						requiresUpdate = true;
					}
				}
				else
				{
					for (ResourcePartnerAddress la : l.getAddresses())
					{
						ResourcePartnerAddress pa = pAddresses.remove(la.getAddressType());
						if (pa == null)
						{
							changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc,
							                                            "address:" + la.getAddressType(), null,
							                                            om.writeValueAsString(la)));
							p.getAddresses().add(la);
							requiresUpdate = true;
						}
						else if (!la.equals(pa))
						{
							changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc,
							                                            "address:" + la.getAddressType(),
							                                            om.writeValueAsString(pa),
							                                            om.writeValueAsString(la)));
							p.getAddresses().remove(pa);
							p.getAddresses().add(la);
							requiresUpdate = true;
						}
					}
				}

				for (String type : pAddresses.keySet())
				{
					changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "address:" + type + ":status",
					                                            "active", "inactive"));
					pAddresses.get(type).setAddressStatus("inactive");
					requiresUpdate = true;
				}
			}
			catch (JsonProcessingException jpe)
			{
				log.error("{}", jpe.getMessage(), jpe);
			}

			if (requiresUpdate)
			{
				p.setUpdated(sdf.format(new Date()));
				updated.put(nuc, p);
			}
		}

		for (String nuc : removeList)
		{
			ResourcePartner p = partners.get(nuc);
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
