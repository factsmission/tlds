# tlds
A simple templating LD Server

This is mainly a demo to shows how [slds](https://github.com/linked-solutions/slds) 
can be extended. It provides a linked data server that can return HTML representations 
of the resource. The HTML rendering is done client-side with [LD2h](https://github.com/rdf2h/ld2h).

The RDF2h renderers are taken from URIs that can be specified with the 
`tlds:renderers` property in the configuration.

# Build / install

In the directory containing tlds run
```
   mvn install -P executable
```

Note that because this depends on an slds SNAPSHOT version you need to compile that first:

> In the directory containing slds run
> ```
>    mvn install -P executable
> ```


Tlds is build and used analogously to [slds](https://github.com/linked-solutions/slds).
