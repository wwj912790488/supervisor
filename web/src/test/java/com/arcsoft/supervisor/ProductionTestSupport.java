package com.arcsoft.supervisor;



import com.arcsoft.supervisor.utils.app.Environment;
import org.junit.BeforeClass;

/**
 * @author zw.
 */
public class ProductionTestSupport extends BaseWebTestSupport {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("spring.profiles.active", Environment.Profiler.STR_PRODUCTION);
    }

}
