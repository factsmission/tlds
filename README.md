# tlds
A simple templating LD Server

This is mainly a demo to shows how [slds](https://github.com/linked-solutions/slds) 
can be extended. It provides a linked data server that can return HTML representations 
of the resource. The HTML rendering is done client-side with [LD2h](https://github.com/rdf2h/ld2h).

The RDF2h matchers are taken from a named graph named like the requested resource,
but with "/matchers" as URI-path section.

Tlds is build and used analougously to [slds](https://github.com/linked-solutions/slds). 
Note that because this depends on an slds SNAPSHOT version you need to compile 
that first (using `mvn install`)
