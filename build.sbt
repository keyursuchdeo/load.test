name := "load.test"

version := "0.1"

scalaVersion := "2.12.8"

enablePlugins(GatlingPlugin)

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.2" % "test,it"
libraryDependencies += "io.gatling"            % "gatling-test-framework"    % "3.0.2" % "test,it"