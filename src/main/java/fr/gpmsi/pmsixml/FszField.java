package fr.gpmsi.pmsixml;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Un noeud de type champ, qui contient une valeur.
 * @author hkaradimas
 *
 */
public class FszField
extends FszNode
{
  static Logger lg = LogManager.getLogger(FszField.class);
  
  private static SimpleDateFormat pmsidf = new SimpleDateFormat("ddMMyyyy");
  private static SimpleDateFormat nxdf = new SimpleDateFormat("yyyyMMdd");
  private static DateTimeFormatter pmsildf = DateTimeFormatter.ofPattern("ddMMyyyy");
  private static DateTimeFormatter nxldf = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static SimpleDateFormat fdf = new SimpleDateFormat("dd/MM/yyyy"); //french date format
  
  FszFieldMeta meta;
  String value;
  
  /** position dans la ligne source (commence à 0) */
  int pos;
  
  public FszField(FszFieldMeta meta) {
    this.meta = meta;
  }

  @Override
  public
  boolean isField() { return true; }

  @Override
  public
  boolean isFieldGroup() { return false; }

  @Override
  public
  FszMeta getMeta() { return meta; }

  @Override
  public
  int getChildCount() { return 0; }

  @Override
  public
  List<FszNode> getChildren() { return null; }

  /**
   * Retourner la valeur
   * @return La valeur
   */
  public String getValue() { return value; }

  /**
   * Attribuer la valeur
   * @param value la valeur
   */
  public void setValue(String value) { this.value = value; }
  
  /**
   * Attribuer la valeur puis ajuster le centrage et le remplissage
   * @param value La valeur
   */
  public void setValueAdjusted(String value) { setValue(value); adjustValue(); }
  
  /**
   * Mettre une date dans le champ
   * @param value La date à mettre (peut être null)
   */
  public void setValue(Date value) {
    if (value == null) this.value = null;
    setValue(pmsidf.format(value));
  }
  
  /**
   * Attribuer la valeur puis ajuster le centrage et le remplissage
   * @param value La valeur
   */
  public void setValueAdjusted(Date value) { setValue(value); adjustValue(); }

  /**
   * Mettre un entier long dans le champ. L'entier sera transformé en fonction du format qui a été défini, c'est à dire
   * que par exemple si le format est 8+2, l'entier sera multiplié par 100.
   * @param value La valeur à mettre
   */
  public void setValue(Long value) {
    if (value == null) this.value = null;
    else setValue(BigDecimal.valueOf(value)); //astuce, on réutilise le setValue() qui utilise un BigDecimal
  }
  
  /**
   * Attribuer la valeur puis ajuster le centrage et le remplissage
   * @param value La valeur
   */
  public void setValueAdjusted(Long value) { setValue(value); adjustValue(); }

  /**
   * Mettre un entier long dans le champ tel quel, sans transformation
   * @param value La valeur à mettre
   */
  public void setValueUnchanged(Long value) {
    if (value == null) this.value = null;
    else this.value = String.valueOf(value);
  }
  
  /**
   * Attribuer la valeur puis ajuster le centrage et le remplissage
   * @param value La valeur
   */
  public void setValueUnchangedAdjusted(Long value) { setValue(value); adjustValue(); }

  /**
   * Mettre un BigDecimal dans le champ. La valeur est convertie en utilisant le format (par exemple si le format est 8+2
   * la valeur sera multipliée par 100 avant d'être stockée).
   * @param value La valeur à mettre
   */
  public void setValue(BigDecimal value) {
    if (value == null) this.value = null;
    else {
      BigDecimal corrected = value.multiply(meta.getMultiplicativeFactor()).divideToIntegralValue(BigDecimal.ONE); 
      this.value = corrected.toPlainString();
    }
  }
  
  /**
   * Attribuer la valeur puis ajuster le centrage et le remplissage
   * @param value La valeur
   */
  public void setValueAdjusted(BigDecimal value) { setValue(value); adjustValue(); }

  /**
   * Mettre un BigDecimal dans le champ sans l'ajuster avec les valeurs de formatage
   * @param value La valeur à mettre
   */
  public void setValueUnchanged(BigDecimal value) {
    if (value == null) this.value = null;
    else this.value = value.toPlainString();
  }
  
  /**
   * Attribuer la valeur puis ajuster le centrage et le remplissage
   * @param value La valeur
   */
  public void setValueUnchangedAdjusted(BigDecimal value) { setValue(value); adjustValue(); }

  /**
   * Ajuster la valeur à l'aide de ce qui est déclaré dans les métadonnées (Zéro ou Espace ou NA, Gauche ou Droite ou NA).
   * On tolère les différences de casse, et aussi l'utilisation de ZERO (E sans accent). Cf. 
   * {@link FszFieldMeta#isZeroFilled()},
   * {@link FszFieldMeta#isSpaceFilled()}, etc. 
   * @return La chaîne de caractères ajustée
   */
  public String getAdjustedValue() {
    int len = meta.getSize();
    StringBuilder sb = new StringBuilder(len);
    if (value != null) sb.append(value);
    char fillChar = ' '; //par défaut on prend l'espace comme caractère de remplissage
    if (meta.isSpaceFilled()) {
      fillChar = ' ';
    }
    else if (meta.isZeroFilled()) {
      fillChar = '0';
    }
    if (meta.isLeftAligned()) {
      while (sb.length() < len) sb.append(fillChar); //remplir avec des caractères à droite
    }
    else if (meta.isRightAligned()) {
      while (sb.length() < len) sb.insert(0, fillChar); //remplir avec des caractères à gauche
    }
    return sb.toString();
  }

  /**
   * Ajuster la valeur pour qu'elle soit correctement alignée avec les caractères souhaités.
   * Par défaut ce n'est pas fait pour les méthodes setValue() ; il faut alors explicitement appeler cette méthode pour que la justification ait lieu.
   * Sinon on peut utiliser setValueAdjusted() qui appelle setValue() puis adjustValue() juste après.
   */
  public void adjustValue() {
    this.value = getAdjustedValue();
  }
  
  /**
   * Récupérer la valeur en tant que texte. Si value est null, retourne une
   * chaîne vide. Sinon, retourne la valeur apprès application de trim() qui
   * supprime les espaces en début et fin de chaîne.
   * @return Valeur en texte
   */
  public String getValueAsText() {
    if (value == null) return "";
    return value.trim();
  }
  
  /**
   * Retourner la valeur formatée pour envoi en texte mais en fonction du
   * type préféré. Les dates sortent ainsi avec le format jj/mm/aaaa qui est 
   * correctement interprété par les tableurs. Les nombres sortent sans les zéros 
   * initiaux. Les textes sortent sans les espaces de début et de fin.
   * C'est la méthode à utiliser pour une sortie vers du CSV.
   * @return Valeur formatée
   * @throws ParseException Si erreur d'analyse
   */
  public String getFormattedValue() 
      throws ParseException
  {
    FszFieldMeta m = (FszFieldMeta) getMeta();
    String typ = m.getPreferredType();
    if (typ.equals("N")) {
      if (value == null || value.trim().equals("")) return "";
      else {
        return String.valueOf(toInt()); //convertir en nombre puis en texte à nouveau
      }
    }
    else if (typ.equals("D")) {
      if (value == null || value.trim().equals("")) return "";
      Date val = getValueAsEuropeanDate();
      if (val == null) return value; //on n'a pas réussi à convertir la date (par ex "00000000" dans les champs du RSF), renvoyer tel quel.
      else return fdf.format(getValueAsEuropeanDate()); //convertir en date puis à nouveau en texte avec le format de date français 
    }
    else {
      return getValueAsText();
    }
    
  }
  
  /**
   * Retourner la valeur en tant qu'entier naturel. Si le champ est vide, renvoie 0.
   * @return Valeur en int
   */
  public int getValueAsInt() {
    if (isEmpty(value)) return 0;
    return Integer.parseInt(value.trim());
  }
  
  /**
   * raccourci pour {@link #getValueAsInt()} 
   * @return Valeur en int
   */
  public int toInt() {
  	return getValueAsInt();
  }
  
  /**
   * Retourner la valeur en tant que BigDecimal (permet de gérer la conversion de très grands nombres).
   * 
   * @return la valeur ou null si la valeur est vide
   * @throws NumberFormatException si la valeur n'est pas une valeur numérique.
   */
  public BigDecimal getValueAsBigDecimal() {
    if (isEmpty()) return null;
    try {
      return NumUtils.parse(getValue());
    }
    catch (NumberFormatException nfe) {
      //intercepter l'exception pour montrer où est le problème
      lg.error("Mauvais format de nombre '"+getValue()+"'");
      throw nfe;
    }
  }
  
  /**
   * Retourne la valeur corrigée, en utilisant le format utilisé lors de la définition.
   * Par exemple pour un nombre au format 5+3 , pour 1536456 le nombre retourné sera 1536.456
   * Même si le champ est déclaré 'N', il vaut mieux toujours gérer l'exception
   * NumberFormatException si on veut continuer l'exécution malgré une mauvaise valeur 
   * d'entrée.
   * @return La valeur corrigée ou null si la valeur de départ est vide
   * @throws NumberFormatException si la valeur n'est pas une valeur numérique.
   */
  public BigDecimal getCorrectedValue() {
    BigDecimal rawValue = getValueAsBigDecimal();
    if (rawValue == null) return null;
    return rawValue.divide(meta.getMultiplicativeFactor());
  }
  
  /**
   * raccourci pour {@link #getValueAsBigDecimal()}
   * @return la valeur ou 0 (zéro) si la valeur est vide
   */
  public BigDecimal toBigDecimal() {
    return getValueAsBigDecimal();
  }
  
  /**
   * Retourner la valeur en tant que date si elle est compatible avec une date au format
   * "Européen", c'est à dire jjmmaaaa (ou aaaammjj pour les fichiers nx).
   * @return la date ou null si la valeur est vide ou ne contient que des zéros
   * @throws ParseException _
   */
  public Date getValueAsEuropeanDate()
      throws ParseException
  {
    Date r;
    if (value.trim().isEmpty()) return null;
    if (NumUtils.onlyZeroes(value.trim())) return null;
    if (getParent().getMeta().getRoot().getReadStrategy().getName().equals("NX")) {
      synchronized (nxdf) {
        r = nxdf.parse(value); 
      }
    }
    else {
      synchronized (pmsidf) {
        r = pmsidf.parse(value);
      }
    }
    return r;
  }
  
  /**
   * Retourner la valeur en tant que localdate si elle est compatible avec une localdate au format PMSI
   * "Européen", c'est à dire jjmmaaaa (ou aaaammjj pour les fichiers nx).
   * @return la localdate ou null si la valeur est vide ou ne contient que des zéros
   */
  public LocalDate getValueAsEuropeanLocalDate()
  {
    if (value.trim().isEmpty()) return null;
    if (NumUtils.onlyZeroes(value.trim())) return null;
    LocalDate ld;
    if (getParent().getMeta().getRoot().getReadStrategy().getName().equals("NX")) {
      ld = LocalDate.parse(value, nxldf);
    }
    else {
      ld = LocalDate.parse(value, pmsildf);
    }
    return ld;
  }
  
  /**
   * raccourci pour {@link #getValueAsEuropeanDate()}
   * @return La date
   * @throws ParseException _
   */
  public Date toDate() throws ParseException { return getValueAsEuropeanDate(); }
  
  /**
   * raccourci pour {@link #getValueAsEuropeanLocalDate()}
   * @return la localdate
   */
  public LocalDate toLocalDate() { return getValueAsEuropeanLocalDate(); }
  
  /**
   * Lire le champ sur deux niveaux. (Ancienne méthode, remplaçée par {@link #read(InputString)})
   * @param in L'entrée
   * @param repetitionCountByName La table des compteurs
   * @param repetionCountFieldByName La table des champs qui correspondent aux compteurs
   * @throws FieldParseException Si erreur d'analyse
   */
  public void readFieldValue__old(InputString in, Map<String, Integer> repetitionCountByName, Map<String, String> repetionCountFieldByName)
      throws FieldParseException 
  {
    int sz = meta.getSize();
    if (in.line.length() < in.pos+sz) {
      if (in.acceptTruncated) {
        value = in.line.substring(pos); //prendre la valeur telle qu'elle est
        StringBuffer sb = new StringBuffer();
        //la convertir en texte de la bonne longueur
        try { toText(sb); } catch (FieldSizeException ignored) {}
        value = sb.toString();
      }
      else {
        throw new FieldParseException("Longueur de ligne atteinte prematurement lors de la lecture du champ '"+meta.getLongName()+"', position "+in.pos+",a la ligne "+in.lineNumber);
      }
    }
    else {
      value = in.line.substring(in.pos, in.pos + sz);
    }
    pos = in.pos;
    in.pos += sz;
    if (!isEmpty(meta.getFieldCounter())) {
      //si ce champ est un champ compteur, mettre à jour la valeur de compteur
      //lg.debug("Setting counter '"+meta.getFieldCounter()+"' to "+getValueAsInt());      
      repetitionCountByName.put(meta.getFieldCounter(), getValueAsInt());
      //mettre aussi à jour le nom du champ qui contient le compteur
      repetionCountFieldByName.put(meta.getFieldCounter(), meta.getStdName());
    }
  }
  
  @Override
  public void read(InputString in) 
          throws FieldParseException 
  {
    int sz = meta.getSize();
    int end = in.pos+sz;
    if (in.line.length() < in.pos+sz) {
        if (in.acceptTruncated) end = in.line.length();
        else {
          throw new FieldParseException("Longueur de ligne atteinte prematurement lors de la lecture du champ '"+meta.getLongName()+"', position "+in.pos+",a la ligne "+in.lineNumber);
        }
    }
    if (in.pos > end) {
      throw new FieldParseException("position courante "+in.pos+" apres la fin "+end+" pour " + meta.stdName + " ("+meta.size+")");
    }
    value = in.line.substring(in.pos, end);
    if (value.length() < sz) {
      StringBuffer sb = new StringBuffer(); 
      try { toText(sb); } catch (FieldSizeException ignored) {} 
      value = sb.toString();
    }
    pos = in.pos;
    in.pos += sz;
    if (!isEmpty(meta.getFieldCounter())) {
        if (parent == null) {
          String msg = "Erreur pas de parent pour rechercher le compteur '"+meta.getFieldCounter()+"'";
          throw new RuntimeException(msg);          
        }
        //mettre la valeur du compteur dans le groupe parent
        parent.getCountersByName().put(meta.getFieldCounter(), getValueAsInt());
        parent.getCounterFieldsByName().put(meta.getFieldCounter(), meta.stdName);
    }
  }
  
  @Override
  public void dump(StringBuffer sb, int level) {
    for (int i = 0; i < level; i++) { sb.append("  "); }
    sb.append(meta.getStdName());
    sb.append(' ');
    sb.append(meta.getLongName());
    sb.append(':');
    sb.append('\''); sb.append(value); sb.append("\' ::"); 
    sb.append(pos);
    sb.append('\n');
  }
  
  @Override
  public void dumpStructure(StringBuffer sb, int level) {
    //pour la structure, on ne liste pas les champs simples
  }
  
  /**
   * Ce champ représente-t-il une date au format européen ?
   * @return True si les métadonnées indiquent que ce champ représente une date au format européen
   */
  public boolean representsEuropeanDate() {
    return meta.representsEuropeanDate(); 
  }
  
  /**
   * Ce champ représente-t-il un nombre ?
   * 
   * @return True si les métadonnées indique que ce champ représente un nombre
   */
  public boolean representsNumber() {
    return meta.representsNumber();
  }
  
  /**
   * Renvoie nom du champ + ':' + valeur
   * @return une représentation du champ 
   */
  @Override
  public String toString() {
    return meta.getStdName()+":"+getValue();
  }

  /**
   * Renvoie la position de ce champ (commence à 0)
   * @return la position
   */
  public int getPos() {
    return pos;
  }

  /**
   * Attribue la position de ce champ (commence à 0)
   * @param pos La position
   */
  public void setPos(int pos) {
    this.pos = pos;
  }
 
  /**
   * Compare ce champ ave un autre. Les champs sont égaux s'ils ont même valeur
   * et si leurs métadonnées sont égales.
   * @return true si les champs sont égaux
   */
  @Override
  public boolean equals(Object obj) {
  	if (obj == null) return false;
  	FszField b = (FszField) obj;
  	if (!ObjectUtils.safeEquals(meta, b.meta)) return false;
  	return ObjectUtils.safeEquals(value, b.value);
  }

  /**
   * Convertir la "valeur" (variable <i>value</i>) du champ en texte, de la bonne longueur.
   * Si la taille en caractères de la valeur est trop courte pour le champ, le champ est
   * complété avec un caractère de remplissage.
   * Le caractère de remplissage est '0' si la valeur de la définition est 'zero' ou 'zéro' 
   * (sans distinction majuscules/minuscules), sinon c'est ' '.
   * Si la valeur de définition de l'ajustement est 'gauche' (sans distinction majuscules/minuscules)
   * la valeur est ajustée à gauche, c'est à dire en rajoutant les caractères de remplissage 
   * à la droite de la valeur. Sinon la valeur est ajustée à droite, c'est à dire
   * en rajoutant les caractères de remplissage à gauche de la valeur.
   * Si la valeur est déjà à la bonne taille, elle est envoyée telle quelle.
   * Donc si l'on veut envoyer un champ non rempli, il faut mettre des espaces de la
   * bonne taille. Par exemple, si un champ est défini avec "Zéro/Droite", de longueur
   * 4, et est à "" (longueur 0), toText() enverra '0000'. S'il est à '   ' (3 espaces),
   * toText() renverra '   0'. S'il est à '    ' (4 espaces), toText() le renverra
   * tel quel, puisqu'il n'a pas besoin de le compléter.
   * @throws FieldSizeException Si la valeur est trop longue pour entrer dans le champ
   */
	@Override
	public void toText(StringBuffer sb)
			throws FieldSizeException 
	{
	  String v = value;
	  if (v == null) v = "";
		FszFieldMeta fm = (FszFieldMeta) getMeta();
		int len = fm.getSize();
		if (v.length() > len) {
			throw new FieldSizeException("Valeur '"+v+"' depasse la taille du champ : "+len);
		}
		//determiner le caractère de remplissage. Par sécurité, on n'autorise le zéro que si on est justifié à droite
		char fc = fm.isZeroFilled() ? ( fm.isLeftAligned() ? ' ' : '0') : ' ';
		if (fm.isLeftAligned()) {
			sb.append(v);
			for (int i = v.length(); i < len; i++) sb.append(fc);
		}
		else {
			//right aligned
			for (int i = v.length(); i < len; i++) sb.append(fc);
			sb.append(v);
		}
	}

	/**
	 * Est-ce que cette structure est un conteneur ?
	 * @return false ici
	 */
	@Override
	public boolean isContainer() { return false; }

	/**
	 * Transférer la valeur de ce champ au "prepared statement" à l'index donné.
	 * @param ps Le ps
	 * @param index L'index (commence à 1)
	 * @throws SQLException Exception
	 * @throws ParseException Exception
	 */
	public void transferTo(PreparedStatement ps, int index)
	    throws SQLException, ParseException 
	{
	  FszFieldMeta m = (FszFieldMeta) getMeta();
	  String typ = m.getPreferredType();
	  if (typ.equals("N")) {
	    if (value == null) ps.setNull(index, Types.NUMERIC);
	    else {
	      BigDecimal bd = NumUtils.parse(value);
	      if (bd == null) ps.setNull(index, Types.NUMERIC);
	      else ps.setBigDecimal(index, bd);
	    }
	  }
	  else if (typ.equals("D")) {
	    if (value == null) ps.setNull(index, Types.TIMESTAMP);
	    else ps.setTimestamp(index, new Timestamp(getValueAsEuropeanDate().getTime()));
	  }
	  else {
	    if (value == null) ps.setNull(index, Types.VARCHAR);
	    else ps.setString(index, value);
	  }
	}

	/**
	 * Est-ce que le champ est vide ?
	 * Le champ est vide si sa valeur est null ou bien s'il ne contient que des espaces.
	 * @return true si le champ est vide
	 */
	public boolean isEmpty() { return value == null || value.trim().length() == 0; }
	
}
