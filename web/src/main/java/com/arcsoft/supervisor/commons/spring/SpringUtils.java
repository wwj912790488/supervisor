package com.arcsoft.supervisor.commons.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring utilities.
 * 
 * @author fjli
 * @author zw
 */
public class SpringUtils {

	/**
	 * Find all methods with the annotation in the specified bean.
	 * 
	 * @param context - the spring application context
	 * @param beanName - the bean name
	 * @param annotation - the annotation class
	 * @return the list of all found methods.
	 */
	public static List<Method> findMethodsWithAnnotation(ApplicationContext context, String beanName, Class<? extends Annotation> annotation) {
		Class<?> clazz = context.getType(beanName);
		if (clazz == null || Modifier.isAbstract(clazz.getModifiers()))
			return null;
		List<Method> list = new ArrayList<>();
		for (Method method : clazz.getMethods()) {
			Annotation receiver = method.getAnnotation(annotation);
			if (receiver != null)
				list.add(method);
		}
		return list;
	}

	/**
	 * Find all methods with the annotation in all defined beans.
	 * 
	 * @param context - the spring application context
	 * @param annotation - the annotation class
	 * @return the map of all found methods in all defined beans.
	 */
	public static Map<String, List<Method>> findAllMethodsWithAnnotation(ApplicationContext context, Class<? extends Annotation> annotation) {
		Map<String, List<Method>> map = new HashMap<>();
		String[] names = context.getBeanDefinitionNames();
		for (String name : names) {
			List<Method> methods = findMethodsWithAnnotation(context, name, annotation);
			if (methods != null && !methods.isEmpty())
				map.put(name, methods);
		}
		return map;
	}

    /**
     * Returns the {@code HttpServletRequest} currently bound to the thread of spring mvc.
     *
     * @return the {@code HttpServletRequest}
     */
    public static HttpServletRequest getThreadBoundedHttpServletRequest(){
        return ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
