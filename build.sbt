

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.confluent",
      scalaVersion := "2.12.5",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "multiple-schemas",

    resolvers ++= Seq (
      Opts.resolver.mavenLocalFile,
      "Confluent" at "http://packages.confluent.io/maven"
    ),

    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.2",
      "org.apache.kafka" % "kafka-clients" % "2.4.0",
      "org.apache.kafka" % "connect-json" % "2.4.0",
      "org.apache.kafka" % "connect-runtime" % "2.4.0",
      "org.apache.kafka" % "kafka-streams" % "2.4.0",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.4.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.5",
      "org.apache.kafka" % "connect-runtime" % "2.1.0",
      "io.confluent" % "kafka-avro-serializer" % "5.5.0",
      "javax.ws.rs" % "javax.ws.rs-api" % "2.1.1" artifacts( Artifact("javax.ws.rs-api", "jar", "jar"))// this is a workaround for https://github.com/jax-rs/api/issues/571
    )


  ).enablePlugins(SbtAvro)
  .disablePlugins(SbtAvrohugger)
