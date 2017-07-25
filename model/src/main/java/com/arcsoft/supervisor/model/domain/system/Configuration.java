package com.arcsoft.supervisor.model.domain.system;

import javax.persistence.*;

/**
 * Abstract entity for <code>Configuration</code>.
 *
 * @author zw.
 */
@Entity
@Table(name = "configuration")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("1")
public abstract class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
