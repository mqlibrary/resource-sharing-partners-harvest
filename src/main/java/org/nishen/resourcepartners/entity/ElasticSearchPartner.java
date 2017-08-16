package org.nishen.resourcepartners.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "partner")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "nuc", "updated", "name", "enabled", "status", "suspensionStart", "suspensionEnd", "emailMain",
                       "emailIll", "phoneMain", "phoneIll", "addresses" })
public class ElasticSearchPartner implements ElasticSearchEntity
{
	@XmlElement(name = "nuc")
	private String nuc;

	@XmlElement(name = "updated")
	private String updated;

	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "enabled")
	private boolean enabled;

	@XmlElement(name = "status")
	private String status;

	@XmlElement(name = "suspension_start", nillable = true)
	private String suspensionStart;

	@XmlElement(name = "suspension_end", nillable = true)
	private String suspensionEnd;

	@XmlElement(name = "email_main")
	private String emailMain;

	@XmlElement(name = "email_ill")
	private String emailIll;

	@XmlElement(name = "phone_main")
	private String phoneMain;

	@XmlElement(name = "phone_ill")
	private String phoneIll;

	@XmlElement(name = "addresses")
	private List<ElasticSearchPartnerAddress> addresses;

	@Override
	public String getElasticSearchId()
	{
		return nuc;
	}

	@Override
	public String getElasticSearchIndex()
	{
		return "partners";
	}

	@Override
	public String getElasticSearchType()
	{
		return "partner";
	}

	@Override
	public String getTime()
	{
		return updated;
	}

	public String getNuc()
	{
		return nuc;
	}

	public void setNuc(String nuc)
	{
		this.nuc = nuc;
	}

	public String getUpdated()
	{
		return updated;
	}

	public void setUpdated(String updated)
	{
		this.updated = updated;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getSuspensionStart()
	{
		return suspensionStart;
	}

	public void setSuspensionStart(String suspensionStart)
	{
		this.suspensionStart = suspensionStart;
	}

	public String getSuspensionEnd()
	{
		return suspensionEnd;
	}

	public void setSuspensionEnd(String suspensionEnd)
	{
		this.suspensionEnd = suspensionEnd;
	}

	public String getEmailMain()
	{
		return emailMain;
	}

	public void setEmailMain(String emailMain)
	{
		this.emailMain = emailMain;
	}

	public String getEmailIll()
	{
		return emailIll;
	}

	public void setEmailIll(String emailIll)
	{
		this.emailIll = emailIll;
	}

	public String getPhoneMain()
	{
		return phoneMain;
	}

	public void setPhoneMain(String phoneMain)
	{
		this.phoneMain = phoneMain;
	}

	public String getPhoneIll()
	{
		return phoneIll;
	}

	public void setPhoneIll(String phoneIll)
	{
		this.phoneIll = phoneIll;
	}

	public List<ElasticSearchPartnerAddress> getAddresses()
	{
		return addresses;
	}

	public void setAddresses(List<ElasticSearchPartnerAddress> addresses)
	{
		this.addresses = addresses;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
		result = prime * result + ((emailIll == null) ? 0 : emailIll.hashCode());
		result = prime * result + ((emailMain == null) ? 0 : emailMain.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nuc == null) ? 0 : nuc.hashCode());
		result = prime * result + ((phoneIll == null) ? 0 : phoneIll.hashCode());
		result = prime * result + ((phoneMain == null) ? 0 : phoneMain.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((suspensionEnd == null) ? 0 : suspensionEnd.hashCode());
		result = prime * result + ((suspensionStart == null) ? 0 : suspensionStart.hashCode());
		result = prime * result + ((updated == null) ? 0 : updated.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElasticSearchPartner other = (ElasticSearchPartner) obj;
		if (addresses == null)
		{
			if (other.addresses != null)
				return false;
		}
		else if (!addresses.equals(other.addresses))
			return false;
		if (emailIll == null)
		{
			if (other.emailIll != null)
				return false;
		}
		else if (!emailIll.equals(other.emailIll))
			return false;
		if (emailMain == null)
		{
			if (other.emailMain != null)
				return false;
		}
		else if (!emailMain.equals(other.emailMain))
			return false;
		if (enabled != other.enabled)
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (nuc == null)
		{
			if (other.nuc != null)
				return false;
		}
		else if (!nuc.equals(other.nuc))
			return false;
		if (phoneIll == null)
		{
			if (other.phoneIll != null)
				return false;
		}
		else if (!phoneIll.equals(other.phoneIll))
			return false;
		if (phoneMain == null)
		{
			if (other.phoneMain != null)
				return false;
		}
		else if (!phoneMain.equals(other.phoneMain))
			return false;
		if (status == null)
		{
			if (other.status != null)
				return false;
		}
		else if (!status.equals(other.status))
			return false;
		if (suspensionEnd == null)
		{
			if (other.suspensionEnd != null)
				return false;
		}
		else if (!suspensionEnd.equals(other.suspensionEnd))
			return false;
		if (suspensionStart == null)
		{
			if (other.suspensionStart != null)
				return false;
		}
		else if (!suspensionStart.equals(other.suspensionStart))
			return false;
		if (updated == null)
		{
			if (other.updated != null)
				return false;
		}
		else if (!updated.equals(other.updated))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "ElasticSearchPartner [nuc=" + nuc + ", updated=" + updated + ", name=" + name + ", enabled=" + enabled +
		       ", status=" + status + ", suspensionStart=" + suspensionStart + ", suspensionEnd=" + suspensionEnd +
		       ", emailMain=" + emailMain + ", emailIll=" + emailIll + ", phoneMain=" + phoneMain + ", phoneIll=" +
		       phoneIll + ", addresses=" + addresses + "]";
	}
}
