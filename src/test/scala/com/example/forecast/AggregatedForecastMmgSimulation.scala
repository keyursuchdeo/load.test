package com.example.forecast

import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class AggregatedForecastMmgSimulation extends Simulation {

  /**
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                         60 (OK=60     KO=0     )
    * > min response time                                     77 (OK=77     KO=-     )
    * > max response time                                   3466 (OK=3466   KO=-     )
    * > mean response time                                   349 (OK=349    KO=-     )
    * > std deviation                                        454 (OK=454    KO=-     )
    * > response time 50th percentile                        240 (OK=240    KO=-     )
    * > response time 75th percentile                        405 (OK=405    KO=-     )
    * > response time 95th percentile                        718 (OK=718    KO=-     )
    * > response time 99th percentile                       2215 (OK=2215   KO=-     )
    * > mean requests/sec                                      1 (OK=1      KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                            58 ( 97%)
    * > 800 ms < t < 1200 ms                                   0 (  0%)
    * > t > 1200 ms                                            2 (  3%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                         60 (OK=60     KO=0     )
    * > min response time                                     77 (OK=77     KO=-     )
    * > max response time                                    709 (OK=709    KO=-     )
    * > mean response time                                   235 (OK=235    KO=-     )
    * > std deviation                                        149 (OK=149    KO=-     )
    * > response time 50th percentile                        192 (OK=192    KO=-     )
    * > response time 75th percentile                        279 (OK=279    KO=-     )
    * > response time 95th percentile                        558 (OK=558    KO=-     )
    * > response time 99th percentile                        682 (OK=682    KO=-     )
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
    * > request count                                         60 (OK=60     KO=0     )
    * > min response time                                    108 (OK=108    KO=-     )
    * > max response time                                    654 (OK=654    KO=-     )
    * > mean response time                                   242 (OK=242    KO=-     )
    * > std deviation                                        124 (OK=124    KO=-     )
    * > response time 50th percentile                        202 (OK=202    KO=-     )
    * > response time 75th percentile                        313 (OK=313    KO=-     )
    * > response time 95th percentile                        524 (OK=524    KO=-     )
    * > response time 99th percentile                        597 (OK=597    KO=-     )
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
    * > max response time                                   4638 (OK=4638   KO=-     )
    * > mean response time                                   491 (OK=491    KO=-     )
    * > std deviation                                        850 (OK=850    KO=-     )
    * > response time 50th percentile                         96 (OK=96     KO=-     )
    * > response time 75th percentile                        373 (OK=373    KO=-     )
    * > response time 95th percentile                       2610 (OK=2610   KO=-     )
    * > response time 99th percentile                       3534 (OK=3534   KO=-     )
    * > mean requests/sec                                     20 (OK=20     KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                           984 ( 82%)
    * > 800 ms < t < 1200 ms                                  36 (  3%)
    * > t > 1200 ms                                          180 ( 15%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                       1200 (OK=1200   KO=0     )
    * > min response time                                     29 (OK=29     KO=-     )
    * > max response time                                   1979 (OK=1979   KO=-     )
    * > mean response time                                    97 (OK=97     KO=-     )
    * > std deviation                                        155 (OK=155    KO=-     )
    * > response time 50th percentile                         50 (OK=50     KO=-     )
    * > response time 75th percentile                         92 (OK=92     KO=-     )
    * > response time 95th percentile                        289 (OK=289    KO=-     )
    * > response time 99th percentile                        908 (OK=908    KO=-     )
    * > mean requests/sec                                     20 (OK=20     KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                          1187 ( 99%)
    * > 800 ms < t < 1200 ms                                   7 (  1%)
    * > t > 1200 ms                                            6 (  1%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    */
  val feeder: SourceFeederBuilder[String] = csv("mmgsearcheffaggfc.csv").random

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://solr.lb.solr.cotd.test7.mcc.be-gcw1.metroscales.io:8080/solr")

  val readAggRegFcstScn: ScenarioBuilder = scenario("read agg reg forecast").feed(feeder)
    .exec(http("jsonFacetAggRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=mainMerchGroup:${mmg}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=meanForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"meanForecast": "sum(meanForecast)"}}}}""")).asJson
    .check(status is 200))

  val readAggAdjRegFcstScn: ScenarioBuilder = scenario("read agg adj reg forecast").feed(feeder)
    .exec(http("jsonFacetAggAdjRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=mainMerchGroup:${mmg}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjMeanForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"adjMeanForecast": "sum(adjMeanForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggPromoFcstScn: ScenarioBuilder = scenario("read agg promo forecast").feed(feeder)
    .exec(http("jsonFacetAggPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=mainMerchGroup:${mmg}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=promoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"promoForecast": "sum(promoForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggAdjPromoFcstScn: ScenarioBuilder = scenario("read agg adj promo forecast").feed(feeder)
    .exec(http("jsonFacetAggAdjPromoForecastTest")
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

