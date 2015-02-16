import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "testproject"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    cache,
    filters,
    anorm,
    
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0",
    "ws.securesocial" %% "securesocial" % "2.1.3"
    
  )
  
  val main = play.Project(appName, appVersion, appDependencies).settings(
     // Add your own project settings here
     resolvers += Resolver.sonatypeRepo("releases")
  )

}
