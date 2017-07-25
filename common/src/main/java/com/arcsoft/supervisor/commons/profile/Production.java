package com.arcsoft.supervisor.commons.profile;

import com.arcsoft.supervisor.utils.app.Environment;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for {@code Production} profile.
 *
 * @author zw.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Profile(Environment.Profiler.STR_PRODUCTION)
public @interface Production {
}
