/*
 * Gretty
 *
 * Copyright (C) 2013-2015 Andrey Hihlovskiy and contributors.
 *
 * See the file "LICENSE" for copying and usage permission.
 * See the file "CONTRIBUTORS" for complete list of contributors.
 */
package org.akhikhl.examples.gretty.hellogretty

import geb.spock.GebReportingSpec

class RequestResponseIT extends GebReportingSpec {

  private static String host
  private static int port
  private static String contextPath

  void setupSpec() {
    host = System.getProperty('gretty.host')
    port = System.getProperty('gretty.port') as int
    contextPath = System.getProperty('gretty.contextPath')
  }

  def 'should get expected static page'() {
    when:
    go "http://${host}:${port}${contextPath}/index.html"
    then:
    $('h1').text() == 'Hello, world!'
    $('p', 0).text() == /This is static HTML page./
    $('p strong').text() == /Please note different context path: it was set in "jetty-env.xml"./
  }

  def 'should get expected response from servlet'() {
    when:
    go "http://${host}:${port}${contextPath}/dynamic"
    then:
    $('h1').text() == 'Hello, world!'
    $('p', 0).text() == /This is dynamic HTML page generated by servlet./
    $('p strong').text() == /Please note different context path: it was set in "jetty-env.xml"./
  }
}

