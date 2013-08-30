package org.terracotta.dataloader;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.terracotta.dataloader.domain.GeoTarget;

import com.lmax.disruptor.WorkHandler;

/**
 * @author Fabien Sanglier
 * 
 */
public final class ValueHandler implements WorkHandler<ValueEvent>
{
	private Cache m_cache;
	private AtomicLong m_atomicLong;

	public ValueHandler(final Cache cache,final AtomicLong atomicLong)
	{
		m_cache = cache;
		m_atomicLong = atomicLong;
	}

	@Override
	public void onEvent(final ValueEvent valueEvent) throws Exception
	{
		final GeoTarget objValue = valueEvent.getValue();
		if(null != objValue){
			objValue.setId(m_atomicLong.incrementAndGet());
			m_cache.putQuiet(new Element(objValue.getId(), objValue));
		}
	}
}
