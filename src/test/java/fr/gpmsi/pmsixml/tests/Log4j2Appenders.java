package fr.gpmsi.pmsixml.tests;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class Log4j2Appenders {

  static Logger lg = LogManager.getLogger();
  
  private void run() {
    FileAppender newAppender = FileAppender.newBuilder()
        .setName("mylogfile")
        .withFileName("mylogfile.txt")
        .build();
    
    LoggerContext ctxt = (LoggerContext) LogManager.getContext(false);
    
    AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
    AppenderRef[] refs = new AppenderRef[] {ref};
    
    Configuration config = ctxt.getConfiguration();
    config.addAppender(newAppender);
    config.start();
    
    //!deprecated ! LoggerConfig loggerConfig = LoggerConfig.createLogger("true", Level.ERROR, "org.apache.logging.log4j", "true", refs, null, config, null );
    //!also deprecated ! LoggerConfig loggerConfig = LoggerConfig.createLogger(true, Level.ERROR, "org.apache.logging.log4j", "true", refs, null, config, null );
    //we have to use a builder now.
    LoggerConfig loggerConfig = LoggerConfig.newBuilder()
    		.withAdditivity(true)
    		.withLevel(Level.ERROR)
    		.withLoggerName("org.apache.logging.log4j")
    		.withIncludeLocation("true")
    		.withRefs(refs)
    		.withConfig(config)
    		.build();
    loggerConfig.addAppender(newAppender, null, null);
    
    config.addAppender(newAppender);
    config.addLogger("fr.gpmsi.pmsixml.tests.Log4j2Appenders", loggerConfig);
    //ctx.updateLoggers();
    
    ctxt.updateLoggers(config);
    
    lg.error("Hello new logger");
    Logger lg2 = LogManager.getContext().getLogger("fr.gpmsi.pmsixml.tests.Log4j2Appenders");
    lg2.error("Hello from lg2");
  }
  
  public static void main(String[] args) {
    new Log4j2Appenders().run();
  }

}
