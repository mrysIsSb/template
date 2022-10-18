appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] --%X{traceId} %-5level %logger{36} - %msg%n"
    }
}

root(INFO, ["CONSOLE"])