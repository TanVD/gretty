/*
 * Gretty
 *
 * Copyright (C) 2013-2015 Andrey Hihlovskiy and contributors.
 *
 * See the file "LICENSE" for copying and usage permission.
 * See the file "CONTRIBUTORS" for complete list of contributors.
 */
package org.akhikhl.gretty

import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TypeCheckingMode

/**
 *
 * @author akhikhl
 */
@CompileStatic(TypeCheckingMode.SKIP)
@ToString
class ServerConfig {

    // attention: this constant must always have the same value as PortUtils.RANDOM_FREE_PORT
    static final int RANDOM_FREE_PORT = -1

    List<String> jvmArgs
    Map<String, String> systemProperties
    String servletContainer

    Boolean managedClassReload

    String host
    Boolean httpEnabled
    Integer httpPort
    Integer httpIdleTimeout
    Boolean httpsEnabled
    Integer httpsPort
    Integer httpsIdleTimeout
    def sslKeyStorePath
    String sslKeyStorePassword
    String sslKeyManagerPassword
    def sslTrustStorePath
    String sslTrustStorePassword
    boolean sslNeedClientAuth
    def realm
    def realmConfigFile
    def serverConfigFile
    String interactiveMode
    def logbackConfigFile
    String loggingLevel
    Boolean consoleLogEnabled
    Boolean fileLogEnabled
    def logFileName
    def logDir
    List<Closure> onStart
    List<Closure> onStop

    Boolean secureRandom
    String logbackVersion
    Boolean singleSignOn
    /**
     * Tomcat-specific: Enables JNDI naming which is disabled by default.
     */
    Boolean enableNaming

    List<Integer> auxPortRange

    String portPropertiesFileName

    static ServerConfig getDefaultServerConfig(String serverName) {
        ServerConfig result = new ServerConfig()
        result.jvmArgs = []
        result.servletContainer = 'jetty9.4'
        result.managedClassReload = false
        result.httpEnabled = true
        result.httpsEnabled = false
        result.interactiveMode = 'stopOnKeyPress'
        result.loggingLevel = 'INFO'
        result.consoleLogEnabled = true
        result.fileLogEnabled = true
        result.logFileName = serverName
        result.logDir = "${System.getProperty('user.home')}/logs" as String
        result.portPropertiesFileName = 'gretty_ports.properties'
        return result
    }

    // use serverConfigFile instead
    @Deprecated
    def getJettyXmlFile() {
        serverConfigFile
    }

    // use httpPort instead
    @Deprecated
    Integer getPort() {
        httpPort
    }

    int getRandomFreePort() {
        RANDOM_FREE_PORT
    }

    void jvmArg(Object a) {
        if (a) {
            if (jvmArgs == null) {
                jvmArgs = []
            }
            jvmArgs.add(a)
        }
    }

    void jvmArgs(Object... args) {
        if (args) {
            if (jvmArgs == null) {
                jvmArgs = []
            }
            jvmArgs.addAll(args)
        }
    }

    void onStart(Closure newValue) {
        if (onStart == null) {
            onStart = []
        }
        onStart.add newValue
    }

    void onStop(Closure newValue) {
        if (onStop == null) {
            onStop = []
        }
        onStop.add newValue
    }

    void systemProperty(String name, Object value) {
        if (systemProperties == null) {
            systemProperties = [:]
        }
        systemProperties[name] = value
    }

    void systemProperties(Map<String, Object> m) {
        if (m) {
            if (systemProperties == null) {
                systemProperties = [:]
            }
            systemProperties << m
        }
    }
}
