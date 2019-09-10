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

#FROM openjdk:8
#COPY --from=builder /app/ami3/target/appassembler/bin/ /bin/ami3/bin/
#COPY --from=builder /app/ami3/target/*jar-with-dependencies.jar /bin/ami3/
#ENV PATH /bin/ami3/bin:${PATH}

VOLUME [ "/workspace" ]
WORKDIR /workspace

CMD [ "/bin/bash" ]
# docker build --tag ami3 .
# docker run --rm -it norami-docker:0.0.1 ami-test a b c
