name := "ApiGateway"

version := "1.0"

lazy val `apigateway` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc ,
  cache ,
  ws   ,
  specs2 % Test,
  "org.apache.kafka" % "kafka_2.11" % "0.8.2.0"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

