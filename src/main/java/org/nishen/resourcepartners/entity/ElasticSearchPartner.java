package org.nishen.resourcepartners.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "partner")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "dataSource", "updated", "nuc", "status", "addresses" })
public class ElasticSearchPartner implements ElasticSearchEntity
{
	@XmlTransient
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

	@XmlElement(name = "data_source")
	private String dataSource;

	@XmlElement(name = "updated")
	private String updated;

	@XmlElement(name = "nuc")
	private String nuc;

	@XmlElement(name = "status")
	private String status;

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
	public Date getTime()
	{
		return new Date();
	}

	public String getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}

	public String getUpdated()
	{
		return sdf.format(updated);
	}

	public void setUpdated(String updated)
	{
		this.updated = updated;
	}

	public String getNuc()
	{
		return nuc;
	}

	public void setNuc(String nuc)
	{
		this.nuc = nuc;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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
		result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
		result = prime * result + ((nuc == null) ? 0 : nuc.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		if (dataSource == null)
		{
			if (other.dataSource != null)
				return false;
		}
		else if (!dataSource.equals(other.dataSource))
			return false;
		if (nuc == null)
		{
			if (other.nuc != null)
				return false;
		}
		else if (!nuc.equals(other.nuc))
			return false;
		if (status == null)
		{
			if (other.status != null)
				return false;
		}
		else if (!status.equals(other.status))
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
		return "ElasticSearchPartner [dataSource=" + dataSource + ", updated=" + updated + ", nuc=" + nuc +
		       ", status=" + status + ", addresses=" + addresses + "]";
	}
}
