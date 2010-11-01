#!/bin/sh

JAVA_OPTS='-Xms2000m -Xmx6000m -server -Djava.awt.headless=true -Dxitrum.mode=production'

# These should be the same as in project/build.properties
APP_VERSION=0.1-SNAPSHOT
APP=colinh

# Not frequently changed
MAIN_CLASS='colinh.Boot'
SCALA_VERSION=2.8.0

CLASS_PATH="lib/*:config"

#-------------------------------------------------------------------------------

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

start() {
  nohup java $JAVA_OPTS -cp $CLASS_PATH $MAIN_CLASS > log/$APP.out 2>&1 &
  ps aux | grep $MAIN_CLASS
}

release() {
  REL_DIR=target/$APP-$APP_VERSION
  LIB_DIR=$REL_DIR/lib

  rm -rf target/$APP*
  mkdir -p $REL_DIR

  # lib directory --------------------------------------------------------------

  mkdir $LIB_DIR
  sbt clean
  sbt package
  cp target/scala_$SCALA_VERSION/"$APP"_$SCALA_VERSION-$APP_VERSION.jar $LIB_DIR
  cp lib_managed/scala_$SCALA_VERSION/compile/*.jar $LIB_DIR
  if [ -d lib ]; then cp lib/*.jar $LIB_DIR; fi
  cp project/boot/scala-$SCALA_VERSION/lib/scala-library.jar $LIB_DIR/scala-library-$SCALA_VERSION.jar

  # Scalate needs Scala compiler to compile autogenerated .scala files
  cp project/boot/scala-$SCALA_VERSION/lib/scala-compiler.jar $LIB_DIR/scala-compiler-$SCALA_VERSION.jar

  # Other default directories --------------------------------------------------

  # Do not copy directory (e.g. cp -r) to avoid hidden files
  mkdir $REL_DIR/bin
  cp bin/* $REL_DIR/bin

  mkdir $REL_DIR/log

  mkdir $REL_DIR/config
  cp config/* $REL_DIR/config
  mv $REL_DIR/config/logback.xml.sample $REL_DIR/config/logback.xml

  # TODO: avoid copying hidden files
  if [ -d public ]; then cp -r public $REL_DIR; fi

  # Application-specific operations --------------------------------------------

  cp README $REL_DIR

  # Compress -------------------------------------------------------------------

  cd target
  tar cjf $APP-$APP_VERSION.tar.bz2 $APP-$APP_VERSION
}

case "$1" in
  release)
    release
    ;;
  *)
    start
esac
