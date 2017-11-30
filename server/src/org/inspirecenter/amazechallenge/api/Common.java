package org.inspirecenter.amazechallenge.api;

import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.Parameter;

public class Common {

    static boolean checkMagic(final String magic) {
        final Parameter parameter = ObjectifyService.ofy().load().type(Parameter.class).filter("name", "magic").first().now();
        return parameter != null && parameter.value.equals(magic);
    }
}