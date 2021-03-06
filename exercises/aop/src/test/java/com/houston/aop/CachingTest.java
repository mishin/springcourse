package com.houston.aop;

import com.houston.aop.aspects.Counter;
import com.houston.aop.beans.EntryBean;
import com.houston.aop.beans.OtherBean;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Statistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/context.xml")
public class CachingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachingTest.class);

    @Autowired
    private EntryBean entryBean;

    @Autowired
    private OtherBean otherBean;

    @Autowired
    private EhCacheFactoryBean ehCacheFactoryBean;

    @Test
    public void loopAndCheckCacheHits() {
        int hitLimit = 9;
        for (int i = 0; i < hitLimit; i++) {
            entryBean.entryMethod("" + (i%3));
        }
        Ehcache cache = ehCacheFactoryBean.getObject();
        Statistics statistics = cache.getStatistics();
        long hits = statistics.getCacheHits();
        assertEquals("Hit counts should match", 3L, ((Counter)otherBean).getCount());
        assertEquals("there should be cache hits.", (hitLimit - 3), hits);
        long misses = statistics.getCacheMisses();
        assertEquals("there should be two cache miss", 3, misses);
    }

}
