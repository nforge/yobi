import com.typesafe.config._
import java.nio.file.Paths

name := """yobi"""

version := "0.8.0-rc2"

libraryDependencies ++= Seq(
  // Add your project dependencies here,
  javaCore,
  javaJdbc,
  javaEbean,
  cache,
  // Add your project dependencies here,
  "com.h2database" % "h2" % "1.4.184",
  // Core Library
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.5.3.201412180710-r",
  // Smart HTTP Servlet
  "org.eclipse.jgit" % "org.eclipse.jgit.http.server" % "3.5.3.201412180710-r",
  // Symlink support for Java7
  "org.eclipse.jgit" % "org.eclipse.jgit.java7" % "3.5.3.201412180710-r",
  // svnkit
  "sonia.svnkit" % "svnkit" % "1.8.5-scm2",
  // svnkit-dav
  "sonia.svnkit" % "svnkit-dav" % "1.8.5-scm2",
  // javahl
  "sonia.svnkit" % "svnkit-javahl16" % "1.8.5-scm2",
  "net.sourceforge.jexcelapi" % "jxl" % "2.6.10",
  // shiro
  "org.apache.shiro" % "shiro-core" % "1.2.1",
  // commons-codec
  "commons-codec" % "commons-codec" % "1.2",
  // apache-mails
  "org.apache.commons" % "commons-email" % "1.2",
  "info.schleichardt" %% "play-2-mailplugin" % "0.9.1",
  "commons-lang" % "commons-lang" % "2.6",
  "org.apache.tika" % "tika-core" % "1.2",
  "commons-io" % "commons-io" % "2.4",
  "org.julienrf" %% "play-jsmessages" % "1.6.2",
  "commons-collections" % "commons-collections" % "3.2.1",
  "org.jsoup" % "jsoup" % "1.7.2",
  "com.googlecode.juniversalchardet" % "juniversalchardet" % "1.0.3",
  "org.mockito" % "mockito-all" % "1.9.0" % "test",
  "com.github.zafarkhaja" % "java-semver" % "0.7.2",
  // mina-sshd
  "org.apache.mina" % "mina-core" % "2.0.7",
  "org.apache.sshd" % "sshd-core" % "0.12.0",
  // bouncycastle
  "org.bouncycastle" % "bcprov-jdk15on" % "1.49",
  "org.bouncycastle" % "bcmail-jdk15on" % "1.49",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.49"
)

val projectSettings = Seq(
  // Add your own project settings here
  resolvers += "jgit-repository" at "http://download.eclipse.org/jgit/maven",
  resolvers += "scm-manager release repository" at "http://maven.scm-manager.org/nexus/content/groups/public",
  resolvers += "tmatesoft release repository" at "http://maven.tmatesoft.com/content/repositories/releases",
  resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/",
  TwirlKeys.templateImports in Compile += "models.enumeration._",
  TwirlKeys.templateImports in Compile += "scala.collection.JavaConversions._",
  TwirlKeys.templateImports in Compile += "play.core.j.PlayMagicForJava._",
  TwirlKeys.templateImports in Compile += "java.lang._",
  TwirlKeys.templateImports in Compile += "java.util._",
  includeFilter in (Assets, LessKeys.less) := "*.less",
  excludeFilter in (Assets, LessKeys.less) := "_*.less",
  javaOptions in test ++= Seq("-Xmx2g", "-Xms1g", "-XX:MaxPermSize=1g", "-Dfile.encoding=UTF-8"),
  javacOptions ++= Seq("-Xlint:all", "-Xlint:-path"),
  scalacOptions ++= Seq("-feature")
)

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version)

buildInfoPackage := "yobi"

mappings in Universal :=
    (mappings in Universal).value.filterNot { case (_, file) => file.startsWith("conf/") }

NativePackagerKeys.bashScriptExtraDefines += """# Added by build.sbt
    |YOBI_HOME=$(cd "$(realpath "$(dirname "$(realpath "$0")")")/.."; pwd -P)
    |addJava "-Dyobi.home=$YOBI_HOME"
    |
    |yobi_config_file="$YOBI_HOME"/conf/application.conf
    |yobi_log_config_file="$YOBI_HOME"/conf/application-logger.xml
    |[ -f "$yobi_config_file" ] && addJava "-Dconfig.file=$yobi_config_file"
    |[ -f "$yobi_log_config_file" ] && addJava "-Dlogger.file=$yobi_log_config_file"
    |""".stripMargin

lazy val yobi = (project in file("."))
      .enablePlugins(PlayScala)
      .enablePlugins(SbtWeb)
      .enablePlugins(SbtTwirl)
      .settings(projectSettings: _*)
      .settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)
      .settings(de.johoop.findbugs4sbt.FindBugs.findbugsSettings: _*)
      .settings(findbugsExcludeFilters :=  Some(
          <FindBugsFilter>
            <!-- Exclude classes generated by PlayFramework. See docs/examples
                 at http://findbugs.sourceforge.net/manual/filter.html for the
                 filtering rules. -->
            <Match>
              <Class name="~views\.html\..*"/>
            </Match>
            <Match>
              <Class name="~Routes.*"/>
            </Match>
            <Match>
              <Class name="~controllers\.routes.*"/>
            </Match>
          </FindBugsFilter>
        )
      )
