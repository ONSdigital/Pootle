package com.github.onsdigital.pootle;

import com.github.davidcarboni.restolino.framework.Home;
import com.github.onsdigital.pootle.api.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Example home ("/") implementation.
 * Take a look at the {@code com.github.davidcarboni.restolino.framework} package for more useful interfaces/classes.
 */
public class Default implements Home {

    @Override
    public String get(HttpServletRequest req, HttpServletResponse res) throws IOException {
        return "Please browse to: /" + Example.class.getSimpleName().toLowerCase();
    }
}
