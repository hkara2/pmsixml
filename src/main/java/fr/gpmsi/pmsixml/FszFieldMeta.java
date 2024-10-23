package fr.gpmsi.pmsixml;

import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Méta information sur un champ, ce qui permet de définir ce que contient ce champ, et 
 * d'utiliser ces informations pour la lecture, les calculs et la présentation.
 * 
 * <p>
 * champs csv utilisés (S = String, I = Integer), avec leur numero (commence à 0) :
 * 
 * <ul>
 * <li> 0 : fieldGroupName (S) : code du groupe (ex. AH pour Anohosp, VH pour Vidhosp)
 * <li> 1 : longName (S) : nom long pour affichage
 * <li> 2 : stdName (S) : nom standard abrevie pour reference et utilisation en nom de colonne 
 * <li> 3 : size (I) : taille en nombre de caracteres
 * <li> 4 : start (I) : position de debut (commence a 1)
 * <li> 5 : end (I) : position de fin (incluse, commence a 1)
 * <li> 6 : mandatory (S) : obligatoire (O Obligatoire, F Facultatif)
 * <li> 7 : typeOrig (S) : type original (A Alphabetique, N Numerique)
 * <li> 8 : typePref (S) : type prefere, a utiliser (A Alphabetique, N Numerique, D Date)
 * <li> 9 : alignment/filling (S)  : alignement/remplissage (NA/NA;Gauche/Espace ; Droite/Zéro)
 * <li> 10 : remarks (S) : remarques générales
 * <li> 11 : fieldCounter (S) : si ce champ est un compteur, ici est le champ pour lequel cette valeur est le compteur
 * <li> 12 : format (S) : donne le format nn+nn à utiliser (par ex. 8+2 pour un format 8 décimales avant la virgule et 2 après)
 * </ul>
 * 
 * @author hkaradimas
 *
 */
