appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%clr(%d{HH:mm:ss.SSS}){faint} [%thread] --%X{traceId} %-5level %logger{36} - %msg%n"
    }
}

root(INFO, ["CONSOLE"])

//        pattern = "%clr(%d{\${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd HH:mm:ss.SSS}}){faint} "
//        + "%clr(\${LOG_LEVEL_PATTERN:-%5p}) %clr(\${PID:- }){magenta} %clr(---){faint} "
//        + "%clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} "
//        + "%clr(:){faint} %m%n\${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}"