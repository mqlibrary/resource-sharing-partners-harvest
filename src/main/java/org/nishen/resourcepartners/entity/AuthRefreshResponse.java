package org.nishen.resourcepartners.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthRefreshResponse implements Serializable
{
	private static final long serialVersionUID = -3109431230519244513L;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("scope")
	private String scope;

	@JsonProperty("expires_in")
	private long expiresIn;

	@JsonProperty("ext_expires_in")
	private long extExpiresIn;

	@JsonProperty("expires_on")
	private long expiresOn;

	@JsonProperty("not_before")
	private long notBefore;

	@JsonProperty("resource")
	private String resource;

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("refresh_token")
	private String refreshToken;

	@JsonProperty("id_token")
	private String idToken;

	public AuthRefreshResponse()
	{}

	public String getTokenType()
	{
		return tokenType;
	}

	public void setTokenType(String tokenType)
	{
		this.tokenType = tokenType;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}

	public long getExpiresIn()
	{
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn)
	{
		this.expiresIn = expiresIn;
	}

	public long getExtExpiresIn()
	{
		return extExpiresIn;
	}

	public void setExtExpiresIn(long extExpiresIn)
	{
		this.extExpiresIn = extExpiresIn;
	}

	public long getExpiresOn()
	{
		return expiresOn;
	}

	public void setExpiresOn(long expiresOn)
	{
		this.expiresOn = expiresOn;
	}

	public long getNotBefore()
	{
		return notBefore;
	}

	public void setNotBefore(long notBefore)
	{
		this.notBefore = notBefore;
	}

	public String getResource()
	{
		return resource;
	}

	public void setResource(String resource)
	{
		this.resource = resource;
	}

	public String getAccessToken()
	{
		return accessToken;
	}

	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}

	public String getRefreshToken()
	{
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken)
	{
		this.refreshToken = refreshToken;
	}

	public String getIdToken()
	{
		return idToken;
	}

	public void setIdToken(String idToken)
	{
		this.idToken = idToken;
	}
}
