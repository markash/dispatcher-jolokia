import sbt._

object Build extends Build {
	import Keys._

	lazy val dispatchVersion = SettingKey[String]("x-dispatch-version")

	lazy val buildSettings = Defaults.defaultSettings ++ Seq(
		dispatchVersion := "0.10.0",
		version <<= dispatchVersion { dv => "dispatch" + dv + "_0.1.0-SNAPSHOT"},
		organization := "za.co.yellowfire",
		scalaVersion := "2.10.0",
		libraryDependencies <++= (dispatchVersion) { (dv) => Seq(
			"net.databinder.dispatch" %% "dispatch-core" % dv,
			"net.databinder.dispatch" %% "dispatch-json4s-native" % dv
		)},
		libraryDependencies <+= (scalaVersion) {
			case "2.9.3" => "org.spec2" %% "spec2" % "1.12.4.1" % "test"
			case _ => "org.spec2" %% "spec2" % "1.14" % "test"
		},
		crossScalaVersions := Seq("2.10.1"),
		resolvers += "sonatype-public" at "http://oss.sonatype.org/content/repositories/public"
	)

	lazy val coreSettings = buildSettings ++ Seq(
		name := "dispatch-jolokia-core"
	)

	lazy val root = Project("root", file("."), settings = buildSettings ++ Seq(name := "dispatch-jolokia")) aggregate(core)

	lazy val core = Project("core", file("core"), settings = coreSettings)
}