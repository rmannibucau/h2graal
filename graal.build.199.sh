#! /bin/bash

classpath="target/classes:$HOME/.m2/repository/com/h2database/h2/1.4.199/h2-1.4.199.jar"
build_time_classes="org.h2.Driver"

native-image \
    -classpath "$classpath" \
    -H:+TraceClassInitialization \
    -H:+ReportExceptionStackTraces \
    -H:ResourceConfigurationFiles=src/graal/resource-config.json \
    -H:ReflectionConfigurationFiles=src/graal/reflect-config.json \
    --no-server \
    --no-fallback \
    --allow-incomplete-classpath \
    --report-unsupported-elements-at-runtime \
    --initialize-at-build-time="$build_time_classes" \
    com.github.rmannibucau.h2.Sample \
    target/sample.bin &&

echo "You can launch 'target/sample.bin'"

