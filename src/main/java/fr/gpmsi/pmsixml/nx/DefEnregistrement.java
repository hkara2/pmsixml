package fr.gpmsi.pmsixml.nx;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Définition d'un enregistrement.
 */
public class DefEnregistrement {
	static Logger lg = LogManager.getLogger();
			
	String type;
	
	private int rubriqueMin;
	private int rubriqueMax;
	
	private int sequenceMin;
	private int sequenceMax;
	
	String complete; //complete l'enregistrement, les champs seront fusionnes avec l'enregistrement mentionne
	String etend; //etend l'enregistrement, (? voir si "complete" ne suffit pas ?)
	String fusionneSur; //(?a enlever ?)si 'SEQ' est superieur de 1 a SEQ precedent, sera fusionne avec l'enregistrement precedent pour n'en faire qu'un seul.
	String lien; //relié (enfant) à l'enregistrement precedent decrit
		
	String nom;
	String nomLong;
	
	String description;
	
	HashMap<String, List<String>> collections;
	//HashMap<String, LinkedList<DefChamp>> enfantsDefChamp;
	//listeEnfants, dans l'ordre. Peuvent être DefChamp ou DefSousEnregistrement.
	LinkedList<Object> enfants = new LinkedList<Object>();
	
	/**
	 * Produire un enregistrement à partir de la ligne au format NX.
	 * @param line Ligne au format NX
	 * @return Un enregistrement
	 * @throws NxParseException _
	 */
	public Enregistrement makeEnregistrement(String line)
			throws NxParseException 
	{
		if (line == null || line.length() != 128) {
			throw new NxParseException("Erreur enregistrement pas Nx");
		}
		Enregistrement e = new Enregistrement();
		e.definition = this;
		Position p = new Position(0); //position d'analyse
		String enr_type = p.substring(line, 3);
		if (!enr_type.equals(type)) {
			throw new NxParseException("Types differents : attendu:"+type+",lu:"+enr_type);
		}
		p.add(3);
		//on teste rubrique et sequence car certains enregistrements NX n'ont pas de rubrique/sequence !
		if (rubriqueMax > 0) {
			e.rub = p.substring(line, 2);
			p.add(2);		
		}
		if (sequenceMax > 0) {
			e.seq = p.substring(line, 2);
			p.add(2);
		}
		int nbEnfants = enfants.size();
		for (int i = 0; i < nbEnfants; i++) {
			Object defEnfant = enfants.get(i);
			if (defEnfant instanceof DefChamp) {
				if (defEnfant instanceof DefRepetition) {
					DefRepetition defRep = (DefRepetition)defEnfant;
					List<Champ> coll = e.getOrMakeCollection(defRep.nom, defRep.numeroter);
					for (int j = 0; j < defRep.getMax(); j++) {
						Champ rep = defRep.champARepeter.makeChamp(line, p);
						//lg.debug("rep "+rep.definition+" : " + rep);
						coll.add(rep);
					}
				}
				else {
					DefChamp enfantDefChamp = (DefChamp) defEnfant;
					e.champParNom.put(enfantDefChamp.nom, enfantDefChamp.makeChamp(line, p));
				}
			}
			else if (defEnfant instanceof DefSousEnregistrement) {
				DefSousEnregistrement enfantDefSousEnregistrement = (DefSousEnregistrement) defEnfant;
				int nbSe = enfantDefSousEnregistrement.max;
				//lg.debug("def se : "+enfantDefSousEnregistrement.nom+", nbSe:"+nbSe);
				ArrayList<SousEnregistrement> seList = new ArrayList<SousEnregistrement>();
				for (int j = 0; j < nbSe; j++) {
					SousEnregistrement se = new SousEnregistrement();
					se.def = enfantDefSousEnregistrement;
					for (DefChamp defChampEnfant : enfantDefSousEnregistrement.champs) {
						se.addChamp(defChampEnfant, line, p);
					}					
					//lg.debug("Ajout " + se);
					seList.add(se);
				}
				//lg.debug("Ajout de la liste des sous-enregistrements sous "+enfantDefSousEnregistrement.nom);
				e.sousEnregistrementsParNom.put(enfantDefSousEnregistrement.nom, seList);
			}
			else {
				throw new Error("type d'enfant inconnu " + defEnfant.getClass());
			}
		}
		return e;
	}
	
