package org.inspirecenter.amazechallenge.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Parameter {

    @Id public Long id;
    @Index public String name;
    public String value;

    public Parameter() {
        super();
    }

    public Parameter(final String name, final String value) {
        this();
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}