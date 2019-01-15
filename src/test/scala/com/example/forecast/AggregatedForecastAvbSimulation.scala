package com.example.forecast

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class AggregatedForecastAvbSimulation extends Simulation {

  /**
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                         60 (OK=60     KO=0     )
    * > min response time                                     30 (OK=30     KO=-     )
    * > max response time                                     88 (OK=88     KO=-     )
    * > mean response time                                    39 (OK=39     KO=-     )
    * > std deviation                                          9 (OK=9      KO=-     )
    * > response time 50th percentile                         36 (OK=36     KO=-     )
    * > response time 75th percentile                         41 (OK=41     KO=-     )
    * > response time 95th percentile                         54 (OK=54     KO=-     )
    * > response time 99th percentile                         71 (OK=71     KO=-     )
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
    * > min response time                                     30 (OK=30     KO=-     )
    * > max response time                                  12152 (OK=12152  KO=-     )
    * > mean response time                                  1186 (OK=1186   KO=-     )
    * > std deviation                                       2546 (OK=2546   KO=-     )
    * > response time 50th percentile                         54 (OK=54     KO=-     )
    * > response time 75th percentile                        207 (OK=207    KO=-     )
    * > response time 95th percentile                       7882 (OK=7882   KO=-     )
    * > response time 99th percentile                       9914 (OK=9914   KO=-     )
    * > mean requests/sec                                     20 (OK=20     KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                           961 ( 80%)
    * > 800 ms < t < 1200 ms                                  11 (  1%)
    * > t > 1200 ms                                          228 ( 19%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                       1200 (OK=1200   KO=0     )
    * > min response time                                     28 (OK=28     KO=-     )
    * > max response time                                    303 (OK=303    KO=-     )
    * > mean response time                                    44 (OK=44     KO=-     )
    * > std deviation                                         22 (OK=22     KO=-     )
    * > response time 50th percentile                         36 (OK=36     KO=-     )
    * > response time 75th percentile                         50 (OK=51     KO=-     )
    * > response time 95th percentile                         67 (OK=67     KO=-     )
    * > response time 99th percentile                        142 (OK=142    KO=-     )
    * > mean requests/sec                                     20 (OK=20     KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                          1200 (100%)
    * > 800 ms < t < 1200 ms                                   0 (  0%)
    * > t > 1200 ms                                            0 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    */

  val feeder: SourceFeederBuilder[String] = csv("avbsearchaggfc.csv").random

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://solr.lb.solr.cotd.test7.mcc.be-gcw1.metroscales.io:8080/solr")

  val readAggRegFcstScn: ScenarioBuilder = scenario("read agg reg forecast").feed(feeder)
    .exec(http("jsonFacetAggRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=avb:${avb}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=meanForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"meanForecast": "sum(meanForecast)"}}}}""")).asJson
    .check(status is 200))

  val readAggAdjRegFcstScn: ScenarioBuilder = scenario("read agg adj reg forecast").feed(feeder)
    .exec(http("jsonFacetAggAdjRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=avb:${avb}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjMeanForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"adjmeanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"adjMeanForecast": "sum(adjMeanForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggPromoFcstScn: ScenarioBuilder = scenario("read agg promo forecast").feed(feeder)
    .exec(http("jsonFacetAggPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=avb:${avb}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=promoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"promoperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"promoForecast": "sum(promoForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggAdjPromoFcstScn: ScenarioBuilder = scenario("read agg adj promo forecast").feed(feeder)
    .exec(http("jsonFacetAggAdjPromoForecastTest")
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

