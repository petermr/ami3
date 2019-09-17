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

# install npm and getpapers package, partly based on https://gist.github.com/remarkablemark/aacf14c29b3f01d6900d13137b21db3a
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
ENV NVM_DIR /usr/local/nvm
ENV NODE_VERSION 7.10.1
WORKDIR $NVM_DIR

#ARG DEBIAN_FRONTEND=noninteractive
#RUN apt-get update && apt-get install -y --no-install-recommends tzdata && \
#    apt-get clean
RUN wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.34.0/install.sh | bash

RUN source $NVM_DIR/nvm.sh \
    && nvm install $NODE_VERSION \
    && nvm alias default $NODE_VERSION \
    && nvm use default

ENV NODE_PATH $NVM_DIR/v$NODE_VERSION/lib/node_modules
ENV PATH $NVM_DIR/versions/node/v$NODE_VERSION/bin:$PATH

RUN npm install --global getpapers

VOLUME [ "/workspace" ]
WORKDIR /workspace

CMD [ "/bin/bash" ]
# docker build --tag ami3 .
# docker run --rm -it norami-docker:0.0.1 ami-test a b c
