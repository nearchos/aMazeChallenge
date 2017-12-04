package org.inspirecenter.amazechallenge.api;

import com.googlecode.objectify.ObjectifyService;
import org.inspirecenter.amazechallenge.data.Parameter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Common {

    public static boolean checkMagic(final String magic) {
        final Parameter parameter = ObjectifyService.ofy().load().type(Parameter.class).filter("name", "magic").first().now();
        return parameter != null && parameter.value.equals(magic);
    }

    static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}