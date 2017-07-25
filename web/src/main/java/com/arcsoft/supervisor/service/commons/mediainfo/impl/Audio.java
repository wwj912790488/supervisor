package com.arcsoft.supervisor.service.commons.mediainfo.impl;

/**
 * A bean class to holding the <code>id</code> and <code>name</code> of <code>Audio</code>.
 *
 * @author zw.
 */
public class Audio {

    private final Integer id;
    private final String name;

    public Audio(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
