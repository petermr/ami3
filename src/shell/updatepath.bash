#!/usr/bin/env bash


# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASEDIR=`cd "$PRGDIR/.." >/dev/null; pwd`

OLD_AMI_HOME="${AMI_HOME}"

if grep AMI_HOME ~/.bashrc >> /dev/null;
then
  echo "AMI_HOME was previously set to ${AMI_HOME} in ~/.bashrc, updating to ${PRGDIR}";
  sed -i.bak "s/export AMI_HOME=.*$/export AMI_HOME=${PRGDIR}/g" ~/.bashrc && rm ~/.bashrc.bak
else
  echo "AMI_HOME was not set in ~/.bashrc, updating to ${PRGDIR}";
  cat "export AMI_HOME=${PRGDIR}" >> ~/.bashrc
  cat "export PATH=\${AMI_HOME}/bin:\${PATH}" >> ~/.bashrc
fi

echo "Done. Start a new shell session for the changes to take effect."
