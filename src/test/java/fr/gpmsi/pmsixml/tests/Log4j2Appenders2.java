package fr.gpmsi.pmsixml.tests;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class Log4j2Appenders2 {
  static Logger lg = LogManager.getLogger();

  /**
   * Essayer d'autres paramètres pour faire un appender
   * @param args arguments
   */
  public static void main(String[] args) {
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
    
    // !deprecated ! LoggerConfig loggerConfig = LoggerConfig.createLogger(true, Level.ERROR, "myxlogger", null, refs, /*properties*/null, config, /*filter*/ null);
    //using builder now
    LoggerConfig loggerConfig = LoggerConfig.newBuilder()
    		.withAdditivity(true)
    		.withLevel(Level.ERROR)
    		.withLoggerName("myxlogger")
    		.withRefs(refs)
    		.withConfig(config)
    		.build();
    loggerConfig.addAppender(newAppender, null, null);
    
    config.addAppender(newAppender);
    config.addLogger("fr.gpmsi.pmsixml.tests.Log4j2Appenders2", loggerConfig);
    
    ctxt.updateLoggers(config);

    // maintenant ça va marcher sur notre logger
    lg.error("Hello new logger2");
    //ne marche pas :
    //Logger lg2 = LogManager.getContext().getLogger("fr.gpmsi.pmsixml.tests.Log4j2Appenders2");
    //marche :
    Logger lg2 = LogManager.getLogger("fr.gpmsi.pmsixml.tests.Log4j2Appenders2");
    lg2.error("Hello from lg2"); //par contre ça ça ne marche toujours pas !
  }

}
