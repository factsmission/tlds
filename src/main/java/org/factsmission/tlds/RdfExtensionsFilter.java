/*
 * The MIT License
 *
 * Copyright 2017 FactsMission AG, Switzerland.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.factsmission.tlds;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriBuilder;

@PreMatching
public class RdfExtensionsFilter implements ContainerRequestFilter {

    Map<String, String> extension2type = new HashMap<>();

    {
        extension2type.put("ttl", "text/turtle");
        extension2type.put("nt", "application/n-triples");
        extension2type.put("rdf", "application/rdf+xml");
        extension2type.put("jsonld", "application/rdf+json");
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        int dotPos = path.lastIndexOf('.');
        if (dotPos == -1) {
            return;
        }
        String extension = path.substring(dotPos + 1);
        if (extension2type.containsKey(extension)) {
            requestContext.getHeaders().remove("Accept");
            requestContext.getHeaders().add("Accept", extension2type.get(extension));
            UriBuilder uriBuilder = requestContext.getUriInfo().getRequestUriBuilder();
            uriBuilder.replacePath(path.substring(0, dotPos));
            requestContext.setRequestUri(uriBuilder.build());
        }
    }

}
