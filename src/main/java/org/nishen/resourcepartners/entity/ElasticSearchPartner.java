package org.nishen.resourcepartners.entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "partner")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "nuc", "updated", "name", "enabled", "status", "suspensionStart", "suspensionEnd", "emailMain",
                       "emailIll", "phoneMain", "phoneIll", "phoneFax", "suspensions", "addresses" })
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

	@XmlElement(name = "email_main")
	private String emailMain;

	@XmlElement(name = "email_ill")
	private String emailIll;

	@XmlElement(name = "phone_main")
	private String phoneMain;

	@XmlElement(name = "phone_ill")
	private String phoneIll;

	@XmlElement(name = "phone_fax")
	private String phoneFax;

	@XmlElement(name = "suspensions")
	private Set<ElasticSearchSuspension> suspensions = new LinkedHashSet<ElasticSearchSuspension>();

	@XmlElement(name = "addresses")
	private List<ElasticSearchPartnerAddress> addresses = new ArrayList<ElasticSearchPartnerAddress>();

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

	public String getPhoneFax()
	{
		return phoneFax;
	}

	public void setPhoneFax(String phoneFax)
	{
		this.phoneFax = phoneFax;
	}

	public Set<ElasticSearchSuspension> getSuspensions()
	{
		return suspensions;
	}

	public void setSuspensions(Set<ElasticSearchSuspension> suspensions)
	{
		this.suspensions = suspensions;
	}

	public List<ElasticSearchPartnerAddress> getAddresses()
	{
		return addresses;
	}

	public void setAddresses(List<ElasticSearchPartnerAddress> addresses)
	{
		this.addresses = addresses;
	}

}
