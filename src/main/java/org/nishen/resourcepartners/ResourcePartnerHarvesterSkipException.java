package org.nishen.resourcepartners;

public class ResourcePartnerHarvesterSkipException extends Exception
{
	private static final long serialVersionUID = -2153804468705212252L;

	public ResourcePartnerHarvesterSkipException()
	{
		super();
	}

	public ResourcePartnerHarvesterSkipException(String msg)
	{
		super(msg);
	}

	public ResourcePartnerHarvesterSkipException(Throwable t)
	{
		super(t);
	}
}
