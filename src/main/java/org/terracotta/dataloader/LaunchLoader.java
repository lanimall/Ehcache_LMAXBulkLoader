package org.terracotta.dataloader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import net.sf.ehcache.Cache;

import org.terracotta.dataloader.domain.GeoTarget;
import org.terracotta.dataloader.utils.CacheUtils;
import org.terracotta.dataloader.utils.RandomUtil;

import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;

/**
 */
public class LaunchLoader
{
	public static final long maxEntriesDefault = 10000000;
	public static final double[] bboxDefault = new double[] {-180,-90,180,90};
	private static final Pattern m_patternComma = Pattern.compile(",");
	public static final String CACHE = "GeoTarget";

	public static void main(final String[] args) throws Exception
	{
		long maxEntries = maxEntriesDefault;
		double[] bbox = bboxDefault;
		if (args.length > 0)
		{
			try {
				maxEntries = Long.parseLong(args[0]);
			} catch (NumberFormatException nfe) {
				maxEntries = maxEntriesDefault;
			}

			if (args.length > 1){
				try {
					bbox = parseBoundingBox(args[1]);
				} catch (Exception e) {
					bbox = bboxDefault;
				}
			}
		}

		final Cache cache = CacheUtils.getCache(CACHE);
		try
		{
			if(cache.isTerracottaClustered())
				cache.setNodeBulkLoadEnabled(true);
			
			bulkLoad(cache,maxEntries,bbox);
		}
		finally
		{
			if(cache.isTerracottaClustered()){
				cache.setNodeBulkLoadEnabled(false);
				cache.waitUntilClusterBulkLoadComplete();
			}
		}
	}

	private static void bulkLoad(
			final Cache cache, long entriesToLoad,double[] bbox)
	{
		final AtomicLong atomicLong = new AtomicLong();
		final ValueHandler[] valueHandlers = new ValueHandler[]{
				new ValueHandler(cache, atomicLong),
				new ValueHandler(cache, atomicLong),
				new ValueHandler(cache, atomicLong),
				new ValueHandler(cache, atomicLong)
		};

		final ExecutorService executorService = Executors.newCachedThreadPool();
		try
		{
			final RingBuffer<ValueEvent> ringBuffer =
					RingBuffer.createSingleProducer(ValueEvent.EVENT_FACTORY,
							4096,
							new YieldingWaitStrategy());

			final WorkerPool<ValueEvent> workerPool =
					new WorkerPool<ValueEvent>(ringBuffer,
							ringBuffer.newBarrier(),
							new FatalExceptionHandler(),
							valueHandlers);

			workerPool.start(executorService);
			try
			{
				publishLoadEvents(ringBuffer,entriesToLoad,bbox);
			}
			finally
			{
				workerPool.drainAndHalt();
			}
		}
		finally
		{
			executorService.shutdown();
		}
		System.out.format("Put %d elements.", atomicLong.get());
	}

	private static void publishLoadEvents(final RingBuffer<ValueEvent> ringBuffer, long entriesCountToLoad, double[] bbox)
	{
		if(bbox == null)
			throw new IllegalArgumentException("bbox cannot be null");

		RandomUtil rdm = new RandomUtil(System.nanoTime());
		for(long i=0;i<entriesCountToLoad; i++){
			//generate random lat and long within the provided bbox
			double latitude = rdm.generateRandomDouble(bbox[BBoxIndex.ymin.index],bbox[BBoxIndex.ymax.index]);
			double longitude = rdm.generateRandomDouble(bbox[BBoxIndex.xmin.index],bbox[BBoxIndex.xmax.index]);

			//create the domain object with above generate random coordinates
			GeoTarget target = new GeoTarget();
			target.setLatitude(latitude);
			target.setLongitude(longitude);
			target.setName(rdm.generateRandomText(15));

			//publish the object to the multithreaded engine
			final long sequence = ringBuffer.next();
			ringBuffer.get(sequence).setValue(target);
			ringBuffer.publish(sequence);
		}
		System.out.println("Published all objects to process!");
	}

	public enum BBoxIndex {
		xmin(0),
		ymin(1),
		xmax(2),
		ymax(3);

		private final int index;
		private BBoxIndex(int index) {
			this.index = index;
		}
	}

	private static double[] parseBoundingBox(String commaSeparatedBBox) throws Exception {
		double[] bbox = null;
		double xmin = -1, ymin = -1, xmax = 1, ymax = 1;
		double cell = 1.0;
		if (commaSeparatedBBox != null && commaSeparatedBBox.length() > 0)
		{
			final String[] tokens = m_patternComma.split(commaSeparatedBBox);
			if (tokens.length == 4)
			{
				xmin = Double.parseDouble(tokens[0]);
				ymin = Double.parseDouble(tokens[1]);
				xmax = Double.parseDouble(tokens[2]);
				ymax = Double.parseDouble(tokens[3]);
			}
			else
			{
				throw new IllegalArgumentException("bbox is not in the form xmin,ymin,xmax,ymax");
			}
			bbox = new double[] {xmin,ymin,xmax,ymax};
		}

		return bbox;
	}
}
