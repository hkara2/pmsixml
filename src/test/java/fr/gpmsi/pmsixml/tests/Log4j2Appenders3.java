package fr.gpmsi.pmsixml.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.gpmsi.pmsixml.Log4j2Utils;

public class Log4j2Appenders3 {
  static Logger lg = LogManager.getLogger();

  /**
   * Essayer d'autres paramètres pour faire un appender
   * @param args arguments
   */
  public static void main(String[] args) {
    Log4j2Utils.FileAppenderAttributes aa = new Log4j2Utils.FileAppenderAttributes();
    aa.name = lg.getName();
    aa.fileName = "mylogfile2.txt";
    aa.append = false;
    aa.layout = Log4j2Utils.makeTTCCLayout(); //équivalent à : PatternLayout.newBuilder().withPattern(PatternLayout.TTCC_CONVERSION_PATTERN).build();
    Log4j2Utils.attachFileAppender(lg.getName(), aa);

    // maintenant ça va marcher sur notre logger
    lg.error("Hello new logger");
    //ne marche pas :
    //Logger lg2 = LogManager.getContext().getLogger("fr.gpmsi.pmsixml.tests.Log4j2Appenders2");
    //marche :
    Logger lg3 = LogManager.getLogger(lg.getName());
    lg3.error("Hello from lg3"); //par contre ça ça ne marche toujours pas !
  }

}
