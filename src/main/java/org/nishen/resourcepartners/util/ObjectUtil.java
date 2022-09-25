package org.nishen.resourcepartners.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtil
{
	@SuppressWarnings("unchecked")
	public static <T> T deepClone(T object)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);

			return (T) ois.readObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static boolean compareStrings(String a, String b)
	{
		if (a == null && b == null)
			return true;

		if (a == null || b == null)
			return false;

		return a.equals(b);
	}
}
