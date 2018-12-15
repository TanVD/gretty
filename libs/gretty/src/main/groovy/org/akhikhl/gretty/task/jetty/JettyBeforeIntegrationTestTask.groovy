/*
 * Gretty
 *
 * Copyright (C) 2013-2015 Andrey Hihlovskiy and contributors.
 *
 * See the file "LICENSE" for copying and usage permission.
 * See the file "CONTRIBUTORS" for complete list of contributors.
 */
package org.akhikhl.gretty.task.jetty

import org.akhikhl.gretty.task.app.AppBeforeIntegrationTestTask
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This class is deprecated, please use AppBeforeIntegrationTestTask instead.
 *
 * @author akhikhl
 */
class JettyBeforeIntegrationTestTask extends AppBeforeIntegrationTestTask {

    protected static final Logger log = LoggerFactory.getLogger(JettyBeforeIntegrationTestTask)

    JettyBeforeIntegrationTestTask() {
        doFirst {
            log.warn 'JettyBeforeIntegrationTestTask is deprecated and will be removed in Gretty 2.0. Please use AppBeforeIntegrationTestTask instead.'
        }
    }
}
