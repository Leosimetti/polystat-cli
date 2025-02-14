import ReleaseTransformations.*

ThisBuild / scalaVersion := "3.1.2"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / releaseVersionBump := sbtrelease.Version.Bump.Next

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-parse" % "0.3.7",
  "com.monovore" %% "decline" % "2.2.0",
  "com.monovore" %% "decline-effect" % "2.2.0",
  "co.fs2" %% "fs2-core" % "3.2.7",
  "co.fs2" %% "fs2-io" % "3.2.7",
  "org.polystat.odin" %% "analysis" % "0.3.3",
  "is.cir" %% "ciris" % "2.3.2",
  "lt.dvim.ciris-hocon" %% "ciris-hocon" % "1.0.1",
  "org.http4s" %% "http4s-ember-client" % "1.0.0-M32",
).map(_.cross(CrossVersion.for3Use2_13))

excludeDependencies ++= Seq(
  "org.typelevel" % "simulacrum-scalafix-annotations_3",
  "org.typelevel" % "cats-kernel_3",
  "org.typelevel" % "cats-core_3",
)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.1",
  "org.scalameta" %% "munit" % "1.0.0-M3" % Test,
  "org.slf4j" % "slf4j-nop" % "1.7.36",
)

assembly / assemblyJarName := "polystat.jar"
assembly / mainClass := Some("org.polystat.Main")

enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq(version)
buildInfoPackage := "org.polystat"

scalacOptions ++= Seq(
  "-Wunused:all"
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
  pushChanges,
)

val githubWorkflowScalas = List("3.1.2")

val checkoutSetupJava = List(WorkflowStep.Checkout) ++
  WorkflowStep.SetupJava(List(JavaSpec.temurin("11")))

ThisBuild / githubWorkflowPublishTargetBranches := Seq()

ThisBuild / githubWorkflowAddedJobs ++= Seq(
  WorkflowJob(
    id = "scalafmt",
    name = "Format code with scalafmt",
    scalas = githubWorkflowScalas,
    steps = checkoutSetupJava ++
      githubWorkflowGeneratedCacheSteps.value ++
      List(
        WorkflowStep.Sbt(List("scalafmtCheckAll")),
        WorkflowStep.Sbt(List("scalafmtSbtCheck")),
      ),
  )
)
