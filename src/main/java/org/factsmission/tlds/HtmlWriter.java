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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.clerezza.commons.rdf.BlankNode;
import org.apache.clerezza.commons.rdf.BlankNodeOrIRI;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.Literal;
import org.apache.clerezza.commons.rdf.RDFTerm;
import org.apache.clerezza.commons.rdf.Triple;
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

    final String embeddedRdfFormat = "text/html";

    final String htmlBeforeMatcherURI
            = "<!DOCTYPE html>\n"
            + "<html class=\"render\" resource=\"\" context=\"http://rdf2h.github.io/2016/generic-rdf2h-renderers/FullPage\">\n"
            + "    <head>\n"
            + "        <title>This will be replaced when the data is loaded</title>\n"
            + "        <meta charset=\"UTF-8\">\n"
            + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n";

    final String htmlAfterMatcherURI
            = "        <script src=\"https://code.jquery.com/jquery-2.1.4.min.js\"></script>\n"
            + "        <script src=\"https://retog.github.io/ext-rdflib/0.3.5/rdf.js\"></script>\n"
            + "        <script src=\"https://rdf2h.github.io/ld2h/2.2.0/ld2h.js\"></script>\n"
            + "    </head>\n"
            + "    <body>\n"
            + "        <div id=\"data\" type=\"" + embeddedRdfFormat + "\">";

    final String htmlAfterRDF = "</div>\n"
            + "            \n"
            + "This will be replaced by rendered RDF.\n"
            + "        <script type=\"text/javascript\">\n" +
            "try {\n" +
            "    LD2h.expand().then(function () {\n" +
            "        console.log(\"finished expanding\");\n" +
            "    });\n" +
            "} catch (e) {\n" +
            "    navigator.sayswho = (function () {\n" +
            "        var ua = navigator.userAgent, tem,\n" +
            "            M = ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\\/))\\/?\\s*(\\d+)/i) || [];\n" +
            "        if (/trident/i.test(M[1])) {\n" +
            "            tem = /\\brv[ :]+(\\d+)/g.exec(ua) || [];\n" +
            "            return 'IE ' + (tem[1] || '');\n" +
            "        }\n" +
            "        if (M[1] === 'Chrome') {\n" +
            "            tem = ua.match(/\\b(OPR|Edge)\\/(\\d+)/);\n" +
            "            if (tem != null) return tem.slice(1).join(' ').replace('OPR', 'Opera');\n" +
            "        }\n" +
            "        M = M[2] ? [M[1], M[2]] : [navigator.appName, navigator.appVersion, '-?'];\n" +
            "        if ((tem = ua.match(/version\\/(\\d+)/i)) != null) M.splice(1, 1, tem[1]);\n" +
            "        return M.join(' ');\n" +
            "    })();\n" +
            "    var div = document.createElement(\"div\");\n" +
            "    var host = window.location.hostname;\n" +
            "    var client = navigator.sayswho;\n" +
            "    div.innerHTML = \"<p>We at \" +\n" +
            "        host +\n" +
            "        \" value diversity and try to be as inclusive as possible by endorsing open standards to support all kind of devices and be accessible to people with different abilities.\" +\n" +
            "        \"</p><p> However it seems that we're experiencing a problem rendering this page correctly with \" +\n" +
            "        client +\n" +
            "        \". The software running this site is fully open source, maybe you could help improving it to support your browser as well? Check out the \" +\n" +
            "        \"<a href='https://github.com/linked-solutions/tlds'>TLDS</a>\" +\n" +
            "        \" project.</p>\";\n" +
            "    div.style.backgroundColor = \"#ffbbbb\";\n" +
            "    div.style.margin = \"3px\";\n" +
            "    div.style.border = \"2px solid grey\";\n" +
            "    div.style.padding = \"15px\";\n" +
            "    div.style.fontSize = \"1.2em\";\n" +
            "    document.body.insertBefore(div, document.body.firstChild);\n" +
            "}"
            + "        </script>\n"
            + "    </body>\n"
            + "</html>";

    private GraphNode config;
    private List<String> renderers = new ArrayList<>();

    HtmlWriter(GraphNode config) {
        this.config = config;
        Iterator<GraphNode> renderersListIter = config.getObjectNodes(TLDS.renderers);
        if (renderersListIter.hasNext()) {
            while (renderersListIter.hasNext()) {
                List<RDFTerm> list = renderersListIter.next().asList();
                for (RDFTerm term : list) {
                    if (!(term instanceof Literal)) {
                        throw new RuntimeException();
                    }
                    renderers.add(((Literal)term).getLexicalForm());
                }
            }
        }
        serializer.bindSerializingProvider(new RDFaSerializer());
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
    public void writeTo(Graph graph, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Writer entityWriter = new OutputStreamWriter(entityStream, "utf-8");
        entityWriter.write(htmlBeforeMatcherURI);
        for (String renderer : renderers) { 
            entityWriter.write("        <link rel=\"renderers\" href=\""+renderer+"\" type=\"text/turtle\" />\n");
        }
        entityWriter.write(htmlAfterMatcherURI);
        entityWriter.flush();
        serializer.serialize(entityStream, graph, embeddedRdfFormat);
        entityWriter.write(htmlAfterRDF);
        entityWriter.flush();
    }


}
