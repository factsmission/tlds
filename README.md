# tlds
A simple templating LD Server

This is mainly a demo to shows how [slds](https://github.com/linked-solutions/slds) 
can be extended. It provides a linked data server that can return HTML representations 
of the resource. The HTML rendering is done client-side with [LD2h](https://github.com/rdf2h/ld2h).

The RDF2h renderers are taken from URIs that can be specified with the 
`tlds:renderers` property in the configuration.

## Building and running

You can build slds directly with [maven](https://maven.apache.org/) or use [Docker](https://docker.com). 

### Maven

Note that because this depends on an [slds](https://github.com/linked-solutions/slds) SNAPSHOT version you need to compile slds first:

> In the directory containing slds run
> ```
> mvn install -P executable
> ```

In the directory containing tlds run
```
mvn install -P executable
```
this will create an executable jar e.g. `tlds-1.0.0-SNAPSHOT.jar` in the target
directory.

Run the executable jar with one argument pointing to the configuration in a
turtle file, for example

    java -jar tlds-1.0.0-SNAPSHOT-executable.jar ../example-config.ttl

### Using Docker

You can build a docker image named `tlds` with

    docker build -t tlds .

The image will launch tlds with `/config.ttl` as configuration by default. The easiest way to start it with you own config is to mount a file from your local filesystem at that location. You can then run the image with something like:

    docker run -ti -p 5000:5000 -v C:\Users\me\path\to\config.ttl:/config.ttl tlds 


### Configuration

Same as [slds](https://github.com/linked-solutions/slds) with the addition of the `tlds:renderers` property.
For example the configuration could have the following addition:

    tlds:renderers ("https://rdf2h.github.io/renderers/0.0.3/fallback-renderers.ttl" "/renderers.ttl")
