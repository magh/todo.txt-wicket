#!/bin/bash
######################################
# - todo.txt-wicket
#   - bin
#     - run.sh
#   - webapp
#     - `unzip todo.txt-wicket-*.war`
######################################
MAINPATH=".."
CONTEXTPATH="/"
WARPATH="$MAINPATH/webapp"
LIBRARY="$WARPATH/WEB-INF/lib"
CLASSES="$WARPATH/WEB-INF/classes"
KEYSTORE="$CLASSES/keystore"
SSLPORT="8443"
KEYSTOREPASSWORD="somepass"

CLASSPATH="$CLASSES"
for a in $LIBRARY/*.jar; do CLASSPATH=$CLASSPATH:$a; done

/usr/bin/java -Dkeystore=$KEYSTORE -Dkeystorepassword=$KEYSTOREPASSWORD -Dcontextpath_0=$CONTEXTPATH -Dwarpath_0=$WARPATH -Dsslport=$SSLPORT -cp $CLASSPATH com.todotxt.todotxtwicket.common.StartJettySsl
