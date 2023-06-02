import com.typesafe.sbt.pgp.PgpKeys
import xerial.sbt.Sonatype.autoImport._
import sbt.{Credentials, ScmInfo, _}
import Keys._
import sbtrelease.ReleasePlugin.autoImport._
import ReleaseTransformations._

object Publish {

  val nemoSettings = Seq(

    credentials ++= {
      (sys.env.get("ARTIFACTORY_USERNAME"), sys.env.get("ARTIFACTORY_PASSWORD"), sys.env.get("ARTIFACTORY_URL")) match {
        case (Some(user), Some(pass), Some(url)) =>
          Seq(Credentials("Artifactory Realm", url, user, pass))
        case _ =>
          Seq.empty
      }
    },
    publishMavenStyle := true,
	  publishTo := {
		(version.value.contains("SNAPSHOT"), sys.env.get("ARTIFACTORY_URL")) match {
			case (false, Some(url)) =>
				Some("Artifactory Realm" at s"https://$url/springernature/nemo-libs-release-local")
			case (true, Some(url)) =>
				Some("Artifactory Realm" at s"https://$url/springernature/libs-snapshot")
			case _ => None
		}
	}
  )

  val coreSettings = Seq(
    organization in ThisBuild := "com.iheart",
    publishMavenStyle := true,
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    homepage := Some(url("http://iheartradio.github.io/play-swagger")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/iheartradio/play-swagger"),
      "git@github.com:iheartradio/play-swagger.git")),
    developers := List(
      Developer(
        "kailuowang",
        "Kailuo Wang",
        "kailuo.wang@gmail.com",
        url("https://kailuowang.com")
      )
    ),
    pomIncludeRepository := { _ ⇒ false },
    publishArtifact in Test := false,
    releaseCrossBuild := true,
    publishTo := sonatypePublishToBundle.value,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      releaseStepCommandAndRemaining("+clean"),
      releaseStepCommandAndRemaining("+test"),
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+publishSigned"),
      releaseStepCommand("sonatypeBundleRelease"),
      setNextVersion,
      commitNextVersion,
      pushChanges))

}
