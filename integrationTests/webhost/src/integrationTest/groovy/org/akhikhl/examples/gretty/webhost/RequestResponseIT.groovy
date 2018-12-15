/*
 * Gretty
 *
 * Copyright (C) 2013-2015 Andrey Hihlovskiy and contributors.
 *
 * See the file "LICENSE" for copying and usage permission.
 * See the file "CONTRIBUTORS" for complete list of contributors.
 */
package org.akhikhl.examples.gretty.webhost

import geb.spock.GebReportingSpec

class RequestResponseIT extends GebReportingSpec {

    private static String baseURI

    void setupSpec() {
        baseURI = System.getProperty('gretty.baseURI')
    }

    def 'should get expected static page'() {
        when:
        go "${baseURI}/index.html"
        then:
        $('h1').text() == 'Hello, world!'
        $('p', 0).text() == /This is static HTML page./
        $('p strong').text() == /The page actually comes from web-fragment./
    }

    def 'should get expected response from servlet'() {
        when:
        go "${baseURI}/dynamic"
        then:
        $('h1').text() == 'Hello, world!'
        $('p', 0).text() == /This is dynamic HTML page generated by servlet./
        $('p strong').text() == /The page actually comes from web-fragment./
    }
}
