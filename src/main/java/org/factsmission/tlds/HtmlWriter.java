/*
 * The MIT License
 *
 * Copyright 2017 user.
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
import java.io.OutputStreamWriter;
import java.io.Writer;
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
public class HtmlWriter implements MessageBodyWriter<GraphNode> {
    
    final Serializer serializer = Serializer.getInstance();
    
    final String embeddedRdfFormat = "text/turtle";
    
    final String htmlBeforeRDF = "<!DOCTYPE html>\n" +
"<html class=\"render\" resource=\"\" context=\"http://zz2h.zazukoians.org/modes/FullPage\">\n" +
"    <head>\n" +
"        <title>This will be replaced when the data is loaded</title>\n" +
"        <meta charset=\"UTF-8\">\n" +
"        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
"        <link rel=\"matchers\" href=\"https://cdn.rawgit.com/zazukoians/trifid-ld/cd9f5d26/data/public/rdf2h/matchers.ttl\" type=\"text/turtle\" />\n" +
"        <script src=\"https://cdn.rawgit.com/rdf2h/rdf2h/v0.3.0/dist/rdf-ext.js\"></script>\n" +
"        <script src=\"https://cdn.rawgit.com/rdf2h/rdf2h/v0.3.0/dist/rdf2h.js\"></script>\n" +
"        <script src=\"https://cdn.rawgit.com/retog/rdf-parser-n3-browser/v0.3.0b/dist/n3-parser.js\"></script>\n" +
"        <script src=\"https://code.jquery.com/jquery-2.1.4.min.js\"></script>\n" +
"        <script src=\"https://cdn.rawgit.com/rdf2h/ld2h/v0.4.4/dist/ld2h.js\"></script>\n" +
"<script id=\"data\" type=\""+embeddedRdfFormat+"\">";

    final String htmlAfterRDF = "</script>\n" +
"            \n" +
"        <script type=\"text/javascript\">\n" +
"$(function () {\n" +
"        LD2h.expand().then(function() { \n" +
"            console.log(\"finsihed expanding\");\n" +
"        });\n" +
"});\n" +
"        </script>\n" +
"    </head>\n" +
"    <body>\n" +
"This will be replaced by rendered RDF.\n" +
"    </body>\n" +
"</html>";
    
    HtmlWriter(GraphNode config) {
        //this.config 
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, 
            Annotation[] annotations, MediaType mediaType) {
        return GraphNode.class.isAssignableFrom(type) && MediaType.TEXT_HTML_TYPE.isCompatible(mediaType);
    }

    @Override
    public long getSize(GraphNode t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(GraphNode t, Class<?> type, Type genericType, 
            Annotation[] annotations, MediaType mediaType, 
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(htmlBeforeRDF.getBytes("utf-8"));
        serializer.serialize(entityStream, getGraph(t), embeddedRdfFormat);
        entityStream.write(htmlAfterRDF.getBytes("utf-8"));
        entityStream.flush();
    }

    protected Graph getGraph(GraphNode t) {
        //TODO version that return context plus recurisvely the context of 
        // URIs in the context that differ only by their hash tag
        return t.getGraph();
    }
    
}
