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

import solutions.linked.slds.Server;
import java.io.FileNotFoundException;
import java.util.Set;


public class TemplatingServer extends Server {
    
    public TemplatingServer(String[] args) throws FileNotFoundException {
        super(args);
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Argument pointing to configuration required");
            return;
        }
        new TemplatingServer(args).run();
    }

    @Override
    protected Set<Object> getJaxRsComponents() {
        Set<Object> result = super.getJaxRsComponents();
        result.add(new HtmlWriter(config));
        result.add(new MyGraphWriter());
        result.add(new Matchers(config));
        result.add(new RdfExtensionsFilter());
        return result;
    }
    
    
}
