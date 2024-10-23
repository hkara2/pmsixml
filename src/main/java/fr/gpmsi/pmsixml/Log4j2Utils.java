package fr.gpmsi.pmsixml;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Utilitaires pour l'utilisation de Log4j.
 * @author hkaradimas
 *
 */
public class Log4j2Utils {

  /**
   * Changer le log Level du Logger racine 
   * @param newLevel le nouveau Level
   */
  public static final void changeRootLogLevel(Level newLevel) {
    changeLoggerLevel(LogManager.ROOT_LOGGER_NAME, newLevel);
  }
  
  /**
   * Changer le log Level d'un Logger particulier
   * @param loggerName Le nom du logger
   * @param newLevel le nouveau niveau
   */
  public static final void changeLoggerLevel(String loggerName, Level newLevel) {
    LoggerContext context = (LoggerContext) LogManager.getContext(false); //must use "false"
    Configuration config = context.getConfiguration();
    LoggerConfig rootLoggerConfig = config.getLoggerConfig(loggerName);
    if (rootLoggerConfig == null) return; //not found, do nothing
    rootLoggerConfig.setLevel(newLevel);
    context.updateLoggers();    
  }
  
  /**
   * Attacher un FileAppender à un Logger particulier.
   * @param loggerName Le nom du logger
   * @param aa Les paramètres pour contrôler le FileAppender à rattacher
   */
  public static final void attachFileAppender(String loggerName, FileAppenderAttributes aa) {
    String name = aa.name;
    String fileName = aa.fileName;
    Layout<?> lay = aa.layout;
    boolean append = aa.append;
    //Level level = aa.level;
    FileAppender fa = FileAppender.newBuilder()
        .setName(name)
        .withFileName(fileName)
        .setLayout(lay)
        .withAppend(append)
        .build();
    LoggerContext ctxt = (LoggerContext) LogManager.getContext(false);
    
    AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
    AppenderRef[] refs = new AppenderRef[] {ref};
    
    Configuration config = ctxt.getConfiguration();
    config.addAppender(fa);
    config.start();
    
    //!deprecated ! LoggerConfig loggerConfig = LoggerConfig.createLogger(true, Level.ERROR, loggerName, null, refs, /*properties*/null, config, /*filter*/ null);
    //now using builder
    LoggerConfig loggerConfig = LoggerConfig.newBuilder()
        .withAdditivity(true)
        .withLevel(Level.ERROR)
        .withLoggerName(loggerName)
        .withRefs(refs)
        .withConfig(config)
        .build();
    loggerConfig.addAppender(fa, null, null);
    
    config.addAppender(fa);
    config.addLogger(loggerName, loggerConfig);
    
    ctxt.updateLoggers(config);
  }

  /**
   * Créer un Layout TTCC (Time, Thread, Category, Context) qui est un des layouts couramment utilisés 
   * @return Le layout TTCC
   */
  public static PatternLayout makeTTCCLayout() {
    return PatternLayout.newBuilder().withPattern(PatternLayout.TTCC_CONVERSION_PATTERN).build();
  }
  
  /**
   * Classe utilitaire pour stocker les attributs utilisables pour créer un FileAppender
   * @author hkaradimas
   */
  public static class FileAppenderAttributes {
    /** constructeur simple */
    public FileAppenderAttributes() {}
    /** Le nom de l'appender */
    public String name;
    /** Le nom du fichier */
    public String fileName;
    /** Est-ce qu'on ajoute au contenu ou non */
    public boolean append = true;
    /** La disposition des messages (par défaut PatternLayout.createDefaultLayout()) */
    public Layout<?> layout = PatternLayout.createDefaultLayout();
  }
  
  private Log4j2Utils() {}
}
