package org.nishen.resourcepartners.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "partner-change")
@JsonPropertyOrder({ "time", "sourceSystem", "nuc", "field", "before", "after" })
public class ResourcePartnerChangeRecord implements BaseEntity, Serializable
{
	@JsonIgnore
	private static final long serialVersionUID = -1951723563951334021L;

	@JsonIgnore
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	@JsonIgnore
	private UUID id;

	@JsonProperty("time")
	private String time;

	@JsonProperty("source_system")
	private String sourceSystem;

	@JsonProperty("nuc")
	private String nuc;

	@JsonProperty("field")
	private String field;

	@JsonProperty("before")
	private String before;

	@JsonProperty("after")
	private String after;

	public ResourcePartnerChangeRecord()
	{
		id = UUID.randomUUID();
		time = sdf.format(new Date());
	}

	public ResourcePartnerChangeRecord(String sourceSystem, String nuc, String field, String before, String after)
	{
		this();
		this.sourceSystem = sourceSystem;
		this.nuc = nuc;
		this.field = field;
		this.before = before;
		this.after = after;
	}

	@Override
	public String getEntityId()
	{
		return id.toString();
	}

	@Override
	public String getTime()
	{
		return time;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getSourceSystem()
	{
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem)
	{
		this.sourceSystem = sourceSystem;
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

	public void setTime(String time)
	{
		this.time = time;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(after, before, field, id, nuc, sourceSystem, time);
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
		ResourcePartnerChangeRecord other = (ResourcePartnerChangeRecord) obj;
		return Objects.equals(after, other.after) && Objects.equals(before, other.before) &&
		       Objects.equals(field, other.field) && Objects.equals(id, other.id) && Objects.equals(nuc, other.nuc) &&
		       Objects.equals(sourceSystem, other.sourceSystem) && Objects.equals(time, other.time);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ResourcePartnerChangeRecord [id=");
		builder.append(id);
		builder.append(", time=");
		builder.append(time);
		builder.append(", sourceSystem=");
		builder.append(sourceSystem);
		builder.append(", nuc=");
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
