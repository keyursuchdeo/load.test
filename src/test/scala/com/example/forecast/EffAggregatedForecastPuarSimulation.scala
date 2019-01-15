package com.example.forecast

import io.gatling.core.Predef._
import io.gatling.core.feeder.SourceFeederBuilder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class EffAggregatedForecastPuarSimulation extends Simulation {

  /**
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                       1200 (OK=1200   KO=0     )
    * > min response time                                     28 (OK=28     KO=-     )
    * > max response time                                   1399 (OK=1399   KO=-     )
    * > mean response time                                   133 (OK=133    KO=-     )
    * > std deviation                                        181 (OK=181    KO=-     )
    * > response time 50th percentile                         55 (OK=56     KO=-     )
    * > response time 75th percentile                        123 (OK=123    KO=-     )
    * > response time 95th percentile                        559 (OK=559    KO=-     )
    * > response time 99th percentile                        801 (OK=801    KO=-     )
    * > mean requests/sec                                 19.672 (OK=19.672 KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                          1187 ( 99%)
    * > 800 ms < t < 1200 ms                                  10 (  1%)
    * > t > 1200 ms                                            3 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    *
    * ================================================================================
    * ---- Global Information --------------------------------------------------------
    * > request count                                       1200 (OK=1200   KO=0     )
    * > min response time                                     29 (OK=29     KO=-     )
    * > max response time                                   1048 (OK=1048   KO=-     )
    * > mean response time                                   126 (OK=126    KO=-     )
    * > std deviation                                        154 (OK=154    KO=-     )
    * > response time 50th percentile                         58 (OK=58     KO=-     )
    * > response time 75th percentile                        129 (OK=129    KO=-     )
    * > response time 95th percentile                        465 (OK=465    KO=-     )
    * > response time 99th percentile                        742 (OK=742    KO=-     )
    * > mean requests/sec                                 19.672 (OK=19.672 KO=-     )
    * ---- Response Time Distribution ------------------------------------------------
    * > t < 800 ms                                          1193 ( 99%)
    * > 800 ms < t < 1200 ms                                   7 (  1%)
    * > t > 1200 ms                                            0 (  0%)
    * > failed                                                 0 (  0%)
    * ================================================================================
    *
    */

  val feeder: SourceFeederBuilder[String] = csv("puarsearcheffaggfc.csv").random

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://solr.lb.solr.cotd.test7.mcc.be-gcw1.metroscales.io:8080/solr")

  val readAggRegFcstScn: ScenarioBuilder = scenario("read agg reg forecast").feed(feeder)
    .exec(http("jsonFacetEffAggRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=purchaseArea:${puar}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=meanForecast:[* TO *]&fq=-adjMeanForecast:[* TO *]&fq=-promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"meanForecast": "sum(meanForecast)"}}}}""")).asJson
    .check(status is 200))

  val readAggAdjRegFcstScn: ScenarioBuilder = scenario("read agg adj reg forecast").feed(feeder)
    .exec(http("jsonFacetEffAggAdjRegularForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=purchaseArea:${puar}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=adjMeanForecast:[* TO *]&fq=-promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"adjMeanForecast": "sum(adjMeanForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggPromoFcstScn: ScenarioBuilder = scenario("read agg promo forecast").feed(feeder)
    .exec(http("jsonFacetEffAggPromoForecastTest")
      .post("/forecasts_cz/select?fq=country:CZ&fq=purchaseArea:${puar}&fq=period:[2018-12-01T00:00:00Z TO 2019-06-30T00:00:00Z]&fq=promoForecast:[* TO *]&fq=-adjPromoForecast:[* TO *]&q=*:*&rows=0")
      .body(StringBody("""{"facet":{"meanperiod": {"type": "terms", "field": "period", "limit": 300, "facet": {"promoForecast": "sum(promoForecast)"}}}}""")).asJson
      .check(status is 200))

  val readAggAdjPromoFcstScn: ScenarioBuilder = scenario("read agg adj promo forecast").feed(feeder)
    .exec(http("jsonFacetEffAggAdjPromoForecastTest")
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