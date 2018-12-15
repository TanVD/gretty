/*
 * Gretty
 *
 * Copyright (C) 2013-2015 Andrey Hihlovskiy and contributors.
 *
 * See the file "LICENSE" for copying and usage permission.
 * See the file "CONTRIBUTORS" for complete list of contributors.
 */
package org.akhikhl.gretty.task.tomcat

import org.akhikhl.gretty.servlet.ServletContainerConfig
import org.akhikhl.gretty.task.app.AppStartTask

class TomcatStartTask extends AppStartTask {

    @Override
    protected String getCompatibleServletContainer(String servletContainer) {
        ServletContainerConfig.getTomcatCompatibleServletContainer(project, servletContainer)
    }
}
