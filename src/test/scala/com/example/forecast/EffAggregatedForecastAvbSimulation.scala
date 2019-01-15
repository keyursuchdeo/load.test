package com.example.forecast

import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class EffAggregatedForecastAvbSimulation extends Simulation {

  /**
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                         60 (OK=60     KO=0     )
    * > min response time                                     32 (OK=32     KO=-     )
    * > max response time                                    117 (OK=117    KO=-     )
    * > mean response time                                    41 (OK=41     KO=-     )
    * > std deviation                                         15 (OK=15     KO=-     )
    * > response time 50th percentile                         37 (OK=37     KO=-     )
    * > response time 75th percentile                         40 (OK=40     KO=-     )
    * > response time 95th percentile                         64 (OK=64     KO=-     )
    * > response time 99th percentile                        114 (OK=114    KO=-     )
    * > mean requests/sec                                      1 (OK=1      KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                            60 (100%)
    * > 800 ms < t < 1200 ms                                   0 (  0%)
    * > t > 1200 ms                                            0 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                       1200 (OK=1200   KO=0     )
    * > min response time                                     29 (OK=29     KO=-     )
    * > max response time                                   1891 (OK=1891   KO=-     )
    * > mean response time                                    58 (OK=58     KO=-     )
    * > std deviation                                         94 (OK=94     KO=-     )
    * > response time 50th percentile                         38 (OK=38     KO=-     )
    * > response time 75th percentile                         54 (OK=54     KO=-     )
    * > response time 95th percentile                        121 (OK=121    KO=-     )
    * > response time 99th percentile                        507 (OK=507    KO=-     )
    * > mean requests/sec                                     20 (OK=20     KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                          1197 (100%)
    * > 800 ms < t < 1200 ms                                   1 (  0%)
    * > t > 1200 ms                                            2 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    */

  val feeder: SourceFeederBuilder[String] = csv("avbsearchaggfc.csv").random

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://solr.lb.solr.cotd.test7.mcc.be-gcw1.metroscales.io:8080/solr")

  val readAggRegFcstScn: ScenarioBuilder = scenario("read agg reg forecast").feed(feeder)
    .exec(http("jsonFacetEffAggRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=avb:${avb}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=meanForecast:[* TO *]&fq=-adjMeanForecast:[* TO *]&fq=-promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"meanForecast": "sum(meanForecast)"}}}}""")).asJson
    .check(status is 200))

  val readAggAdjRegFcstScn: ScenarioBuilder = scenario("read agg adj reg forecast").feed(feeder)
    .exec(http("jsonFacetEffAggAdjRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=avb:${avb}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjMeanForecast:[* TO *]&fq=-promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"adjMeanForecast": "sum(adjMeanForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggPromoFcstScn: ScenarioBuilder = scenario("read agg promo forecast").feed(feeder)
    .exec(http("jsonFacetEffAggPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=avb:${avb}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"promoForecast": "sum(promoForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggAdjPromoFcstScn: ScenarioBuilder = scenario("read agg adj promo forecast").feed(feeder)
    .exec(http("jsonFacetEffAggAdjPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=avb:${avb}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjPromoForecast:[* TO *]&q=*:*&rows=0")
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