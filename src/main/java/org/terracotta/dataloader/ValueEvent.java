package org.terracotta.dataloader;

import org.terracotta.dataloader.domain.GeoTarget;

import com.lmax.disruptor.EventFactory;

/**
 * @author Fabien Sanglier
 * 
 */
public final class ValueEvent
{
    private GeoTarget value;

	public GeoTarget getValue() {
		return value;
	}

	public void setValue(GeoTarget value) {
		this.value = value;
	}
	
	public final static EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>()
    {
        public ValueEvent newInstance()
        {
            return new ValueEvent();
        }
    };
}