public class FszFieldMeta
extends FszMeta
{
  static Logger lg = LogManager.getLogger(FszFieldMeta.class);
  
  FszMeta parent;
  
  String fieldGroupName; //pos. 0
  String longName; //pos. 1
  String stdName; //pos. 2
  Integer size; //pos. 3
  Integer start; //pos. 4
  Integer end; //pos. 5
  String mandatory; //pos. 6
  String typeOrig; //pos. 7
  String typePref;   //pos. 8 
  String alignment; //pos. 9.0
  String filling; //pos. 9.1
  String remarks; //pos. 10
  String fieldCounter; //pos. 11 
  String format; //pos 12
  int precision = 999; //nombre total de decimales (par ex pour 8+2 precision est 10)
  int scale = 0; //nombre de chiffres apres la virgule (par ex pour 8+2 scale est 2)
  BigDecimal multiplicativeFactor = new BigDecimal(1); //facteur multiplicatif (par ex pour 8+2 c'est 100)
  
  static Pattern numFormatPattern = Pattern.compile("(\\d+)\\+(\\d+)"); //pour vérifier ce qu'il y a dans la colonne "format"
  
  /** constructeur simple */
  public FszFieldMeta() {}
  
  @Override
  public boolean isFieldMeta() { return true; }
  
  @Override
  public boolean isGroupMeta() { return false; }
  
  /**
   * Remplir les informations de métadonnées à partir d'une ligne au format csv (séparateur ";")
   * N.B. alignment peut être "Gauche" ou "Droite" ou "Left" ou "Right", avec n'importe quelle casse,
   * et filling peut être "Zéro" ou "Zero" ou "Espace" ou "Space", avec n'importe quelle casse.
   * Les 4 combinaisons sont possibles mais par sécurité lorsque alignment est "left" ou "gauche", le caractère
   * de remplissage est toujours l'espace, même lorsque "filling" est "zero".
   * @param line La ligne de description
   */
  public void readFromCsvLine(String line)
  {
    int pos = 0; int slashPos = -1; String val = null;
    StringTokenizer stok = new StringTokenizer(line, ";", true);
    while (stok.hasMoreTokens()) {
      String tok = stok.nextToken();
      if (tok.equals(";")) {
        if (val != null) {
          //ignorer, c'est juste le séparateur normal, qui suit une valeur non vide
          val = null; //remettre à null, permet de détecter les ";" qui se suivent et qui indiquent ainsi une valeur vide
          continue;
        }
        //si on arrive ici, c'est qu'il s'agit d'un séparateur pour un champ vide.
        //tomber dans la suite
      }
      else {
        val = tok; //prendre la valeur du champ
      }      
      switch (pos) {
      case 0: fieldGroupName = val; break;
      case 1: 
      	longName = val;
      	if (fieldGroupName.endsWith(":")) {
      		while (stok.hasMoreTokens()) tok = stok.nextToken(); //eat all other tokens
      	}
        break;
      case 2: 
        stdName = val; 
        if (isEmpty(stdName)) lg.error("stdName empty for '"+longName+"'");
        break;
      case 3: size = Integer.parseInt(val); break;
      case 4: start = numParse(val); break;
      case 5: end = numParse(val); 
              if (start != null && start > 0 && end != null && end > 0 && end - start + 1 != size) {
                throw new RuntimeException("Erreur de taille dans la declaration. (ligne '"+line+"')"); 
              }
              break;
      case 6: mandatory = val; break;
      case 7: typeOrig = val; break;
      case 8: typePref = val; break;
      case 9: slashPos = val == null ? -1 : val.indexOf('/');
        if (slashPos >= 0) {
          alignment = val.substring(0, slashPos).trim();
          filling = val.substring(slashPos+1, val.length()).trim();  
        }
        break;
      case 10: remarks = val; break;
      case 11: 
      	fieldCounter = val; 
      	if (!isEmpty(val) && isLoadDebuggingEnabled()) {
      		lg.debug("field counter '"+val+"'");
      	}
      	break;
      case 12:
        setFormat(val);
        break;
      default:
        throw new RuntimeException("Champ supplementaire nr."+pos+" (valeur '"+val+"') non gere. Ligne : "+line);
      }
      pos++;
    }//while
  }
  
  /**
   * Analyser la chaîne de caractère pour voir s'il s'agit bien d'un nombre.
   * L'étoile est acceptée et est interprétée comme null. (hk 230116 source de cette info ?)
   * Les chaines vides sont interprétées comme null.
   * @param nstr La chaîne à transformer en nombre
   * @return L'entier qui représente le nombre ou null
   * @throws NumberFormatException si la chaîne ne représente pas un nombre et n'est ni vide, ni égale à "*"
   */
  public Integer numParse(String nstr)
  {
    if (nstr == null || nstr.length() == 0) return 0;
    if (nstr.equals("*")) return null;
    return Integer.parseInt(nstr);
  }
  
  /**
   * Faire un dump de cette définition dans le StringBuffer sb
   * @param sb le {@link StringBuffer} à alimenter.
   */
  public void dump(StringBuffer sb) { dump(sb, 0); }
  
  /**
   * Faire un dump de cette définition dans le StringBuffer sb, en décalant via le niveau.
   * @param sb le {@link StringBuffer} à alimenter.
   * @param level le niveau de décalage. Envoie deux espaces par niveau.
   */
  public void dump(StringBuffer sb, int level) {
    for (int i = 0; i < level; i++) { sb.append("  "); }
    sb.append("<FIELD");
    sb.append(" fgn="); sb.append('"'); nna(sb, fieldGroupName); sb.append('"');
    sb.append(" longname="); sb.append('"'); nna(sb, longName); sb.append('"');
    sb.append(" stdname="); sb.append('"'); nna(sb, stdName); sb.append('"');
    sb.append(" size="); sb.append('"'); nna(sb, size); sb.append('"');
    sb.append(" start="); sb.append('"'); nna(sb, start); sb.append('"');
    sb.append(" end="); sb.append('"'); nna(sb, end); sb.append('"');
    sb.append(" mand="); sb.append('"'); nna(sb, mandatory); sb.append('"');
    sb.append(" typorig="); sb.append('"'); nna(sb, typeOrig); sb.append('"');
    sb.append(" typpref="); sb.append('"'); nna(sb, typePref); sb.append('"');
    sb.append(" alng="); sb.append('"'); nna(sb, alignment); sb.append('"');
    sb.append(" fill="); sb.append('"'); nna(sb, filling); sb.append('"');
    sb.append(" rem="); sb.append('"'); nna(sb, remarks); sb.append('"');
    sb.append(" fc="); sb.append('"'); nna(sb, fieldCounter); sb.append('"');
    sb.append(" fmt="); sb.append('"'); nna(sb, format); sb.append('"');
    sb.append("/>\n");
  }
  
  /**
   * Ajouter la String s au StringBuffer sb mais seulement si elle n'est pas null.
   * @param sb buffer dans lequel on ajoute s
   * @param s la String a ajouter (peut être null)
   */
  void nna(StringBuffer sb, String s) {
    if (s == null) return;
    else sb.append(s);
  }
  
  /**
   * Ajouter l'entier i au StringBuffer sb mais seulement si il n'est pas null.
   * @param sb buffer dans lequel on ajoute s
   * @param i l'entier à ajouter (peut être null)
   */
  void nna(StringBuffer sb, Integer i) {
    if (i == null) return;
    else sb.append(i);
  }
  
  /**
   * Est-ce que ce champ est de type "Date" (européenne)
   * Une date europeenne est un definie comme un format numerique avec un type préféré
   * qui est à "D" (habituellement dans les commentaires on retrouve 
   * JJMMAAAA)
   * @return true si typePref est égal à "D"
   */
  public boolean representsEuropeanDate() {
    return "D".equals(typePref);
  }
  
  /**
   * Est-ce que ce champ est de type numérique
   * @return true si typePref est égal à "N"
   */
  public boolean representsNumber() {
    return "N".equals(typePref);
  }
  
  /**
   * Retourner le nom du groupement de champs. Correspond à la 1ère colonne de la ligne de définition.
   * @return Le nom (par exemple "RSS" pour un RSS)
   */
  public String getFieldGroupName() {
    return fieldGroupName;
  }
  
  /**
   * Définir le nom du groupe
   * @param fieldGroupName Le nom
   */
  public void setFieldGroupName(String fieldGroupName) {
    this.fieldGroupName = fieldGroupName;
  }
  
  /** 
   * Retourner le nom long du champ. 
   * Correspond à la deuxième colonne de la ligne de définition.  
   * @return Le nom long (par exemple pour un RSS "Diagnostic principal (DP)", "N° Administratif local de séjour")
   */
  public String getLongName() {
    return longName;
  }
  
  /**
   * Définir le nom long du champ
   * @param longName Le nom long
   */
  public void setLongName(String longName) {
    this.longName = longName;
  }
  
  /**
   * Retourner le nom standardisé du champ. C'est le nom que l'on pourra utiliser pour
   * créer un nom de colonne dans une base de données, une feuille Excel, 
   * ou bien un encore un nom de balise XML.
   * @return Le nom standard (par exemple pour un RSS "DP", "NADL", etc.)
   */
  public String getStdName() {
    return stdName;
  }
  
  /**
   * Définir le nom standard
   * @param stdName Le nom standard
   */
  public void setStdName(String stdName) {
    this.stdName = stdName;
  }
  
  /**
   * Retourner la taille du champ en nombre de caractères.
   * Correspond à la 4ème colonne de la ligne de définition
   * @return La taille du champ
   */
  public Integer getSize() {
    return size;
  }
  
  /**
   * Définir la taille du champ
   * @param size La taille
   */
  public void setSize(Integer size) {
    this.size = size;
  }
  
  /**
   * Retourner le numéro de caractère qui contient le 1er caractère du champ.
   * Les numéros de caractères commencent à 1.
   * Correspond à la 5ème colonne de la ligne de définition. 
   * @return La position du premier caractère (commence à 1)
   */
  public Integer getStart() {
    return start;
  }
  
  /**
   * Définir la position de début du champ (tel que donné dans les documents de l'ATIH)
   * @param start Le numéro de position
   */
  public void setStart(Integer start) {
    this.start = start;
  }
  
  /**
   * Retourner le numéro de caractère qui contient le dernier caractère du champ.
   * Les numéros de caractères commencent à 1.
   * Correspond à la 6ème colonne de la ligne de définition. 
   * @return La position du dernier caractère (commence à 1)
   */
  public Integer getEnd() {
    return end;
  }
  
  /**
   * Définir la position de fin (telle que donnée dans les documents de l'ATIH)
   * @param end Le numéro de position de fin
   */
  public void setEnd(Integer end) {
    this.end = end;
  }
  
  /**
   * Retourne "O" si le champ est obligatoire, "F" s'il n'est pas obligatoire.
   * Correspond à la 7ème colonne de la ligne de définition.
   * @return La valeur de la 7ème colonne (normalement O (Obligatoire) ou F (Facultatif))
   */
  public String getMandatory() {
    return mandatory;
  }
  
  /**
   * Définir si ce champ est obligatoire ou pas (cf. {@link #getMandatory()}
   * @param mandatory La valeur de la 7ème colonne (normalement O (Obligatoire) ou F (Facultatif))
   */
  public void setMandatory(String mandatory) {
    this.mandatory = mandatory;
  }
  
  /**
   * Retourne le type qui est précisé dans la définition originale de l'ATIH.
   * C'est soit "A" pour alphanumérique, soit "N" pour numérique 
   * A noter que souvent on ne trouve pas cette information explicitement dans les documents
   * fournis par l'ATIH.
   * Correspond à la 8ème colonne de la ligne de définition.
   * @return Le type original ATIH
   */
  public String getOriginalType() {
    return typeOrig;
  }
  
  /**
   * Définir le type de données original (cf. {@link #getOriginalType()}
   * @param type Le type
   */
  public void setOriginalType(String type) {
    this.typeOrig = type;
  }
  
  /**
   * Retourne le type que l'on préfère avoir.
   * On peut ici mettre des éléments différents de ce que l'on trouve dans le type
   * original, mais il faut alors être prêt à gérer les erreurs et à bien 
   * contrôler ce qui sera envoyé dans ce champ si on génère des enregistrements.
   * C'est soit "A" pour alphanumérique, soit "N" pour numérique, soit "D" pour date.
   * Si c'est "D", le champ doit avoir une taille de 6 caractères et être soit null,
   * soit ne contenir que des zéros, soit contenir une date au format JJMMAAAA.
   * @return le type prefere
   */
  public String getPreferredType() {
    return typePref;
  }
  
  /**
   * Définir le type préféré (cf. {@link #getPreferredType()}
   * @param type La lettre du type
   */
  public void setPreferredType(String type) {
    this.typePref = type;
  }
  
  /**
   * Retourner l'alignement. On doit utiliser strictement les noms qui se trouvent dans les docs ATIH,
   * soit
   * <UL>
   * <LI>Gauche
   * <LI>Droite
   * <LI>NA
   * </UL>
   * @return Le texte qui correspond à l'alignement
   */
  public String getAlignment() {
    return alignment;
  }
  
  /**
   * Définir l'alignement (cf. {@link #getAlignment()}
   * @param alignment L'alignement
   */
  public void setAlignment(String alignment) {
    this.alignment = alignment;
  }
  /**
   * Retourner le remplissage. On doit utiliser strictement les noms qui se trouvent dans les docs ATIH,
   * soit 
   * <UL>
   * <LI>NA
   * <LI>Espace
   * <LI>Zéro
   * </UL>
   * 
   * @return Le texte qui correspond à l'alignement
   */
  public String getFilling() {
    return filling;
  }
  
  /**
   * Définir le remplissage (cf. {@link #getFilling()}
   * @param filling Le caractère de remplissage
   */
  public void setFilling(String filling) {
    this.filling = filling;
  }
  
  /**
   * Retourner les remarques. Dans les remarques, il ne doit pas y avoir de retour à la ligne,
   * c'est à dire que le texte de la remarque doit tenir sur une seule ligne.
   * Si le texte des remarques doit être affiché sur plusieurs lignes, on doit mettre '\n' là
   * où il faut faire passer le texte à la ligne.
   * @return les remarques
   */
  public String getRemarks() {
    return remarks;
  }
  
  /**
   * Retourner les remarques "formatées", c'est à dire que tous les \n ont été remplacés par la chaîne qui représente le
   * passage à la ligne. 
   * @param newline La chaîne qui représente la fin de ligne.
   * @return Les remarques, formatées.
   */
  public String getRemarksFormatted(String newline) {
    if (remarks == null) return "";
    return remarks.replace("\\n", newline);
  }
  
  /**
   * Définir les remarques formatées (cf. {@link #getRemarks()}
   * @param remarks Les remarques, on peut utiliser \n pour représenter une fin de ligne
   */
  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  /**
   * Si ce champ représente un compteur, retourner le nom du compteur. 
   * Correspond à la 12ème colonne de la ligne de définition.
   * Par exemple si on a cette ligne de définition :
   * <pre>
   * RUM    Nombre de données à visée documentaire (nDAD) dans ce RUM   NDAD    2   136 137 O   N   N   Droite/Zéro     DAD
   * </pre>
   * Le champ s'appellera NDAD, et son contenu représentera le compteur de DAD
   * L'appel à {@link #getFieldCounter()} renverra donc "DAD"
   * @return Le nom du compteur qui correspond à ce champ
   */
  public String getFieldCounter() {
    return fieldCounter;
  }

  /**
   * Définir le nom du compteur (cf. {@link #getFieldCounter()}
   * @param fieldCounter Le nom du compteur
   */
  public void setFieldCounter(String fieldCounter) {
    this.fieldCounter = fieldCounter;
  }

  /**
   * Méthode triviale pour tester si une chaîne est vide, c'est à dire qu'elle est soit null,
   * ou soit de longeur 0, ou soit qu'elle ne contient que des espaces
   * @see String#trim() 
   * 
   * @param str La chaîne à tester
   * @return true si chaîne est vide
   */
  public static final boolean isEmpty(String str) {
    return str == null || str.trim().equals("");
  }
  
  /**
   * Construire un nouveau noeud (ici de type FszField) qui aura pour métadonnées cet objet.
   */
  @Override
  public FszNode makeNewNode() {
    FszField fld = new FszField(this);
    return fld;
  }

  /**
   * Retourner l'objet métadonnées parent (qui est normalement un groupe)
   * @return le parent
   */
  @Override
  public FszMeta getParent() { return parent; }

  @Override
  public void setParent(FszMeta parent) { this.parent = parent; }
  
  /**
   * Méthode pour savoir si l'alignement est gauche. Convertit en minuscules avant de comparer
   * avec "gauche". Marche aussi avec "left" (mais déconseillé)
   * @return true si l'alignement doit se faire à gauche
   */
  public boolean isLeftAligned() { 
    return "gauche".equalsIgnoreCase(alignment) || "left".equalsIgnoreCase(alignment); 
  }
  
  /**
   * Méthode pour savoir si l'alignement est à droite. Convertit en minuscules avant de comparer
   * avec "droite". Marche aussi avec "right" (mais déconseillé)
   * @return true si l'alignement doit se faire à droite
   */
  public boolean isRightAligned() { 
    return "droite".equalsIgnoreCase(alignment) || "right".equalsIgnoreCase(alignment); 
  } 
  
  /**
   * Méthode pour savoir si le remplissage doit se faire avec des zéros.
   * Convertit en minuscules avant de comparer avec "zéro". Marche aussi avec "zero" (sans accent).
   * @return true si le remplissage doit se faire avec des zéros
   */
  public boolean isZeroFilled() {
  	if ("zero".equalsIgnoreCase(filling)) return true;
  	if ("z\u00e9ro".equalsIgnoreCase(filling)) return true;
  	return false;
  }
  
  /**
   * Méthode pour savoir si le remplissage doit se faire avec des espaces.
   * Convertit en minuscules avant de comparer avec "espace". Marche aussi avec "space" (mais déconseillé)
   * @return true si le remplissage doit se faire avec des espaces.
   */
  public boolean isSpaceFilled() {
  	return "espace".equalsIgnoreCase(filling) || "space".equalsIgnoreCase(filling);
  }
  
  /**
   * Retourner le format à utiliser. Est soit vide, soit de la forme n+m, par exemple 8+2.
   * Si non vide, 
   * @return le format à utiliser
   */
  public String getFormat() {
    return format;
  }

  /**
   * Déterminer le format à utiliser. Le format doit être soit vide, soit de la forme n+m (par ex. 8+2)
   * Le format sert alors à remplir precision, scale, multiplicative factor.
   * Si le format n'est pas correct, il est ignoré et "" est utilisé à la place.
   * @param format_p Le format à utiliser
   */
  public void setFormat(String format_p) {
    //si le format est bien du format nn+mm (ex : 10+2) l'utiliser.
    //sinon laisser le format à "" (chaîne vide)
    precision = (size == null ? 0 : size); //par defaut precision = la taille du champ
    scale = 0; //par défaut zéro décimales derrière la virgule
    Matcher nfm = numFormatPattern.matcher(format_p == null ? "" : format_p.trim());
    if (nfm.matches()) {
      //extraire les valeurs du format
      int d1 = Integer.parseInt(nfm.group(1));
      scale = Integer.parseInt(nfm.group(2));
      precision = d1 + scale;
      this.format = format_p;
      multiplicativeFactor = new BigDecimal(10).pow(scale);
    }
    else if (format_p == null || format_p.trim().equals("")) this.format = ""; 
    else {
      lg.error("Format nombre illegal '"+format_p+"'");
      this.format = "";
    }
  }

  /**
   * Le nombre de décimales total. Par ex. pour 8+2 la précision est 10.
   * @return la précision
   */
  public int getPrecision() {
    return precision;
  }

  /**
   * Définir la précision (cf. {@link #getPrecision()}
   * @param precision La précision
   */
  public void setPrecision(int precision) {
    this.precision = precision;
  }

  /**
   * L'échelle, qui est le nombre de décimales derrière la virgule. Par ex. pour 8+2 l'échelle est 2.
   * @return l'échelle
   */
  public int getScale() {
    return scale;
  }

  /**
   * Définir l'échelle (cf. {@link #getScale()}
   * @param scale L'échelle
   */
  public void setScale(int scale) {
    this.scale = scale;
  }

  /**
   * Retourner le facteur multiplicatif, qui est la puissance de 10 par laquelle il faut multiplier un
   * nombre à virgule pour avoir un nombre entier dans ce champ. Par exemple pour 8+2 le facteur multiplicatif
   * est 10**2 soit 100.
   * @return le facteur multiplicatif
   */
  public BigDecimal getMultiplicativeFactor() {
    return multiplicativeFactor;
  }

  /**
   * Définir le facteur multiplicatif (cf. {@link #getMultiplicativeFactor()}
   * @param multiplicativeFactor Le facteur multiplicatif
   */
  public void setMultiplicativeFactor(BigDecimal multiplicativeFactor) {
    this.multiplicativeFactor = multiplicativeFactor;
  }

}
