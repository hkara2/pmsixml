package fr.gpmsi.pmsixml.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Appli de test de log normal.
 */
public class Log4j2NormalLogging {
  static Logger lg = LogManager.getLogger();
  
  /**
   * Lancement de l'appli
   * @param args Arguments (non utilisés)
   */
  public static void main(String[] args) {
    /*
     * Config of log4j2.xml is :
     * <Loggers>
     *   <Root level="error">
     *     <AppenderRef ref="Console"/>
     *   </Root>
     * </Loggers>
     */
    lg.info("An info"); //this will not show
    lg.debug("some debugging" ); //this will not show
    lg.warn("A warning"); //this will not show
    lg.error("An error, from logger " + lg.getName()); //this will show
  }
}
