package com.example.forecast

import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class EffAggregatedForecastMmgSimulation extends Simulation {

  /**
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                         60 (OK=60     KO=0     )
    * > min response time                                     95 (OK=95     KO=-     )
    * > max response time                                    568 (OK=568    KO=-     )
    * > mean response time                                   230 (OK=230    KO=-     )
    * > std deviation                                        136 (OK=136    KO=-     )
    * > response time 50th percentile                        176 (OK=176    KO=-     )
    * > response time 75th percentile                        258 (OK=258    KO=-     )
    * > response time 95th percentile                        536 (OK=536    KO=-     )
    * > response time 99th percentile                        555 (OK=555    KO=-     )
    * > mean requests/sec                                      1 (OK=1      KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                            60 (100%)
    * > 800 ms < t < 1200 ms                                   0 (  0%)
    * > t > 1200 ms                                            0 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                         60 (OK=60     KO=0     )
    * > min response time                                     79 (OK=79     KO=-     )
    * > max response time                                    802 (OK=802    KO=-     )
    * > mean response time                                   264 (OK=264    KO=-     )
    * > std deviation                                        137 (OK=137    KO=-     )
    * > response time 50th percentile                        224 (OK=224    KO=-     )
    * > response time 75th percentile                        340 (OK=340    KO=-     )
    * > response time 95th percentile                        505 (OK=505    KO=-     )
    * > response time 99th percentile                        660 (OK=660    KO=-     )
    * > mean requests/sec                                      1 (OK=1      KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                            59 ( 98%)
    * > 800 ms < t < 1200 ms                                   1 (  2%)
    * > t > 1200 ms                                            0 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                         60 (OK=60     KO=0     )
    * > min response time                                     83 (OK=83     KO=-     )
    * > max response time                                    715 (OK=715    KO=-     )
    * > mean response time                                   250 (OK=250    KO=-     )
    * > std deviation                                        134 (OK=134    KO=-     )
    * > response time 50th percentile                        212 (OK=212    KO=-     )
    * > response time 75th percentile                        335 (OK=335    KO=-     )
    * > response time 95th percentile                        483 (OK=483    KO=-     )
    * > response time 99th percentile                        595 (OK=595    KO=-     )
    * > mean requests/sec                                      1 (OK=1      KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                            60 (100%)
    * > 800 ms < t < 1200 ms                                   0 (  0%)
    * > t > 1200 ms                                            0 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    * ----------
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                       1200 (OK=1200   KO=0     )
    * > min response time                                     29 (OK=29     KO=-     )
    * > max response time                                   2082 (OK=2082   KO=-     )
    * > mean response time                                    91 (OK=91     KO=-     )
    * > std deviation                                        141 (OK=141    KO=-     )
    * > response time 50th percentile                         48 (OK=48     KO=-     )
    * > response time 75th percentile                         86 (OK=87     KO=-     )
    * > response time 95th percentile                        240 (OK=240    KO=-     )
    * > response time 99th percentile                        838 (OK=838    KO=-     )
    * > mean requests/sec                                 19.672 (OK=19.672 KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                          1186 ( 99%)
    * > 800 ms < t < 1200 ms                                  11 (  1%)
    * > t > 1200 ms                                            3 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    *
    *
    */

  val feeder: SourceFeederBuilder[String] = csv("mmgsearcheffaggfc.csv").random

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://solr.lb.solr.cotd.test7.mcc.be-gcw1.metroscales.io:8080/solr")

  val readAggRegFcstScn: ScenarioBuilder = scenario("read agg reg forecast").feed(feeder)
    .exec(http("jsonFacetEffAggRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=mainMerchGroup:${mmg}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=meanForecast:[* TO *]&fq=-adjMeanForecast:[* TO *]&fq=-promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"meanForecast": "sum(meanForecast)"}}}}""")).asJson
    .check(status is 200))

  val readAggAdjRegFcstScn: ScenarioBuilder = scenario("read agg adj reg forecast").feed(feeder)
    .exec(http("jsonFacetEffAggAdjRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=mainMerchGroup:${mmg}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjMeanForecast:[* TO *]&fq=-promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"adjMeanForecast": "sum(adjMeanForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggPromoFcstScn: ScenarioBuilder = scenario("read agg promo forecast").feed(feeder)
    .exec(http("jsonFacetEffAggPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=mainMerchGroup:${mmg}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"promoForecast": "sum(promoForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggAdjPromoFcstScn: ScenarioBuilder = scenario("read agg adj promo forecast").feed(feeder)
    .exec(http("jsonFacetEffAggAdjPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=mainMerchGroup:${mmg}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjPromoForecast:[* TO *]&q=*:*&rows=0")
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