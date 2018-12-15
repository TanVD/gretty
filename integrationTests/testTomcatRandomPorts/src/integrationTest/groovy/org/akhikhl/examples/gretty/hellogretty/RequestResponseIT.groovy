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

    private static String baseURI
    private static String baseHttpsURI

    void setupSpec() {
        baseURI = System.getProperty('gretty.baseURI')
        baseHttpsURI = System.getProperty('gretty.httpsBaseURI')
    }

    def 'should get expected static page'() {
        when:
        go "${baseURI}/index.html"
        then:
        $('h1').text() == 'Hello, world!'
        $('p', 0).text() == /This is static HTML page./
    }

    def 'should get expected response from servlet'() {
        when:
        go "${baseURI}/dynamic"
        then:
        $('h1').text() == 'Hello, world!'
        $('p', 0).text() == /This is dynamic HTML page generated by servlet./
    }

    def 'should get expected static page over HTTPS'() {
        when:
        go "${baseHttpsURI}/index.html"
        then:
        $('h1').text() == 'Hello, world!'
        $('p', 0).text() == /This is static HTML page./
    }

    def 'should get expected response from servlet over HTTPS'() {
        when:
        go "${baseHttpsURI}/dynamic"
        then:
        $('h1').text() == 'Hello, world!'
        $('p', 0).text() == /This is dynamic HTML page generated by servlet./
    }
}
