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
			"net.databinder.dispatch" %% "dispatch-json4s-native" % dv,
			"net.databinder.dispatch" %% "dispatch-json4s-jackson" % dv
		)},
		libraryDependencies ++= Seq(
			"junit" % "junit" % "4.8.1" % "test",
			"org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"
		),
		crossScalaVersions := Seq("2.10.1"),
		resolvers += "repo-prox" at "http://vt01ecs02.tb01.test.jse.co.za:9092/nexus/content/groups/public"

	)

	lazy val coreSettings = buildSettings ++ Seq(
		name := "dispatch-jolokia-core"
	)

	lazy val root = Project("root", file("."), settings = buildSettings ++ Seq(name := "dispatch-jolokia")) aggregate(core)

	lazy val core = Project("core", file("core"), settings = coreSettings)
}