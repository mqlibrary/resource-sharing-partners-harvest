package org.nishen.resourcepartners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.harvesters.Harvester;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Named;

/**
 * @author nishen.naidoo
 *
 */
public class ResourcePartnerProcessorImpl implements ResourcePartnerProcessor
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerProcessorImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private ElasticSearchDAO elastic;

	private Harvester laddHarvester;
	private Harvester ilrsHarvester;
	private Harvester tepunaHarvester;

	private List<ElasticSearchChangeRecord> changes;

	public ResourcePartnerProcessorImpl(ElasticSearchDAO elastic, @Named("harvester.ladd") Harvester laddHarvester,
	                                    @Named("harvester.ilrs") Harvester ilrsHarvester,
	                                    @Named("harvester.tepuna") Harvester tepunaHarvester)
	{
		this.elastic = elastic;

		this.laddHarvester = laddHarvester;
		this.ilrsHarvester = ilrsHarvester;
		this.tepunaHarvester = tepunaHarvester;

		this.changes = new ArrayList<ElasticSearchChangeRecord>();

		log.debug("instantiated ResourcePartnerProcessor");
	}

	public void process()
	{
		Map<String, ElasticSearchPartner> partners = elastic.getPartners();

		Map<String, ElasticSearchPartner> laddPartners = laddHarvester.harvest();
		Map<String, ElasticSearchPartner> changedPartners = update(partners, laddPartners, "ladd");
	}

	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> dst,
	                                                Map<String, ElasticSearchPartner> src, String sourceSystem)
	{
		Map<String, ElasticSearchPartner> updated = new HashMap<String, ElasticSearchPartner>();

		for (String nuc : src.keySet())
		{
			ElasticSearchPartner s = dst.get(nuc);
			ElasticSearchPartner d = dst.get(nuc);

			boolean requiresUpdate = false;

			if (d == null)
			{
				s.setUpdated(sdf.format(new Date()));
				updated.put(nuc, s);
				changes.add(new ElasticSearchChangeRecord(sourceSystem, nuc, "partner", null, JaxbUtil.format(s)));

				continue;
			}

			if (!compareStrings(d.getName(), s.getName()))
			{
				requiresUpdate = true;
			}

			if (requiresUpdate)
			{
				d.setUpdated(sdf.format(new Date()));
				updated.put(nuc, d);
			}
		}

		return updated;
	}

	private boolean compareStrings(String a, String b)
	{
		if (a == null && b == null)
			return true;

		if (a == null || b == null)
			return false;

		return a.equals(b);
	}
}