	/**
	 * Ajouter un champ enfant
	 * @param enfant Le champ à ajouter
	 */
	public void addEnfant(DefChamp enfant) {
		enfants.add(enfant);
	}

	/**
	 * Ajouter une définition de sous-enregistrement enfant 
	 * @param enfant La définition de sous-enregistrement à ajouter
	 */
	public void addEnfant(DefSousEnregistrement enfant) {
		enfants.add(enfant);
	}

	/**
	 * Définir le numéro de rubrique (cette valeur se retrouve dans le minimum et le maximum)
	 * @param rubrique Le numéro de rubrique
	 */
	public void setRubrique(int rubrique) {
		this.rubriqueMin = rubrique;
		this.rubriqueMax = rubrique;
	}

	/**
	 * Définir le numéro de rubrique
	 * @param rubrique Une String qui contient le numéro de rubrique
	 */
	public void setRubrique(String rubrique) {
		setRubrique(Integer.valueOf(rubrique));
	}
	
	
	/**
	 * retourner le numéro de rubrique minimum
	 * @return Le numéro de rubrique
	 */
	public int getRubriqueMin() {
		return rubriqueMin;
	}

	/**
	 * Définir le numéro de rubrique minimum
	 * @param rubriqueMin _
	 */
	public void setRubriqueMin(int rubriqueMin) {
		this.rubriqueMin = rubriqueMin;
	}

	/**
	 * Définir le numéro de rubrique minimum
	 * @param rubriqueMin Une String qui contient le numéro
	 */
	public void setRubriqueMin(String rubriqueMin) {
		this.rubriqueMin = Integer.valueOf(rubriqueMin);
	}

    /**
     * Retourner le numéro de rubrique maximum
     * @return Le numéro
     */
	public int getRubriqueMax() {
		return rubriqueMax;
	}

	/**
	 * Définir le numéro de rubrique maximum
	 * @param rubriqueMax Le numéro
	 */
	public void setRubriqueMax(int rubriqueMax) {
		this.rubriqueMax = rubriqueMax;
	}
	
	/**
	 * Définir le numéro de rubrique maximum
	 * @param rubriqueMax Une String qui contient le numéro
	 */
	public void setRubriqueMax(String rubriqueMax) {
		this.rubriqueMax = Integer.valueOf(rubriqueMax);
	}

	/**
	 * Définir le numéro de séquence (définit en même temps le minimum et le maximum)
	 * @param sequence Le numéro
	 */
	public void setSequence(int sequence) {
		this.sequenceMin = sequence;
		this.sequenceMax = sequence;
	}

	/**
	 * Définir le numéro de séquence
	 * @param sequence Une String qui contient le numéro
	 */
	public void setSequence(String sequence) {
		setSequence(Integer.valueOf(sequence));
	}

	/**
	 * Retourner le numéro de séquence minimum
	 * @return Le numéro
	 */
	public int getSequenceMin() {
		return sequenceMin;
	}

	/**
	 * Définir le numéro de séquence minimum
	 * @param sequenceMin Une String qui contient le numéro
	 */
	public void setSequenceMin(int sequenceMin) {
		this.sequenceMin = sequenceMin;
	}

	/**
	 * Définir le numéro de séquence minimum
	 * @param sequenceMin Le numéro
	 */
	public void setSequenceMin(String sequenceMin) {
		this.sequenceMin = Integer.valueOf(sequenceMin);
	}

	/**
	 * Retourner le numéro de séquence maximum
	 * @return Le numéro de séquence
	 */
	public int getSequenceMax() {
		return sequenceMax;
	}

