#!/usr/bin/env bash

# KDoc - Library
java -jar util/dokka/dokka-fatjar-0.9.14-eap-3.jar library/src/main/kotlin -output build/docs/kdoc/library

# KDoc - Library Extras
java -jar util/dokka/dokka-fatjar-0.9.14-eap-3.jar library-extras/src/main/kotlin -output build/docs/kdoc/library-extras

# JavaDoc - Library
java -jar util/dokka/dokka-fatjar-0.9.14-eap-3.jar library/src/main/kotlin -format javadoc -output build/docs/javadoc/library

# JavaDoc - Library Extras
java -jar util/dokka/dokka-fatjar-0.9.14-eap-3.jar library-extras/src/main/kotlin -format javadoc -output build/docs/javadoc/library-extras