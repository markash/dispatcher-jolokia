sbtResolver <<= (sbtResolver) { r =>
  Option(System.getenv("SBT_PROXY_REPO")) map { x =>
    Resolver.url("proxy repo for sbt", url(x))(Resolver.ivyStylePatterns)
  } getOrElse r
}

resolvers <<= (resolvers) { r =>
  (Option(System.getenv("SBT_PROXY_REPO")) map { url =>
    Seq("proxy-repo" at url)
  } getOrElse {
    r ++ Seq(
      "sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
      "scala-tools" at "http://scala-tools.org/repo-releases/",
      "maven" at "http://repo1.maven.org/maven2/"
    )
  }) ++ Seq("local" at ("file:" + System.getProperty("user.home") + "/.m2/repo/"))
}

externalResolvers <<= (resolvers) map identity

//addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")