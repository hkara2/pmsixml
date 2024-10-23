package fr.gpmsi.pmsixml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Injecter un group mono-niveau dans une table de base de données via un {@link PreparedStatement}.
 * Les types préférés sont utilisés
 * Les noms de colonne sont les noms standard des champs.
 * @author hkaradimas
 *
 */
public class MonoLevelTableInjector {
  Connection cxn;
  
  PreparedStatement insPs;
  
  boolean truncatedInputAccepted = true;
  
  /** constructeur simple */
  public MonoLevelTableInjector() {}
  
  /**
   * Injecter dans une table JDBC le contenu apporté par le Reader, en utilisant le MonoLevelReader pour la transformation.
   * @param rdr Le Reader à utiliser pour lire les données
   * @param mlr L'objet MonoLevelReader à utiliser
   * @param tableName Le nom de la table à utiliser dans les requêtes
   * @throws IOException Si erreur E/S
   * @throws FieldParseException Si erreur d'analyse
   * @throws MissingMetafileException Si un fichier de métadonnées n'a pas été trouvé
   * @throws ParseException Si erreur d'analyse
   * @throws SQLException Si erreur SQL lors des opérations de base de données
   */
  public void inject(Reader rdr, MonoLevelReader mlr, String tableName)
      throws IOException, FieldParseException, MissingMetafileException, ParseException, SQLException
  {
    BufferedReader br = new BufferedReader(rdr);
    try {
      @SuppressWarnings("unused")
      int linenr = 0;
      String line;
      InputString is = null;
      while ((line = br.readLine()) != null) {
        if (is == null) { is = new InputString(line); is.acceptTruncated = truncatedInputAccepted; }
        else is.nextLine(line);
        FszGroup g = mlr.readMonoLevel(is);
        inject(g, tableName);
        linenr++;
      }//while      
    }
    finally {
      br.close();
    }
  }
  
  /**
   * Injecter un groupe mono-niveau, en utilisant INSERT. Le commit n'est pas fait.
   * Le PreparedStatement est réutilisé, donc tous les groupes injectés doivent être de même structure.
   * @param mono Le {@link FszGroup} qui contient le groupe mono-niveau
   * @param tableName Le nom de la table dans laquelle injecter
   * @throws ParseException _ 
   * @throws SQLException  _
   */
  public void inject(FszGroup mono, String tableName)
      throws ParseException, SQLException 
  {
    ArrayList<Object> values = new ArrayList<>();
    ArrayList<Character> types = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    for (FszNode child : mono.getChildren()) {
      FszMeta childMeta = child.getMeta();
      String name = childMeta.getStdName();
      FszField fld = (FszField) child;
      String typ = childMeta.asFieldMeta().getPreferredType();
      if (typ == null || typ.length() == 0) typ = "A";
      char c = typ.charAt(0); 
      switch (c) {
      case 'N':
        types.add(c); values.add(fld.getValueAsInt());
        break;
      case 'D':
        types.add(c); values.add(fld.getValueAsEuropeanDate());
        break;
      default:
        types.add('A'); values.add(fld.getValue());
      }//case
      names.add(name);
    }//for
    if (insPs == null) {
      StringBuffer sb = new StringBuffer();
      StringBuffer sb2 = new StringBuffer();
      sb.append("INSERT INTO "+tableName+"(");
      boolean first = true;
      for (String name : names) {
        if (first) first = false; else { sb.append(","); sb2.append(","); }
        sb.append(name); sb2.append('?');
      }
      sb.append(") VALUES (");
      sb.append(sb2.toString());
      sb.append(")");
      insPs = cxn.prepareStatement(sb.toString());      
    }
    try {
      for (int i = 0; i < types.size(); i++) {
        Object value = values.get(i);
        char c = types.get(i);
        switch (c) {
        case 'N':
          if (value == null) insPs.setNull(i+1, Types.NUMERIC);
          else insPs.setInt(i+1, (Integer)value);                
          break;
        case 'D':
          if (value == null) insPs.setNull(i+1, Types.DATE);
          else insPs.setDate(i+1, new java.sql.Date(((Date)value).getTime()));                
          break;
        default:
          if (value == null) insPs.setNull(i+1, Types.VARCHAR);
          else insPs.setString(i+1, String.valueOf(value));        
        }//switch
      }//for
      insPs.executeUpdate();
    }
    finally {
      insPs.clearParameters();
    }
  }//inject

  /**
   * Fermeture de l'injecteur 
   * @throws SQLException Si erreur de base de données pendant la fermeture
   */
  public void close()
      throws SQLException
  {
    if (insPs != null) insPs.close();
  }

  /**
   * Retourner la connexion
   * @return la connexion
   */
  public Connection getConnection() {
    return cxn;
  }

  /**
   * Définir la connexion JDBC à utiliser
   * @param connection La connexion
   */
  public void setConnection(Connection connection) {
    this.cxn = connection;
  }

  /**
   * Est-ce que les entrées tronquées (c'est à dire les lignes trop courtes) sont acceptées.
   * Par défaut c'est oui.
   * @return true si les entrées tronquées sont acceptées
   */
  public boolean isTruncatedInputAccepted() {
    return truncatedInputAccepted;
  }

  /**
   * Est-ce que les entrées tronquées (c'est à dire les lignes trop courtes) sont acceptées.
   * Par défaut c'est oui.
   * @param truncatedInputAccepted true si les entrées tronquées sont acceptées
   */
  public void setTruncatedInputAccepted(boolean truncatedInputAccepted) {
    this.truncatedInputAccepted = truncatedInputAccepted;
  }
  
}
