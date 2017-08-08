package org.nishen.resourcepartners.entity;

import java.util.Date;

public class ElasticSearchChangeRecord implements ElasticSearchEntity
{
	private Date time;

	private String nuc;

	private String field;

	private String before;

	private String after;

	public ElasticSearchChangeRecord()
	{
		time = new Date();
	}

	public ElasticSearchChangeRecord(String nuc, String field, String before, String after)
	{
		super();
		this.nuc = nuc;
		this.field = field;
		this.before = before;
		this.after = after;
	}

	@Override
	public String getElasticSearchId()
	{
		return null;
	}

	@Override
	public String getElasticSearchIndex()
	{
		return "partner-changelog";
	}

	@Override
	public String getElasticSearchType()
	{
		return "partner-change";
	}

	@Override
	public Date getTime()
	{
		return time;
	}

	public String getNuc()
	{
		return nuc;
	}

	public void setNuc(String nuc)
	{
		this.nuc = nuc;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public String getBefore()
	{
		return before;
	}

	public void setBefore(String before)
	{
		this.before = before;
	}

	public String getAfter()
	{
		return after;
	}

	public void setAfter(String after)
	{
		this.after = after;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((after == null) ? 0 : after.hashCode());
		result = prime * result + ((before == null) ? 0 : before.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((nuc == null) ? 0 : nuc.hashCode());
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
		ElasticSearchChangeRecord other = (ElasticSearchChangeRecord) obj;
		if (after == null)
		{
			if (other.after != null)
				return false;
		}
		else if (!after.equals(other.after))
			return false;
		if (before == null)
		{
			if (other.before != null)
				return false;
		}
		else if (!before.equals(other.before))
			return false;
		if (field == null)
		{
			if (other.field != null)
				return false;
		}
		else if (!field.equals(other.field))
			return false;
		if (nuc == null)
		{
			if (other.nuc != null)
				return false;
		}
		else if (!nuc.equals(other.nuc))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ChangeRecord [nuc=");
		builder.append(nuc);
		builder.append(", field=");
		builder.append(field);
		builder.append(", before=");
		builder.append(before);
		builder.append(", after=");
		builder.append(after);
		builder.append("]");
		return builder.toString();
	}
}
