package com.example.forecast

import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class AggregatedForecastPuarSimulation extends Simulation {

  /**
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                       1200 (OK=1200   KO=0     )
    * > min response time                                     28 (OK=28     KO=-     )
    * > max response time                                   3039 (OK=3039   KO=-     )
    * > mean response time                                   192 (OK=192    KO=-     )
    * > std deviation                                        318 (OK=318    KO=-     )
    * > response time 50th percentile                         70 (OK=70     KO=-     )
    * > response time 75th percentile                        177 (OK=177    KO=-     )
    * > response time 95th percentile                        733 (OK=733    KO=-     )
    * > response time 99th percentile                       1746 (OK=1746   KO=-     )
    * > mean requests/sec                                     20 (OK=20     KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                          1142 ( 95%)
    * > 800 ms < t < 1200 ms                                  30 (  3%)
    * > t > 1200 ms                                           28 (  2%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                       1200 (OK=1200   KO=0     )
    * > min response time                                     29 (OK=29     KO=-     )
    * > max response time                                   1088 (OK=1088   KO=-     )
    * > mean response time                                   114 (OK=114    KO=-     )
    * > std deviation                                        124 (OK=124    KO=-     )
    * > response time 50th percentile                         59 (OK=59     KO=-     )
    * > response time 75th percentile                        119 (OK=119    KO=-     )
    * > response time 95th percentile                        401 (OK=401    KO=-     )
    * > response time 99th percentile                        536 (OK=536    KO=-     )
    * > mean requests/sec                                 19.672 (OK=19.672 KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                          1198 (100%)
    * > 800 ms < t < 1200 ms                                   2 (  0%)
    * > t > 1200 ms                                            0 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    */

  val feeder: SourceFeederBuilder[String] = csv("puarsearcheffaggfc.csv").random

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://solr.lb.solr.cotd.test7.mcc.be-gcw1.metroscales.io:8080/solr")

  val readAggRegFcstScn: ScenarioBuilder = scenario("read agg reg forecast").feed(feeder)
    .exec(http("jsonFacetAggRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=purchaseArea:${puar}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=meanForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"meanForecast": "sum(meanForecast)"}}}}""")).asJson
    .check(status is 200))

  val readAggAdjRegFcstScn: ScenarioBuilder = scenario("read agg adj reg forecast").feed(feeder)
    .exec(http("jsonFacetAggAdjRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=purchaseArea:${puar}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjMeanForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"adjMeanForecast": "sum(adjMeanForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggPromoFcstScn: ScenarioBuilder = scenario("read agg promo forecast").feed(feeder)
    .exec(http("jsonFacetAggPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=purchaseArea:${puar}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=promoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"promoForecast": "sum(promoForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggAdjPromoFcstScn: ScenarioBuilder = scenario("read agg adj promo forecast").feed(feeder)
    .exec(http("jsonFacetAggAdjPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=purchaseArea:${puar}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"adjPromoForecast": "sum(adjPromoForecast)"}}}}""")).asJson
      .check(status is 200))

  setUp(readAggRegFcstScn.inject(
    constantUsersPerSec(5) during(1 minute)
  ).protocols(httpProtocol),
    readAggAdjRegFcstScn.inject(
      constantUsersPerSec(5) during(1 minute)
    ).protocols(httpProtocol),
    readAggPromoFcstScn.inject(
      constantUsersPerSec(5) during(1 minute)
    ).protocols(httpProtocol),
    readAggAdjPromoFcstScn.inject(
      constantUsersPerSec(5) during(1 minute)
    ).protocols(httpProtocol)
  )
}

