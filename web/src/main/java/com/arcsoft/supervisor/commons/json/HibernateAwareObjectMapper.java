package com.arcsoft.supervisor.commons.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * A extended {@link com.fasterxml.jackson.databind.ObjectMapper} to
 * config serialize behaviour for domain to json.
 */
public class HibernateAwareObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = -2096486925712172683L;

    public HibernateAwareObjectMapper() {
        Hibernate4Module module = new Hibernate4Module();
        module.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
        module.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        registerModule(module);
    }

}
