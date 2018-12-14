FROM linkedsolutions/slds
RUN bash -c '([[ ! -d $JAVA_SECURITY_DIR ]] && ln -s $JAVA_HOME/lib $JAVA_HOME/conf) || (echo "Found java conf dir, package has been fixed, remove this hack"; exit -1)'
COPY . /usr/src/app/tlds
WORKDIR /usr/src/app/tlds
RUN mvn clean install -Pexecutable -DfinalName=tlds
ENTRYPOINT  ["java", "-jar", "target/tlds-executable.jar"]
CMD ["/config.ttl"]