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
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.utils.GraphNode;


/**
 *
 * @author user
 */
@Provider
@Produces("text/html")
public class HtmlWriter implements MessageBodyWriter<Graph> {
    

    final Serializer serializer = Serializer.getInstance();

    final String embeddedRdfFormat = "text/turtle";

    //TODO change rawgit URI with production URI rdf2h/matchers
    final String htmlBeforeMatcherURI
            = "<!DOCTYPE html>\n"
            + "<html class=\"render\" resource=\"\" context=\"http://rdf2h.github.io/2016/generic-rdf2h-renderers/FullPage\">\n"
            + "    <head>\n"
            + "        <title>This will be replaced when the data is loaded</title>\n"
            + "        <meta charset=\"UTF-8\">\n"
            + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
            + "        <link rel=\"renderers\" href=\"https://rawgit.com/rdf2h/renderers/master/fallback-renderers.ttl\" type=\"text/turtle\" />\n"
            + "        <link rel=\"renderers\" href=\"";

    final String htmlAfterMatcherURI
            = "\" type=\"text/turtle\" />\n"
            + "        <script src=\"https://code.jquery.com/jquery-2.1.4.min.js\"></script>\n"
            + "        <script src=\"https://rdf2h.github.io/ld2h/latest/ld2h.js\"></script>\n"
            + "        <script id=\"data\" type=\"" + embeddedRdfFormat + "\">";

    final String htmlAfterRDF = "</script>\n"
            + "            \n"
            + "    </head>\n"
            + "    <body>\n"
            + "This will be replaced by rendered RDF.\n"
            + "        <script type=\"text/javascript\">\n"
            + "        LD2h.expand().then(function() { \n"
            + "            console.log(\"finsihed expanding\");\n"
            + "        });\n"
            + "        </script>\n"
            + "    </body>\n"
            + "</html>";

    HtmlWriter(GraphNode config) {
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return Graph.class.isAssignableFrom(type) && MediaType.TEXT_HTML_TYPE.isCompatible(mediaType);
    }

    @Override
    public long getSize(Graph t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Graph t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(htmlBeforeMatcherURI.getBytes("utf-8"));
        entityStream.write("/renderers.ttl".getBytes("utf-8"));
        entityStream.write(htmlAfterMatcherURI.getBytes("utf-8"));
        serializer.serialize(entityStream, t, embeddedRdfFormat);
        entityStream.write(htmlAfterRDF.getBytes("utf-8"));
        entityStream.flush();
    }

}
