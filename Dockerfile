FROM anapsix/alpine-java:8

EXPOSE 8090
ENV CONSUL_LOCATION consul

COPY ./target/SPAnnotationDetection.jar  /streampipes-processing-element-container.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processing-element-container.jar"]
