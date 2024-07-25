package fr.gpmsi.pmsixml.nx;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
	public void addEnfant(DefChamp enfant) {
		enfants.add(enfant);
	}

	public void addEnfant(DefSousEnregistrement enfant) {
		enfants.add(enfant);
	}

	public void setRubrique(int rubrique) {
		this.rubriqueMin = rubrique;
		this.rubriqueMax = rubrique;
	}

	public void setRubrique(String rubrique) {
		setRubrique(Integer.valueOf(rubrique));
	}
	
	public int getRubriqueMin() {
		return rubriqueMin;
	}


	public void setRubriqueMin(int rubriqueMin) {
		this.rubriqueMin = rubriqueMin;
	}

	public void setRubriqueMin(String rubriqueMin) {
		this.rubriqueMin = Integer.valueOf(rubriqueMin);
	}


	public int getRubriqueMax() {
		return rubriqueMax;
	}


	public void setRubriqueMax(int rubriqueMax) {
		this.rubriqueMax = rubriqueMax;
	}
	
	public void setRubriqueMax(String rubriqueMax) {
		this.rubriqueMax = Integer.valueOf(rubriqueMax);
	}

	public void setSequence(int sequence) {
		this.sequenceMin = sequence;
		this.sequenceMax = sequence;
	}

	public void setSequence(String sequence) {
		setSequence(Integer.valueOf(sequence));
	}

	public int getSequenceMin() {
		return sequenceMin;
	}

	public void setSequenceMin(int sequenceMin) {
		this.sequenceMin = sequenceMin;
	}

	public void setSequenceMin(String sequenceMin) {
		this.sequenceMin = Integer.valueOf(sequenceMin);
	}

	public int getSequenceMax() {
		return sequenceMax;
	}

	public void setSequenceMax(int sequenceMax) {
		this.sequenceMax = sequenceMax;
	}
	
	public void setSequenceMax(String sequenceMax) {
		this.sequenceMax = Integer.valueOf(sequenceMax);
	}
	
	public boolean isSequenceValid(int sequence) {
		return sequenceMin <= sequence && sequence <= sequenceMax;
	}
	
	public boolean isSequenceValid(String sequence) {
		return isSequenceValid(Integer.valueOf(sequence));
	}

	public boolean isRubriqueValid(int rubrique) {
		return rubriqueMin <= rubrique && rubrique <= rubriqueMax;
	}
	
	public boolean isRubriqueValid(String rubrique) {
		return isRubriqueValid(Integer.valueOf(rubrique));
	}

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
	
	public void emettreMarques(Writer w) throws IOException {
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
