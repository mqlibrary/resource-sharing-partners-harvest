package org.nishen.resourcepartners.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "partner")
@JsonPropertyOrder({ "nuc", "updated", "name", "enabled", "isoIll", "status", "emailMain", "emailIll", "phoneMain",
                     "phoneIll", "phoneFax", "suspensions", "addresses" })
public class ResourcePartner implements BaseEntity, Serializable
{
	@JsonIgnore
	private static final long serialVersionUID = 2100949073844029059L;

	@JsonProperty("nuc")
	private String nuc;

	@JsonProperty("updated")
	private String updated;

	@JsonProperty("name")
	private String name;

	@JsonProperty("enabled")
	private boolean enabled;

	@JsonProperty("iso_ill")
	private boolean isoIll;

	@JsonProperty("status")
	private String status;

	@JsonProperty("email_main")
	private String emailMain;

	@JsonProperty("email_ill")
	private String emailIll;

	@JsonProperty("phone_main")
	private String phoneMain;

	@JsonProperty("phone_ill")
	private String phoneIll;

	@JsonProperty("phone_fax")
	private String phoneFax;

	@JsonProperty("suspensions")
	private SortedSet<ResourcePartnerSuspension> suspensions = new TreeSet<>();

	@JsonProperty("addresses")
	private List<ResourcePartnerAddress> addresses = new ArrayList<>();

	@JsonIgnore
	@Override
	public String getEntityId()
	{
		return nuc;
	}

	@JsonIgnore
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

	public boolean isIsoIll()
	{
		return isoIll;
	}

	public void setIsoIll(boolean isoIll)
	{
		this.isoIll = isoIll;
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

	public SortedSet<ResourcePartnerSuspension> getSuspensions()
	{
		return suspensions;
	}

	public void setSuspensions(SortedSet<ResourcePartnerSuspension> suspensions)
	{
		this.suspensions = suspensions;
	}

	public List<ResourcePartnerAddress> getAddresses()
	{
		return addresses;
	}

	public void setAddresses(List<ResourcePartnerAddress> addresses)
	{
		this.addresses = addresses;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(addresses, emailIll, emailMain, enabled, isoIll, name, nuc, phoneFax, phoneIll, phoneMain,
		                    status, suspensions, updated);
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
		ResourcePartner other = (ResourcePartner) obj;
		return Objects.equals(addresses, other.addresses) && Objects.equals(emailIll, other.emailIll) &&
		       Objects.equals(emailMain, other.emailMain) && enabled == other.enabled && isoIll == other.isoIll &&
		       Objects.equals(name, other.name) && Objects.equals(nuc, other.nuc) &&
		       Objects.equals(phoneFax, other.phoneFax) && Objects.equals(phoneIll, other.phoneIll) &&
		       Objects.equals(phoneMain, other.phoneMain) && Objects.equals(status, other.status) &&
		       Objects.equals(suspensions, other.suspensions) && Objects.equals(updated, other.updated);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ResourcePartner [nuc=");
		builder.append(nuc);
		builder.append(", updated=");
		builder.append(updated);
		builder.append(", name=");
		builder.append(name);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", isoIll=");
		builder.append(isoIll);
		builder.append(", status=");
		builder.append(status);
		builder.append(", emailMain=");
		builder.append(emailMain);
		builder.append(", emailIll=");
		builder.append(emailIll);
		builder.append(", phoneMain=");
		builder.append(phoneMain);
		builder.append(", phoneIll=");
		builder.append(phoneIll);
		builder.append(", phoneFax=");
		builder.append(phoneFax);
		builder.append(", suspensions=");
		builder.append(suspensions);
		builder.append(", addresses=");
		builder.append(addresses);
		builder.append("]");
		return builder.toString();
	}
}
