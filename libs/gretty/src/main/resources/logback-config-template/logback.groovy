scan '30 seconds'

String encoderPattern = '%-12date{HH:mm:ss} %-5level %logger{35} - %msg%n'
String logDir = "$logDir"
String logFileName = "$logFileName"

def appenders = []

< % if (consoleLogEnabled) {
    % >
    appender('CONSOLE', ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = encoderPattern
        }
    }
    appenders.add('CONSOLE')
    < %
} % >

< % if (fileLogEnabled) {
    % >
    appender('FILE', RollingFileAppender) {
        file = "\${logDir}/\${logFileName}.logger"
        append = true
        rollingPolicy(TimeBasedRollingPolicy) {
            fileNamePattern = "\${logDir}/\${logFileName}-%d{yyyy-MM-dd_HH}.logger"
            maxHistory = 7
        }
        encoder(PatternLayoutEncoder) {
            pattern = encoderPattern
        }
    }
    appenders.add('FILE')
    < %
} % >

root $loggingLevel, appenders
