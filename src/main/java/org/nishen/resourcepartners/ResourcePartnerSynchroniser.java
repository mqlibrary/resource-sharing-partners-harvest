package org.nishen.resourcepartners;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerChangeRecord;
import org.nishen.resourcepartners.entity.SyncPayload;
import org.nishen.resourcepartners.model.Partner;

public interface ResourcePartnerSynchroniser
{
	public Optional<SyncPayload> sync(boolean preview) throws ResourcePartnerSynchroniserException, IOException;

	public List<ResourcePartnerChangeRecord> comparePartners(Partner a, Partner b);

	public Partner makePartner(ResourcePartner p) throws ResourcePartnerSynchroniserException;
}