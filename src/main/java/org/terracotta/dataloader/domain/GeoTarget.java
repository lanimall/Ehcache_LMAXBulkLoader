package org.terracotta.dataloader.domain;

import java.io.Serializable;

/**
 * @author Fabien Sanglier
 * 
 */
public final class GeoTarget implements Serializable
{

    private static final long serialVersionUID = -2317230442904424261L;

    public long id;
    public String name;
    public double longitude;
    public double latitude;

    public GeoTarget()
    {
    }

    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof GeoTarget))
        {
            return false;
        }
        if (id != ((GeoTarget) o).id)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("Target{");
        sb.append("id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append('}');
        return sb.toString();
    }
}
