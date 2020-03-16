#! /bin/sh

# run from ami3

VERSION=ami2020315
PROGRAM=ami3
# AMIJARS=ami-jars1
AMIJARS=ami-jars
DIR=../${AMIJARS}/${VERSION}/
mkdir ${DIR}
cp -R target/appassembler/bin ${DIR}/bin
cp -R target/appassembler/repo ${DIR}/repo
cp target/${PROGRAM}-0.1-SNAPSHOT-jar-with-dependencies.jar  ${DIR}/${VERSION}-jar-with-dependencies.jar

cd ../${AMIJARS}
git pull
git add $VERSION/
git commit -am "added "$VERSION
git push