	/**
	 * Définir le numéro de séquence maximum
	 * @param sequenceMax Le numéro
	 */
	public void setSequenceMax(int sequenceMax) {
		this.sequenceMax = sequenceMax;
	}
	
	/**
	 * Définir le numéro de séquence maximum
	 * @param sequenceMax Une String qui contient le numéro
	 */
	public void setSequenceMax(String sequenceMax) {
		this.sequenceMax = Integer.valueOf(sequenceMax);
	}
	
	/**
	 * Le numéro de séquence est-il valide ? (il est valide s'il est compris entre min et max)
	 * @param sequence Le numéro de séquence à tester
	 * @return True si le numéro est valide
	 */
	public boolean isSequenceValid(int sequence) {
		return sequenceMin <= sequence && sequence <= sequenceMax;
	}
	
	/**
	 * Le numéro de séquence est-il valide ?
	 * @see #isSequenceValid(int)
	 * @param sequence Une String contenant le numéro
	 * @return True si le numéro est valide
	 */
	public boolean isSequenceValid(String sequence) {
		return isSequenceValid(Integer.valueOf(sequence));
	}

	/**
	 * Le numéro de rubrique est-il valide ? (le numéro est valide s'il est compris entre le minimum et le maximum
	 * @param rubrique Le numéro
	 * @return True si le numéro est valide
	 */
	public boolean isRubriqueValid(int rubrique) {
		return rubriqueMin <= rubrique && rubrique <= rubriqueMax;
	}
	
	/**
	 * Le numéro de rubrique est-il valide ?
	 * @param rubrique Une String qui contient un numéro
	 * @return True si le numéro est valide
	 */
	public boolean isRubriqueValid(String rubrique) {
		return isRubriqueValid(Integer.valueOf(rubrique));
	}

	/**
	 * Convertir cet enregistrement en une String de forme "Enregistrement <nom>  type <type>  rub <rubriqueMax>"
	 */
	@Override
	public String toString() {
		return "Enregistrement '"+nom+"' type '"+type+"' rub '"+rubriqueMax+"'";
	}
	
	void emettreMarque(int size, Writer w) throws IOException {
		w.write('^'); size--; while (size > 0) {
			if (size == 1) w.write('.'); else w.write('_');
			size--;
		}
	}
	
	/**
	 * Emettre des marques qui indiquent le début de chaque champ (permet de vérifier les bonnes définitions
	 * des positions)
	 * @param w Le Writer vers lequel envoyer les marques
	 * @throws IOException _
	 */
	public void emettreMarques(Writer w)
			throws IOException 
	{
		w.write("###"); //on emet toujours 3 hash pour marquer le début
		if (rubriqueMax > 0) emettreMarque(2, w);
		if (sequenceMax > 0) emettreMarque(2, w);
		int nbEnfants = enfants.size();
		for (int i = 0; i < nbEnfants; i++) {
			Object defEnfant = enfants.get(i);
			if (defEnfant instanceof DefChamp) {
				if (defEnfant instanceof DefRepetition) {
					DefRepetition defRep = (DefRepetition)defEnfant;
					for (int j = 0; j < defRep.getMax(); j++) {
						emettreMarque(defRep.champARepeter.lng, w);
					}
				}
				else {
					DefChamp enfantDefChamp = (DefChamp) defEnfant;
					emettreMarque(enfantDefChamp.lng, w);
				}
			}
			else if (defEnfant instanceof DefSousEnregistrement) {
				DefSousEnregistrement enfantDefSousEnregistrement = (DefSousEnregistrement) defEnfant;
				int nbSe = enfantDefSousEnregistrement.max;
				for (int j = 0; j < nbSe; j++) {
					for (DefChamp defChampEnfant : enfantDefSousEnregistrement.champs) {
						emettreMarque(defChampEnfant.lng, w);
					}					
				}
			}
			else {
				throw new Error("type d'enfant inconnu " + defEnfant.getClass());
			}
		}//for	
	}//emettreMarques
	
}
