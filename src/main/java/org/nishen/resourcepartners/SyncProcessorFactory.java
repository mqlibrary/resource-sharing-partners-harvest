package org.nishen.resourcepartners;

public interface SyncProcessorFactory
{
	public SyncProcessor create(String nuc, String apikey) throws SyncException;
}
