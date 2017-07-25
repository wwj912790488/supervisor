package com.arcsoft.supervisor.commons.spring;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.utils.ClassUtils;
import com.arcsoft.supervisor.utils.app.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A processor implementation for exclude specific entity classes with annotation.
 *
 * @author zw.
 */
public class ProfileScanningPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileScanningPersistenceUnitPostProcessor.class);

    private static final Map<Class<? extends Annotation>, String> EXCLUDE_PROFILE_MAPS = new HashMap<>();

    static {
        // Add all of profile annotation map
        EXCLUDE_PROFILE_MAPS.put(Production.class, Environment.Profiler.STR_PRODUCTION);
        EXCLUDE_PROFILE_MAPS.put(Sartf.class, Environment.Profiler.STR_SARTF);


        Class<? extends Annotation> activeAnnotationCls = null;
        for (Map.Entry<Class<? extends Annotation>, String> e : EXCLUDE_PROFILE_MAPS.entrySet()) {
            if (Environment.getProfiler().hasProfile(e.getValue())) {
                // We only enable single profile at the same time
                activeAnnotationCls = e.getKey();
                break;
            }
        }
        // Remove currently active profile
        if (activeAnnotationCls != null) {
            EXCLUDE_PROFILE_MAPS.remove(activeAnnotationCls);
        } else {
            EXCLUDE_PROFILE_MAPS.clear();
        }
    }


    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        List<String> managedClassNames = pui.getManagedClassNames();
        for (Iterator<String> it = managedClassNames.iterator(); it.hasNext();) {
            String managedClassName = it.next();
            Class cls = ClassUtils.forName(managedClassName, false);
            if (cls != null) {
                Annotation[] annotations = cls.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (EXCLUDE_PROFILE_MAPS.containsKey(annotation.annotationType())) {
                        LOG.info("Remove domain class: " + managedClassName);
                        it.remove();
                    }
                }
            }
        }
    }
}
