package fr.gpmsi.pmsixml.tests;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Normalement le niveau est "error", ici on peut le changer par programme.
 * cf.
 * https://logging.apache.org/log4j/log4j-2.4/faq.html#set_logger_level_from_code
 * 
 * @author hkaradimas
 *
 */
public class Log4j2LogLevel {

  static Logger lg = LogManager.getLogger();
  
  public static void main(String[] args) {
    LoggerContext context = (LoggerContext) LogManager.getContext(false); //must use "false"
    Configuration config = context.getConfiguration();
    LoggerConfig rootLoggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
    rootLoggerConfig.setLevel(Level.DEBUG);
    context.updateLoggers();
    
    // now level is debug
    lg.debug("Hello, just debugging as usual.");
  }
}
