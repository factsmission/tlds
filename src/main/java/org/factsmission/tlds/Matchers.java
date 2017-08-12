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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.clerezza.commons.rdf.Graph;
import org.apache.clerezza.commons.rdf.IRI;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import solutions.linked.slds.ConfigUtils;
import solutions.linked.slds.util.IriTranslatorProvider;

/**
 *
 * @author noam
 */
@Path("matchers")
public class Matchers {
    
    public GraphNode config;
    private final ConfigUtils configUtils;
    private IriTranslatorProvider iriTranslatorProvider;
    
    Matchers(GraphNode config){
        this.config = config;
        configUtils = new ConfigUtils(config);
        iriTranslatorProvider = new IriTranslatorProvider(config);
    }

    @GET
    public Graph get(@Context HttpHeaders httpHeaders, @Context UriInfo uriInfo) throws IOException {
        final URI requestUri = uriInfo.getRequestUri();
        //The server unfirtunately takes the hostname and port from its config
        //The following fixes this
        final String hostHeader = httpHeaders.getRequestHeader("Host").get(0);
        final int hostHeaderSeparator = hostHeader.indexOf(':');
        final String host = hostHeaderSeparator > -1 ? 
                hostHeader.substring(0, hostHeaderSeparator)
                : hostHeader;
        final int port  = hostHeaderSeparator > -1 ?
                Integer.parseInt(hostHeader.substring(hostHeaderSeparator+1))
                : -1;
        final URI matchersGraphUri = UriBuilder.fromUri(requestUri).port(port).host(host).replacePath("/matchers").build();
        
        return getNamedGraph(matchersGraphUri);
    }
    
    @GET
    @Path("ttl")
    @Produces("text/turtle")
    public Graph getTurtle(@Context HttpHeaders httpHeaders, @Context UriInfo uriInfo) throws IOException {
        return get(httpHeaders, uriInfo);
    }

    private Graph getNamedGraph(URI matchersGraphUri) throws IOException{
        try (CloseableHttpClient httpClient = configUtils.createHttpClient()) {
            final HttpPost httpPost = new HttpPost(configUtils.getSparqlEndpointUri().getUnicodeString());
            IRI matchersGraphIri = new IRI(matchersGraphUri.toString());
            IRI translatedMatchersGraphIri = iriTranslatorProvider.getIriTranslator().reverse().translate(matchersGraphIri);
            String query = createQuery(translatedMatchersGraphIri);
            System.out.println(query);
            httpPost.setEntity(new StringEntity(query, ContentType.create("application/sparql-query", "utf-8")));
            System.out.println(System.currentTimeMillis());
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                System.out.println(System.currentTimeMillis());
                final StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() >= 400) {
                    throw new IOException("HTTP "+statusLine.getStatusCode()
                            +" "+statusLine.getReasonPhrase());
                }
                byte[] responseBody = EntityUtils.toByteArray(response.getEntity());
                return iriTranslatorProvider.getIriTranslator().translate(Parser.getInstance()
                        .parse(new ByteArrayInputStream(responseBody),
                                response.getFirstHeader("Content-Type").getValue()));
            }
        }
    }

    private String createQuery(IRI matchersGraphIri) {
        return "CONSTRUCT {?s ?p ?o} WHERE { GRAPH <" + matchersGraphIri.getUnicodeString()+ "> {?s ?p ?o} }";
    }


}
