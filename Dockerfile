FROM maven:3-jdk-8 as builder

# Alternative: clone remote code > not used because Dockerfile lifes in code repo
# get and build ami3 (integrating https://github.com/petermr/cephis and https://github.com/petermr/normami)
# WORKDIR /app
# RUN git clone --depth 1 https://github.com/petermr/ami3.git && \

WORKDIR /app/ami3
COPY src src
COPY pom.xml pom.xml

RUN mvn -Dmaven.test.skip=true install

# unstaged build with just this PATH adjustment works!
ENV PATH /app/ami3/target/appassembler/bin/:${PATH}

# remove unused .bat files
WORKDIR /app/ami3/target/appassembler/bin/
RUN rm *.bat

FROM openjdk:8
# would like to copy more specifically the jar and binary files here, if possible, see #2
COPY --from=builder /app/ami3/target/ /bin/ami3/
ENV PATH /bin/ami3/appassembler/bin/:${PATH}

# Add additional tools needed to handle PDF workflows and support Python tools:
RUN apt-get update && \
    apt-get -y install tesseract-ocr gocr default-jre python3 python3-pip && \
    rm -rf /var/lib/apt/lists/*

## And install Tika and GROBID:
RUN curl -k -o /opt/tika.jar https://www.mirrorservice.org/sites/ftp.apache.org/tika/tika-app-1.24.jar && \
    curl -k -L -o /opt/grobid-src-0.5.3.zip https://github.com/kermitt2/grobid/archive/0.5.3.zip && \
    curl -k -L -o /opt/grobid-core-0.5.3-onejar.jar https://github.com/kermitt2/grobid/releases/download/0.5.3/grobid-core-0.5.3-onejar.jar && \
    cd /opt && unzip -o grobid-src-0.5.3.zip && mkdir -p /opt/grobid-0.5.3/grobid-core/build/libs/ && mv /opt/grobid-core-0.5.3-onejar.jar /opt/grobid-0.5.3/grobid-core/build/libs/ && \
    mkdir -p /opt/grobid-0.5.3/grobid-home/tmp && chmod a+rwx /opt/grobid-0.5.3/grobid-home/tmp

CMD [ "/bin/bash" ]
# docker build --tag ami3 .
# docker run --rm -it norami-docker:0.0.1 ami-test a b c
