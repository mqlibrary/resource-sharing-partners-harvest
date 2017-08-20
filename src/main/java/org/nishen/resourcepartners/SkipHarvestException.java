package org.nishen.resourcepartners;

public class SkipHarvestException extends Exception
{
	private static final long serialVersionUID = -2153804468705212252L;

	public SkipHarvestException()
	{
		super();
	}

	public SkipHarvestException(String msg)
	{
		super(msg);
	}

	public SkipHarvestException(Throwable t)
	{
		super(t);
	}
}
