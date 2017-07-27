package org.nishen.resourcepartners.util;

public class AlternateAddressTypeException extends Exception
{
	private static final long serialVersionUID = -1489900689112442973L;

	public AlternateAddressTypeException(String message)
	{
		super(message);
	}

	public AlternateAddressTypeException(Throwable cause)
	{
		super(cause);
	}

	public AlternateAddressTypeException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public AlternateAddressTypeException(String message, Throwable cause, boolean enableSuppression,
	                                     boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
