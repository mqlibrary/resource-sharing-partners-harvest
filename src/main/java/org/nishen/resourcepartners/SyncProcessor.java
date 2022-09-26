package org.nishen.resourcepartners;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerChangeRecord;
import org.nishen.resourcepartners.entity.SyncPayload;
import org.nishen.resourcepartners.model.Partner;

public interface SyncProcessor
{
	public Optional<SyncPayload> sync(boolean preview) throws SyncException, IOException;

	public List<ResourcePartnerChangeRecord> comparePartners(Partner a, Partner b);

	public Partner makePartner(ResourcePartner p) throws SyncException;
}