/*
 * Gretty
 *
 * Copyright (C) 2013-2015 Andrey Hihlovskiy and contributors.
 *
 * See the file "LICENSE" for copying and usage permission.
 * See the file "CONTRIBUTORS" for complete list of contributors.
 */
package org.akhikhl.gretty.task

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.akhikhl.gretty.DefaultLauncher
import org.akhikhl.gretty.Launcher
import org.akhikhl.gretty.LauncherConfig
import org.akhikhl.gretty.ServerConfig
import org.akhikhl.gretty.StartConfig
import org.akhikhl.gretty.WebAppClassPathResolver
import org.akhikhl.gretty.WebAppConfig
import org.akhikhl.gretty.utils.ProjectUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Base task for starting jetty
 *
 * @author akhikhl
 */
@CompileStatic(TypeCheckingMode.SKIP)
abstract class StartBaseTask extends DefaultTask {

    boolean interactive = true
    boolean debug = false
    Integer debugPort
    Boolean debugSuspend

    protected final List<Closure> prepareServerConfigClosures = []
    protected final List<Closure> prepareWebAppConfigClosures = []

    Map serverStartInfo

    StartBaseTask() {
        getOutputs().upToDateWhen { false }
    }

    @TaskAction
    void action() {
        LauncherConfig config = getLauncherConfig()
        Launcher launcher = new DefaultLauncher(project, config)
        if (getIntegrationTest()) {
            boolean result = false
            try {
                launcher.beforeLaunch()
                try {
                    project.ext.grettyLauncher = launcher
                    project.ext.grettyLaunchThread = launcher.launchThread()
                    result = true
                } finally {
                    // Need to call afterLaunch in case of unsuccessful launch.
                    // If launch was successful, afterLaunch will be called in AppAfterIntegrationTestTask or FarmAfterIntegrationTestTask
                    if (!result) {
                        launcher.afterLaunch()
                    }
                }
            } finally {
                // Need to dispose of launcher in case of unsuccessful launch.
                // If launch was successful, launcher will be shut down in AppAfterIntegrationTestTask or FarmAfterIntegrationTestTask
                if (!result) {
                    launcher.dispose()
                }
            }
        } else {
            try {
                launcher.launch()
            } finally {
                launcher.dispose()
            }
        }
        serverStartInfo = launcher.getServerStartInfo()
    }

    protected final void doPrepareServerConfig(ServerConfig sconfig) {
        if (getManagedClassReload(sconfig)) {
            println("Starting with DCEVM and HotSwap Agent")
            sconfig.jvmArgs '-XXaltjvm=dcevm', '-javaagent:' + project.configurations.grettyHotswapAgent.singleFile.absolutePath
        }

        for (Closure c in prepareServerConfigClosures) {
            c = c.rehydrate(sconfig, c.owner, c.thisObject)
            c.resolveStrategy = Closure.DELEGATE_FIRST
            c()
        }
    }

    protected final void doPrepareWebAppConfig(WebAppConfig wconfig) {
        for (Closure c in prepareWebAppConfigClosures) {
            c = c.rehydrate(wconfig, c.owner, c.thisObject)
            c.resolveStrategy = Closure.DELEGATE_FIRST
            c()
        }
    }

    protected boolean getIntegrationTest() {
        false
    }

    protected final LauncherConfig getLauncherConfig() {

        def self = this
        def startConfig = getStartConfig()

        new LauncherConfig() {

            boolean getDebug() {
                self.debug
            }

            int getDebugPort() {
                self.debugPort == null ? self.project.gretty.debugPort : self.debugPort
            }

            boolean getDebugSuspend() {
                self.debugSuspend == null ? self.project.gretty.debugSuspend : self.debugSuspend
            }

            boolean getInteractive() {
                self.getInteractive()
            }

            boolean getManagedClassReload() {
                self.getManagedClassReload(startConfig.getServerConfig())
            }

            ServerConfig getServerConfig() {
                startConfig.getServerConfig()
            }

            String getStopCommand() {
                self.getStopCommand()
            }

            File getBaseDir() {
                new File(self.project.buildDir, 'serverBaseDir_' + getServerConfig().servletContainer)
            }

            WebAppClassPathResolver getWebAppClassPathResolver() {
                new WebAppClassPathResolver() {
                    Collection<URL> resolveWebAppClassPath(WebAppConfig wconfig) {
                        Set<URL> resolvedClassPath = new LinkedHashSet<URL>()
                        if (wconfig.projectPath) {
                            def proj = self.project.project(wconfig.projectPath)
                            String runtimeConfig = 'runtimeClasspath'
                            resolvedClassPath.addAll(ProjectUtils.resolveClassPath(proj, wconfig.beforeClassPath))
                            resolvedClassPath.addAll(ProjectUtils.getClassPath(proj, wconfig.inplace, runtimeConfig))
                            resolvedClassPath.addAll(ProjectUtils.resolveClassPath(proj, wconfig.classPath))
                        } else {
                            for (def classpath in [wconfig.beforeClassPath, wconfig.classPath]) {
                                if (classpath) {
                                    for (String elem in classpath) {
                                        URL url
                                        if (elem =~ /.{2,}\:.+/) {
                                            url = new URL(elem)
                                        } else {
                                            url = new File(elem).toURI().toURL()
                                        }
                                        resolvedClassPath.add(url)
                                    }
                                }
                            }
                        }
                        resolvedClassPath
                    }
                }
            }

            Iterable<WebAppConfig> getWebAppConfigs() {
                startConfig.getWebAppConfigs()
            }
        }
    }

    protected boolean getManagedClassReload(ServerConfig sconfig) {
        sconfig.managedClassReload
    }

    Collection<URL> getRunnerClassPath() {
        DefaultLauncher.getRunnerClassPath(project, getStartConfig().serverConfig)
    }

    protected abstract StartConfig getStartConfig()

    protected abstract String getStopCommand()

    /**
     * key is context path, value is collection of classpath URLs
     * @return
     */
    Map<String, Collection<URL>> getWebappClassPaths() {
        LauncherConfig config = getLauncherConfig()
        WebAppClassPathResolver resolver = config.getWebAppClassPathResolver()
        config.getWebAppConfigs().collectEntries { wconfig ->
            [wconfig.contextPath, resolver.resolveWebAppClassPath(wconfig)]
        }
    }

    final void prepareServerConfig(Closure closure) {
        prepareServerConfigClosures.add(closure)
    }

    final void prepareWebAppConfig(Closure closure) {
        prepareWebAppConfigClosures.add(closure)
    }
}
