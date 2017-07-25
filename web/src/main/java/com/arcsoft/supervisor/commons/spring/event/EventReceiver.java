package com.arcsoft.supervisor.commons.spring.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Event receiver.
 * 
 * @author fjli
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EventReceiver {

	/**
	 * Indicate which event will be received.
	 */
	Class<?> value();

	/**
	 * Indicate the event is delivered sync or not. The default value is false.
	 */
	boolean sync() default false;

}
