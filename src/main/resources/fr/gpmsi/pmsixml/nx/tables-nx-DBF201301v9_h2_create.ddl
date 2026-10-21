-- genere a partir de C:\hkgh\pmsixml\src\main\resources\fr\gpmsi\pmsixml\nx\tables-nx-DBF201301v9.xml en utilisant le script creation_ddl_nx_ccam.groovy

CREATE TABLE IF NOT EXISTS R_AAP_PMSI (
  AAP_COD VARCHAR(16),
  ICR NUMERIC(4),
  CLASSANT VARCHAR(1)
);

COMMENT ON TABLE R_AAP_PMSI IS 'Table des données du PMSI pour l''acte/activité/phase';
  COMMENT ON COLUMN R_AAP_PMSI.AAP_COD IS 'CCAM n° 1+2+4 Code phase de l''activité de l''acte (code interne activité + code interne phase)';
  COMMENT ON COLUMN R_AAP_PMSI.ICR IS 'CCAM n° 48 Indice de coût relatif';
  COMMENT ON COLUMN R_AAP_PMSI.CLASSANT IS 'CCAM n° 51 Caractère Classant';

CREATE TABLE IF NOT EXISTS R_ACTE (
  ID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  COD_ACTE VARCHAR(13),
  DT_MODIF DATE,
  MENU_COD NUMERIC(6),
  FRAIDP_COD VARCHAR(1),
  TYPE_COD NUMERIC(1),
  COD_STRUCT VARCHAR(13),
  NOM_COURT VARCHAR(70),
  NOM_LONG VARCHAR(4000),
  SEXE NUMERIC(1),
  DT_CREATION DATE,
  DT_FIN DATE,
  ENTENTE VARCHAR(1),
  DT_EFFET DATE,
  DT_ARRETE DATE,
  DT_JO DATE,
  MFIC_PLACE NUMERIC(12, 6),
  PRECEDENT VARCHAR(13),
  SUIVANT VARCHAR(13),
  REMBOU_COD NUMERIC(1)
);

COMMENT ON TABLE R_ACTE IS 'Acte ou groupement d''actes';
  COMMENT ON COLUMN R_ACTE.COD_ACTE IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_ACTE.DT_MODIF IS 'date de mise à jour de l''acte';
  COMMENT ON COLUMN R_ACTE.MENU_COD IS 'code du niveau de l''arborescence';
  COMMENT ON COLUMN R_ACTE.FRAIDP_COD IS 'CCAM n° 41 Code frais déplacement';
  COMMENT ON COLUMN R_ACTE.TYPE_COD IS 'CCAM n° 9 Code type d''acte';
  COMMENT ON COLUMN R_ACTE.COD_STRUCT IS 'CCAM n° 5 Code Structuré de l''Acte';
  COMMENT ON COLUMN R_ACTE.NOM_COURT IS 'CCAM n° 7 Libellé court de l''acte';
  COMMENT ON COLUMN R_ACTE.NOM_LONG IS 'CCAM n° 8 Libellé exhaustif de l''acte';
  COMMENT ON COLUMN R_ACTE.SEXE IS 'CCAM n° 15 Sexe Compatible 0 : Sans Objet 1 : Homme 2 : Femme';
  COMMENT ON COLUMN R_ACTE.DT_CREATION IS 'CCAM n° 18 Date de création de l''acte';
  COMMENT ON COLUMN R_ACTE.DT_FIN IS 'CCAM n° 19 Date de suppression du code acte';
  COMMENT ON COLUMN R_ACTE.ENTENTE IS 'CCAM n° 24 Entente préalable';
  COMMENT ON COLUMN R_ACTE.DT_EFFET IS 'CCAM n°26 Date d''Effet des informations historisées';
  COMMENT ON COLUMN R_ACTE.DT_ARRETE IS 'CCAM n° 27 Date de l''arrêté ministériel "tarification"';
  COMMENT ON COLUMN R_ACTE.DT_JO IS 'CCAM n° 28 Date de publication au JO "tarification"';
  COMMENT ON COLUMN R_ACTE.MFIC_PLACE IS 'CCAM n° 17 Position de l''acte dans le dernier niveau de l''Arborescence';
  COMMENT ON COLUMN R_ACTE.PRECEDENT IS 'CCAM n° 49 Code de l''Acte précédent si celui-ci a été recodifié';
  COMMENT ON COLUMN R_ACTE.SUIVANT IS 'CCAM n° 50 Code de l''Acte suivant s''il y a eu recodification';
  COMMENT ON COLUMN R_ACTE.REMBOU_COD IS 'Code mode de remboursement';

CREATE TABLE IF NOT EXISTS R_ACTE_CMUC (
  CLE_FORFAITAIRE NUMERIC(4),
  ACTE_COD VARCHAR(13),
  MODIF_DT DATE
);

COMMENT ON TABLE R_ACTE_CMUC IS 'Forfait CMUC - un acte peut avoir plusieurs couple Forfait/Indice.';
  COMMENT ON COLUMN R_ACTE_CMUC.CLE_FORFAITAIRE IS 'Clé technique entre la table et la Table TB21';
  COMMENT ON COLUMN R_ACTE_CMUC.ACTE_COD IS 'ACTE_COD CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_ACTE_CMUC.MODIF_DT IS 'date de mise à jour de l''acte';

CREATE TABLE IF NOT EXISTS R_ACTE_CLASSE_DMT (
  ACTE_COD VARCHAR(13),
  CLDMT_COD VARCHAR(2)
);

COMMENT ON TABLE R_ACTE_CLASSE_DMT IS 'Classes de DMT de l''acte';
  COMMENT ON COLUMN R_ACTE_CLASSE_DMT.ACTE_COD IS 'code semi structuré de l''acte';
  COMMENT ON COLUMN R_ACTE_CLASSE_DMT.CLDMT_COD IS 'CCAM n° 41.1 Classe de DMT autorisée pour la réalisation de l''acte';

CREATE TABLE IF NOT EXISTS R_ACTE_COND_GEN (
  ACTE_COD VARCHAR(13),
  CONDGE_COD NUMERIC(4)
);

COMMENT ON TABLE R_ACTE_COND_GEN IS 'Conditions générales de l''acte';
  COMMENT ON COLUMN R_ACTE_COND_GEN.ACTE_COD IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_ACTE_COND_GEN.CONDGE_COD IS 'CCAM n° 10 Code condition générale';

CREATE TABLE IF NOT EXISTS R_ACTE_EXO_TM (
  ACTE_COD VARCHAR(13),
  ACDT_MODIF DATE,
  EXOTM_COD NUMERIC(1)
);

COMMENT ON TABLE R_ACTE_EXO_TM IS 'Liste des Code exonération ticket modérateur de l''acte';
  COMMENT ON COLUMN R_ACTE_EXO_TM.ACTE_COD IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_ACTE_EXO_TM.ACDT_MODIF IS 'Date de mise à jour de l''acte';
  COMMENT ON COLUMN R_ACTE_EXO_TM.EXOTM_COD IS 'CCAM n° 25 Code exonération ticket modérateur';

CREATE TABLE IF NOT EXISTS R_ACTE_FORFAIT (
  ACTE_COD VARCHAR(13),
  ACDT_MODIF DATE,
  FORFAI_COD VARCHAR(1)
);

COMMENT ON TABLE R_ACTE_FORFAIT IS 'Forfaits applicables à un code acte';
  COMMENT ON COLUMN R_ACTE_FORFAIT.ACTE_COD IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_ACTE_FORFAIT.ACDT_MODIF IS 'date de mise à jour de l''acte';
  COMMENT ON COLUMN R_ACTE_FORFAIT.FORFAI_COD IS 'CCAM n° 34 Code type de forfait';

CREATE TABLE IF NOT EXISTS R_ACTE_IVITE (
  COD_AA VARCHAR(14),
  DT_MODIF DATE,
  ACTE_COD VARCHAR(13),
  ACDT_MODIF DATE,
  ACTIV_COD VARCHAR(1),
  REGROU_COD VARCHAR(3),
  CATMED_COD VARCHAR(2)
);

COMMENT ON TABLE R_ACTE_IVITE IS 'Commentaire Activité d''un acte';
  COMMENT ON COLUMN R_ACTE_IVITE.COD_AA IS 'CCAM n° 1+2 Code activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ACTE_IVITE.DT_MODIF IS 'date de mise à jour de l''activité de l''acte';
  COMMENT ON COLUMN R_ACTE_IVITE.ACTE_COD IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_ACTE_IVITE.ACDT_MODIF IS 'date de mise à jour de l''acte ';
  COMMENT ON COLUMN R_ACTE_IVITE.ACTIV_COD IS 'CCAM n° 2 Code Activité';
  COMMENT ON COLUMN R_ACTE_IVITE.REGROU_COD IS 'CCAM n° 42 Identifie le groupe de codes';
  COMMENT ON COLUMN R_ACTE_IVITE.CATMED_COD IS 'CCAM n° 12 Code catégorie médicale';

CREATE TABLE IF NOT EXISTS R_ACTE_IVITE_PHASE (
  COD_AAP VARCHAR(16),
  DT_MODIF DATE,
  AA_COD VARCHAR(14),
  AADT_MODIF DATE,
  UOEUVR_COD VARCHAR(3),
  UOEDT_EFFET DATE,
  PAIEM_COD VARCHAR(1),
  PHASE_COD NUMERIC(2),
  NB_SEANCES NUMERIC(2),
  COEFFICIENT NUMERIC(6, 2),
  PU_BASE NUMERIC(6, 2),
  SUPPLEMENT_CAB NUMERIC(6, 2),
  NB_DENTS VARCHAR(2),
  AGE_MIN NUMERIC(3),
  AGE_MAX NUMERIC(3),
  SCORE NUMERIC(6, 2),
  PRATIQUE NUMERIC(6, 2)
);

COMMENT ON TABLE R_ACTE_IVITE_PHASE IS 'Commentaire Phase d''une activité d''un acte';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.COD_AAP IS 'CCAM n° 1+2+4 Code Phase interne de l''activité de l''acte (Code interne Activité + Code Phase)';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.DT_MODIF IS 'date modification de la phase';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.AA_COD IS 'CCAM n° 1+2 Code activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.AADT_MODIF IS 'Date de mise à jour de l''activité de l''acte';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.UOEUVR_COD IS 'CCAM n° 29 Code de l''unité d''oeuvre';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.UOEDT_EFFET IS 'date d''effet de l''unité d''oeuvre';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.PAIEM_COD IS 'CCAM n° 33 Code modalité de paiement des séances';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.PHASE_COD IS 'CCAM n° 4 Code phase de traitement';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.NB_SEANCES IS 'CCAM n° 23 Nombre maximum de séances par période';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.COEFFICIENT IS 'CCAM n° 30 Coefficient de l''acte';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.PU_BASE IS 'CCAM n° 37 Prix Unitaire de base de l''acte. Ce champ n''est plus renseigné dans cette table mais dans la table R_PU_BASE.';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.SUPPLEMENT_CAB IS 'CCAM n° 39 Montant monétaire de charges supplémentaires liées à l''exécution en cabinet';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.NB_DENTS IS 'CCAM n° 43 Nombre de dents à traiter';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.AGE_MIN IS 'CCAM n° 45 Age minimum requis pour l''acte';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.AGE_MAX IS 'CCAM n° 46 Age maximum requis pour l''acte';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.SCORE IS 'CCAM n° 37.1 Score de travail médical';
  COMMENT ON COLUMN R_ACTE_IVITE_PHASE.PRATIQUE IS 'CCAM n° 37.2 Coût de la pratique';

CREATE TABLE IF NOT EXISTS R_ACTE_NAT_ASS (
  ACTE_COD VARCHAR(13),
  NATASS_COD VARCHAR(2)
);

COMMENT ON TABLE R_ACTE_NAT_ASS IS 'Natures d''assurances du code acte';
  COMMENT ON COLUMN R_ACTE_NAT_ASS.ACTE_COD IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_ACTE_NAT_ASS.NATASS_COD IS 'CCAM n° 30.1 Code nature d''assurance';

CREATE TABLE IF NOT EXISTS R_ACTE_PRESCRIPTEUR (
  ACTE_COD VARCHAR(13),
  ACDT_MODIF DATE,
  CATSPE_COD VARCHAR(2)
);

COMMENT ON TABLE R_ACTE_PRESCRIPTEUR IS 'Catégorie de spécialités prescripteurs permises pour l''acte';
  COMMENT ON COLUMN R_ACTE_PRESCRIPTEUR.ACTE_COD IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_ACTE_PRESCRIPTEUR.ACDT_MODIF IS 'date de mise à jour de l''acte ';
  COMMENT ON COLUMN R_ACTE_PRESCRIPTEUR.CATSPE_COD IS 'CCAM n° 31 Code catégorie de spécialité prescripteurs permises';

CREATE TABLE IF NOT EXISTS R_ACTIVITE (
  COD_ACTIV VARCHAR(1),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_ACTIVITE IS 'liste des activités de l''acte  ';
  COMMENT ON COLUMN R_ACTIVITE.COD_ACTIV IS 'Code Activité';
  COMMENT ON COLUMN R_ACTIVITE.LIBELLE IS 'libellé de l''activité';

CREATE TABLE IF NOT EXISTS R_ACTIVITE_AGRE_RADIO (
  AA_CODE VARCHAR(14),
  AGRAD_COD VARCHAR(2)
);

COMMENT ON TABLE R_ACTIVITE_AGRE_RADIO IS 'Commentaire Agréments radio autorisés pour l''activité';
  COMMENT ON COLUMN R_ACTIVITE_AGRE_RADIO.AA_CODE IS 'CCAM n° 1+2 Code activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ACTIVITE_AGRE_RADIO.AGRAD_COD IS 'CCAM n° 40 Code agrément radio';

CREATE TABLE IF NOT EXISTS R_ACTIVITE_EXECUTANT (
  AA_COD VARCHAR(14),
  AADT_MODIF DATE,
  CATSPE_COD VARCHAR(2)
);

COMMENT ON TABLE R_ACTIVITE_EXECUTANT IS 'Catégorie de spécialités exécutants permises pour l''activité';
  COMMENT ON COLUMN R_ACTIVITE_EXECUTANT.AA_COD IS 'CCAM n° 1+2 Code activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ACTIVITE_EXECUTANT.AADT_MODIF IS 'date de mise à jour de l''activité de l''acte';
  COMMENT ON COLUMN R_ACTIVITE_EXECUTANT.CATSPE_COD IS 'CCAM n° 32 Code catégorie de spécialité exécutants permises';

CREATE TABLE IF NOT EXISTS R_ACTIVITE_EXTENSION (
  AA_COD VARCHAR(14),
  EXTENS_COD VARCHAR(1)
);

COMMENT ON TABLE R_ACTIVITE_EXTENSION IS 'Commentaire Extensions documentaires de l''activité';
  COMMENT ON COLUMN R_ACTIVITE_EXTENSION.AA_COD IS 'CCAM n° 1+2 Code activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ACTIVITE_EXTENSION.EXTENS_COD IS 'CCAM n° 3 Code de l''extension documentaire';

CREATE TABLE IF NOT EXISTS R_ACTIVITE_MODIFICATEUR (
  AA_CODE VARCHAR(14),
  AADT_MODIF DATE,
  MODIFI_COD VARCHAR(1)
);

COMMENT ON TABLE R_ACTIVITE_MODIFICATEUR IS 'Modificateurs d''une activité d''un acte';
  COMMENT ON COLUMN R_ACTIVITE_MODIFICATEUR.AA_CODE IS 'CCAM n° 1+2 Code activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ACTIVITE_MODIFICATEUR.AADT_MODIF IS 'date de mise à jour de l''activité de l''acte';
  COMMENT ON COLUMN R_ACTIVITE_MODIFICATEUR.MODIFI_COD IS 'CCAM n° 6 Code modificateur de l''acte';

CREATE TABLE IF NOT EXISTS R_ACTIVITE_PHASE_DENT (
  AAP_COD VARCHAR(16),
  DEN_NUMERO NUMERIC(2)
);

COMMENT ON TABLE R_ACTIVITE_PHASE_DENT IS 'Dents incompatibles avec l''acte/activité/phase';
  COMMENT ON COLUMN R_ACTIVITE_PHASE_DENT.AAP_COD IS 'CCAM n° 1+2+4 Code Phase interne de l''activité de l''acte (Code interne Activité + Code Phase)';
  COMMENT ON COLUMN R_ACTIVITE_PHASE_DENT.DEN_NUMERO IS 'CCAM n° 44 Numéro de la dent incompatible';

CREATE TABLE IF NOT EXISTS R_ACTIVITE_PHASE_DOM (
  AAP_COD VARCHAR(16),
  APDT_MODIF DATE,
  DOM_COD NUMERIC(3),
  MAJORATION NUMERIC(4, 3)
);

COMMENT ON TABLE R_ACTIVITE_PHASE_DOM IS 'Majorations DOM pour un acte/activité/phase';
  COMMENT ON COLUMN R_ACTIVITE_PHASE_DOM.AAP_COD IS 'CCAM n° 1+2+4 Code Phase interne de l''activité de l''acte (Code interne Activité + Code Phase)';
  COMMENT ON COLUMN R_ACTIVITE_PHASE_DOM.APDT_MODIF IS 'Date modification de la phase';
  COMMENT ON COLUMN R_ACTIVITE_PHASE_DOM.DOM_COD IS 'Code DOM';
  COMMENT ON COLUMN R_ACTIVITE_PHASE_DOM.MAJORATION IS 'CCAM n° 47 Majoration DOM';

CREATE TABLE IF NOT EXISTS R_ACTIVITE_RMO (
  AA_COD VARCHAR(14),
  AADT_MODIF DATE,
  INTERN_COD NUMERIC(4)
);

COMMENT ON TABLE R_ACTIVITE_RMO IS 'Table des Références médicales opposables de l''activité.';
  COMMENT ON COLUMN R_ACTIVITE_RMO.AA_COD IS 'CCAM n° 1+2 Code activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ACTIVITE_RMO.AADT_MODIF IS 'date de mise à jour de l''activité de l''acte';
  COMMENT ON COLUMN R_ACTIVITE_RMO.INTERN_COD IS 'code interne de la RMO';

CREATE TABLE IF NOT EXISTS R_AGRE_RADIO (
  COD_AGRAD VARCHAR(2),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_AGRE_RADIO IS 'Liste des Types d''agrément radio utilisés en Base CCAM';
  COMMENT ON COLUMN R_AGRE_RADIO.COD_AGRAD IS 'Code agrément radio';
  COMMENT ON COLUMN R_AGRE_RADIO.LIBELLE IS 'libellé agrément radio';

CREATE TABLE IF NOT EXISTS R_ASSOCIATIONS (
  AA_CODE1 VARCHAR(14),
  DT_MODIF1 DATE,
  AA_CODE2 VARCHAR(14),
  DT_MODIF2 DATE,
  REGLE_COD VARCHAR(1)
);

COMMENT ON TABLE R_ASSOCIATIONS IS 'associations médicales de codes avec règles';
  COMMENT ON COLUMN R_ASSOCIATIONS.AA_CODE1 IS 'CCAM n° 1+2 Code activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ASSOCIATIONS.DT_MODIF1 IS 'date de mise à jour de l''activité de l''acte';
  COMMENT ON COLUMN R_ASSOCIATIONS.AA_CODE2 IS 'CCAM n° 13 Code Activité interne = Code Acte + Code Activité';
  COMMENT ON COLUMN R_ASSOCIATIONS.DT_MODIF2 IS 'date de mise à jour de l''activité de l''acte';
  COMMENT ON COLUMN R_ASSOCIATIONS.REGLE_COD IS 'CCAM n°36 code règle tarifaire';

CREATE TABLE IF NOT EXISTS R_CATEGORIE_MEDIC (
  COD_CATMED VARCHAR(2),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_CATEGORIE_MEDIC IS 'Liste des Catégories médicales';
  COMMENT ON COLUMN R_CATEGORIE_MEDIC.COD_CATMED IS 'Code Catégorie Médicale';
  COMMENT ON COLUMN R_CATEGORIE_MEDIC.LIBELLE IS 'libellé de la catégorie médicale';

CREATE TABLE IF NOT EXISTS R_CATE_SPEC (
  COD_CATSPE VARCHAR(2),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_CATE_SPEC IS 'Liste des Catégories de spécialités ';
  COMMENT ON COLUMN R_CATE_SPEC.COD_CATSPE IS 'code catégorie de spécialité';
  COMMENT ON COLUMN R_CATE_SPEC.LIBELLE IS 'libellé de la catégorie de spécialité';

CREATE TABLE IF NOT EXISTS R_CLASSE_DMT (
  COD_CLDMT VARCHAR(2),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_CLASSE_DMT IS 'Liste des Classes de DMT ';
  COMMENT ON COLUMN R_CLASSE_DMT.COD_CLDMT IS 'Code Classe de DMT autorisée';
  COMMENT ON COLUMN R_CLASSE_DMT.LIBELLE IS 'libellé de la classe de DMT';

CREATE TABLE IF NOT EXISTS R_COMPAT_EXO_TM (
  EXOTM_COD1 NUMERIC(1),
  EXOTM_COD2 NUMERIC(1)
);

COMMENT ON TABLE R_COMPAT_EXO_TM IS 'compatibilités entre codes exonération TM';
  COMMENT ON COLUMN R_COMPAT_EXO_TM.EXOTM_COD1 IS 'code exonération ticket modérateur';
  COMMENT ON COLUMN R_COMPAT_EXO_TM.EXOTM_COD2 IS 'code exonération ticket modérateur';

CREATE TABLE IF NOT EXISTS R_COND_GEN (
  COD_CONDGE NUMERIC(4),
  LIBELLE VARCHAR(4000)
);

COMMENT ON TABLE R_COND_GEN IS 'Liste des Conditions générales';
  COMMENT ON COLUMN R_COND_GEN.COD_CONDGE IS 'Code Condition Générale';
  COMMENT ON COLUMN R_COND_GEN.LIBELLE IS 'libellé condition générale';

CREATE TABLE IF NOT EXISTS R_CONTEXT_BN (
  COD_CONTXTBEN NUMERIC(4),
  LIBELLE VARCHAR(117)
);

COMMENT ON TABLE R_CONTEXT_BN IS 'Code Contexte Bénéficaire (assuré)';
  COMMENT ON COLUMN R_CONTEXT_BN.COD_CONTXTBEN IS 'Code du Contexte Bénéficaire';
  COMMENT ON COLUMN R_CONTEXT_BN.LIBELLE IS 'Libelle du code Contexte du Bénéficaire';

CREATE TABLE IF NOT EXISTS R_CONTEXT_PS (
  COD_CONTXTPS NUMERIC(4),
  LIBELLE VARCHAR(117)
);

COMMENT ON TABLE R_CONTEXT_PS IS 'Contexte Professionnel de Santé';
  COMMENT ON COLUMN R_CONTEXT_PS.COD_CONTXTPS IS 'Code Contexte du Professionnel de Santé';
  COMMENT ON COLUMN R_CONTEXT_PS.LIBELLE IS 'Libelle du contexte du Professionnel de Santé';

CREATE TABLE IF NOT EXISTS R_DENT (
  NUMERO_DEN NUMERIC(2),
  LIBELLE VARCHAR(80)
);

COMMENT ON TABLE R_DENT IS 'Liste des codes des dents';
  COMMENT ON COLUMN R_DENT.NUMERO_DEN IS 'numéro de la dent';
  COMMENT ON COLUMN R_DENT.LIBELLE IS 'Identification de la dent';

CREATE TABLE IF NOT EXISTS R_DOM (
  COD_DOM NUMERIC(3),
  LIBELLE VARCHAR(50)
);

COMMENT ON TABLE R_DOM IS 'Liste des départements d''outre mer';
  COMMENT ON COLUMN R_DOM.COD_DOM IS 'code DOM';
  COMMENT ON COLUMN R_DOM.LIBELLE IS 'libellé DOM';

CREATE TABLE IF NOT EXISTS R_EXO_TM (
  COD_EXOTM NUMERIC(1),
  LIBELLE VARCHAR(80)
);

COMMENT ON TABLE R_EXO_TM IS 'Codification exonération ticket modérateur';
  COMMENT ON COLUMN R_EXO_TM.COD_EXOTM IS 'Code Exonération Ticket Modérateur';
  COMMENT ON COLUMN R_EXO_TM.LIBELLE IS 'libellé exonération ticket modérateur';

CREATE TABLE IF NOT EXISTS R_EXTENSION (
  COD_EXTENS VARCHAR(1),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_EXTENSION IS 'Liste des extensions documentaires';
  COMMENT ON COLUMN R_EXTENSION.COD_EXTENS IS 'Code de l''extension documentaire';
  COMMENT ON COLUMN R_EXTENSION.LIBELLE IS 'description de l''extension documentaire';

CREATE TABLE IF NOT EXISTS R_FORFAIT (
  COD_FORFAI VARCHAR(1),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_FORFAIT IS 'Liste des Forfaits techniques ou consommables applicables à l''acte';
  COMMENT ON COLUMN R_FORFAIT.COD_FORFAI IS 'Code Type de Forfait';
  COMMENT ON COLUMN R_FORFAIT.LIBELLE IS 'libellé du forfait';

CREATE TABLE IF NOT EXISTS R_FRAIS_DEP (
  COD_FRAIDP VARCHAR(1),
  LIBELLE VARCHAR(80)
);

COMMENT ON TABLE R_FRAIS_DEP IS 'Liste des Codifications Frais Déplacement';
  COMMENT ON COLUMN R_FRAIS_DEP.COD_FRAIDP IS 'Code Frais Déplacement';
  COMMENT ON COLUMN R_FRAIS_DEP.LIBELLE IS 'libellé frais déplacement';

CREATE TABLE IF NOT EXISTS R_GLOSSAIRE (
  LIBELLE VARCHAR(50),
  DEFINITION VARCHAR(2000)
);

COMMENT ON TABLE R_GLOSSAIRE IS 'Glossaire des termes indexés dans les champs textes';
  COMMENT ON COLUMN R_GLOSSAIRE.LIBELLE IS 'libellé identifié dans la base';
  COMMENT ON COLUMN R_GLOSSAIRE.DEFINITION IS 'définition du libellé du glossaire';

CREATE TABLE IF NOT EXISTS R_INCOMPATIBILITES (
  ACTE_COD1 VARCHAR(13),
  DT_MODIF1 DATE,
  ACTE_COD2 VARCHAR(13),
  DT_MODIF2 DATE
);

COMMENT ON TABLE R_INCOMPATIBILITES IS 'Incompatibilités médicales de codes';
  COMMENT ON COLUMN R_INCOMPATIBILITES.ACTE_COD1 IS 'code semi structuré de l''acte';
  COMMENT ON COLUMN R_INCOMPATIBILITES.DT_MODIF1 IS 'date de mise à jour de l''acte';
  COMMENT ON COLUMN R_INCOMPATIBILITES.ACTE_COD2 IS 'CCAM n° 14 Code semi structuré de l''acte';
  COMMENT ON COLUMN R_INCOMPATIBILITES.DT_MODIF2 IS 'date de mise à jour de l''acte';

CREATE TABLE IF NOT EXISTS R_MENU (
  COD_MENU NUMERIC(6),
  RANG NUMERIC(6),
  LIBELLE VARCHAR(254),
  COD_PERE NUMERIC(6)
);

COMMENT ON TABLE R_MENU IS 'Arborescence de l acte dans la ccam';
  COMMENT ON COLUMN R_MENU.COD_MENU IS 'CCAM n ° 16 Code du Niveau de l''Arborescence';
  COMMENT ON COLUMN R_MENU.RANG IS 'rang dans la liste des éléments du même niveau';
  COMMENT ON COLUMN R_MENU.LIBELLE IS 'libellé du niveau de l''arborescence';
  COMMENT ON COLUMN R_MENU.COD_PERE IS 'code du niveau de l''arborescence';

CREATE TABLE IF NOT EXISTS R_NAT_ASS (
  COD_NATASS VARCHAR(2),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_NAT_ASS IS 'Liste des Natures d''assurance permise';
  COMMENT ON COLUMN R_NAT_ASS.COD_NATASS IS 'Code Nature d''Assurance';
  COMMENT ON COLUMN R_NAT_ASS.LIBELLE IS 'libellé nature d''assurance';

CREATE TABLE IF NOT EXISTS R_NOTE_ACTE (
  ACTE_COD VARCHAR(13),
  ACDT_MODIF DATE,
  ORDRE_NOTE NUMERIC(4),
  TYPNOT_COD NUMERIC(2),
  TEXTE_NOTE VARCHAR(4000),
  RENVOI_COD VARCHAR(13)
);

COMMENT ON TABLE R_NOTE_ACTE IS 'Note associée à un acte';
  COMMENT ON COLUMN R_NOTE_ACTE.ACTE_COD IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_NOTE_ACTE.ACDT_MODIF IS 'date de mise à jour de l''acte';
  COMMENT ON COLUMN R_NOTE_ACTE.ORDRE_NOTE IS 'numéro de la note dans le type';
  COMMENT ON COLUMN R_NOTE_ACTE.TYPNOT_COD IS 'code de type de note';
  COMMENT ON COLUMN R_NOTE_ACTE.TEXTE_NOTE IS 'CCAM n°11 texte de la note de l''acte';
  COMMENT ON COLUMN R_NOTE_ACTE.RENVOI_COD IS 'code semi structuré de l''acte';

CREATE TABLE IF NOT EXISTS R_NOTE_MENU (
  MENU_COD NUMERIC(6),
  ORDRE_NOTE NUMERIC(4),
  TYPNOT_COD NUMERIC(2),
  TEXTE_NOTE VARCHAR(4000),
  RENVOI_COD VARCHAR(13)
);

COMMENT ON TABLE R_NOTE_MENU IS 'Note associée à un niveau de l''arborescence';
  COMMENT ON COLUMN R_NOTE_MENU.MENU_COD IS 'CCAM n ° 16 Code du Niveau de l''Arborescence';
  COMMENT ON COLUMN R_NOTE_MENU.ORDRE_NOTE IS 'numéro de la note dans le type';
  COMMENT ON COLUMN R_NOTE_MENU.TYPNOT_COD IS 'code de type de note';
  COMMENT ON COLUMN R_NOTE_MENU.TEXTE_NOTE IS 'texte de la note du niveau de l''arborescence';
  COMMENT ON COLUMN R_NOTE_MENU.RENVOI_COD IS 'code semi structuré de l''acte';

CREATE TABLE IF NOT EXISTS R_PAIEMENT (
  COD_PAIEMT VARCHAR(1),
  LIBELLE VARCHAR(80)
);

COMMENT ON TABLE R_PAIEMENT IS 'Liste des Codifications des modalités de paiement des séances';
  COMMENT ON COLUMN R_PAIEMENT.COD_PAIEMT IS 'Code modalité de paiement des séances';
  COMMENT ON COLUMN R_PAIEMENT.LIBELLE IS 'libellé modalité de paiement des séances';

CREATE TABLE IF NOT EXISTS R_PHASE (
  COD_PHASE NUMERIC(2),
  LIBELLE VARCHAR(80)
);

COMMENT ON TABLE R_PHASE IS 'Liste des Codifications des phases d''activité';
  COMMENT ON COLUMN R_PHASE.COD_PHASE IS 'Code Phase de traitement';
  COMMENT ON COLUMN R_PHASE.LIBELLE IS 'libellé phase de traitement';

CREATE TABLE IF NOT EXISTS R_PROCEDURE (
  ACTE_COD VARCHAR(13),
  ACDT_MODIF DATE,
  PROC_COD VARCHAR(13),
  PRDT_MODIF DATE
);

COMMENT ON TABLE R_PROCEDURE IS 'Codes des procédures intégrant cet acte';
  COMMENT ON COLUMN R_PROCEDURE.ACTE_COD IS 'CCAM n° 1 Code semi-structuré de l''acte';
  COMMENT ON COLUMN R_PROCEDURE.ACDT_MODIF IS 'date de mise à jour de l''acte';
  COMMENT ON COLUMN R_PROCEDURE.PROC_COD IS 'CCAM n° 9.1 Code semi structuré de la Procédure';
  COMMENT ON COLUMN R_PROCEDURE.PRDT_MODIF IS 'date de mise à jour de la procédure';

CREATE TABLE IF NOT EXISTS R_PU_BASE (
  AAP_COD VARCHAR(16),
  APDT_MODIF DATE,
  GRILLE_COD NUMERIC(3),
  PU_BASE NUMERIC(6, 2)
);

COMMENT ON TABLE R_PU_BASE IS 'Prix Unitaire de Base de l acte/activité/phase';
  COMMENT ON COLUMN R_PU_BASE.AAP_COD IS 'CCAM n° 1+2+4 Code Phase interne de l''activité de l''acte (Code interne Activité + Code Phase)';
  COMMENT ON COLUMN R_PU_BASE.APDT_MODIF IS 'Date modification de la phase';
  COMMENT ON COLUMN R_PU_BASE.GRILLE_COD IS 'Code de la grille Pour chaque Acte, nous devons retrouver un P.Unitaire de Base de chacune des phases de chacune des activité dans chacune des grille.';
  COMMENT ON COLUMN R_PU_BASE.PU_BASE IS 'Prix Unitaire de Base CCAM N° 37 Pour chaque Acte, nous devons retrouver un P.Unitaire de Base de chacune des phases de chacune des activité dans chacune des grille.';

CREATE TABLE IF NOT EXISTS R_REGROUPEMENT (
  COD_REGROU VARCHAR(3),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_REGROUPEMENT IS 'Liste des Codes Regroupements de codes actes';
  COMMENT ON COLUMN R_REGROUPEMENT.COD_REGROU IS 'Code Regroupement de Codes Actes';
  COMMENT ON COLUMN R_REGROUPEMENT.LIBELLE IS 'descriptif du groupe';

CREATE TABLE IF NOT EXISTS R_REMBOURSEMENT (
  COD_REMBOU NUMERIC(1),
  LIBELLE VARCHAR(80)
);

COMMENT ON TABLE R_REMBOURSEMENT IS 'Liste des Modes d''admission au remboursement';
  COMMENT ON COLUMN R_REMBOURSEMENT.COD_REMBOU IS 'Code mode de remboursement';
  COMMENT ON COLUMN R_REMBOURSEMENT.LIBELLE IS 'libellé mode de remboursement';

CREATE TABLE IF NOT EXISTS R_RMO (
  COD_INTERN NUMERIC(4),
  TITRE VARCHAR(80),
  TEXTE VARCHAR(4000),
  REF_RMO VARCHAR(5)
);

COMMENT ON TABLE R_RMO IS 'Liste des références médicales opposables';
  COMMENT ON COLUMN R_RMO.COD_INTERN IS 'code interne de la RMO';
  COMMENT ON COLUMN R_RMO.TITRE IS 'titre de la RMO';
  COMMENT ON COLUMN R_RMO.TEXTE IS 'CCAM n° 21 Texte de la RMO';
  COMMENT ON COLUMN R_RMO.REF_RMO IS 'CCAM n° 20 Réference RMO';

CREATE TABLE IF NOT EXISTS R_TB01 (
  MODE_TRAIT VARCHAR(2),
  DT_DEBUT DATE,
  DT_FIN DATE
);

COMMENT ON TABLE R_TB01 IS 'Table des modes de traitement impliquant un taux d''hospitalisation.';
  COMMENT ON COLUMN R_TB01.MODE_TRAIT IS 'Code du Mode de traitement.';
  COMMENT ON COLUMN R_TB01.DT_DEBUT IS 'Date de Début.';
  COMMENT ON COLUMN R_TB01.DT_FIN IS 'Date de Fin.';

CREATE TABLE IF NOT EXISTS R_TB02 (
  COD_ASSONP VARCHAR(2),
  DT_DEBUT DATE,
  GRILLE_COD NUMERIC(3),
  LIBELLE VARCHAR(100),
  DT_FIN DATE,
  COEF NUMERIC(4, 3)
);

COMMENT ON TABLE R_TB02 IS 'Associations non prévues Le coef asso. non prévu doit etre rattaché a une grille Tarifaire.';
  COMMENT ON COLUMN R_TB02.COD_ASSONP IS 'code association non prévue';
  COMMENT ON COLUMN R_TB02.DT_DEBUT IS 'date d''effet du coefficient association non prévue';
  COMMENT ON COLUMN R_TB02.GRILLE_COD IS 'code grille';
  COMMENT ON COLUMN R_TB02.LIBELLE IS 'libellé association non prévue';
  COMMENT ON COLUMN R_TB02.DT_FIN IS '';
  COMMENT ON COLUMN R_TB02.COEF IS 'coefficient association non prévue';

CREATE TABLE IF NOT EXISTS R_TB03 (
  COD_REGLE VARCHAR(1),
  DT_DEBUT DATE,
  GRILLE_COD NUMERIC(3),
  LIBELLE VARCHAR(500),
  DT_FIN DATE,
  COEF NUMERIC(4, 3)
);

COMMENT ON TABLE R_TB03 IS 'Règles Tarifaires Le coef asso. prévu doit etre rattaché a une grille Tarifaire.';
  COMMENT ON COLUMN R_TB03.COD_REGLE IS 'CCAM n° 36 Code Règle Tarifaire';
  COMMENT ON COLUMN R_TB03.DT_DEBUT IS 'date d''effet de la règle';
  COMMENT ON COLUMN R_TB03.GRILLE_COD IS 'code grille';
  COMMENT ON COLUMN R_TB03.LIBELLE IS 'règle tarifaire';
  COMMENT ON COLUMN R_TB03.DT_FIN IS '';
  COMMENT ON COLUMN R_TB03.COEF IS 'coefficient associé à la règle';

CREATE TABLE IF NOT EXISTS R_TB04 (
  COD_SPECIA VARCHAR(3),
  DT_DEBUT DATE,
  CATSPE_COD VARCHAR(2),
  LIBELLE VARCHAR(100),
  DT_FIN DATE
);

COMMENT ON TABLE R_TB04 IS 'spécialités exécutants et prescripteurs';
  COMMENT ON COLUMN R_TB04.COD_SPECIA IS 'code spécialité';
  COMMENT ON COLUMN R_TB04.DT_DEBUT IS 'date d''effet de l''association specialité + catégorie';
  COMMENT ON COLUMN R_TB04.CATSPE_COD IS 'code catégorie de spécialité';
  COMMENT ON COLUMN R_TB04.LIBELLE IS 'libellé de la spécialité';
  COMMENT ON COLUMN R_TB04.DT_FIN IS '';

CREATE TABLE IF NOT EXISTS R_TB05 (
  FORFAI_COD VARCHAR(1),
  COD_NATURE VARCHAR(3),
  DT_DEBUT DATE,
  DT_FIN DATE
);

COMMENT ON TABLE R_TB05 IS 'Association des natures de prestations aux forfaits';
  COMMENT ON COLUMN R_TB05.FORFAI_COD IS 'code forfait';
  COMMENT ON COLUMN R_TB05.COD_NATURE IS 'code nature prestation';
  COMMENT ON COLUMN R_TB05.DT_DEBUT IS 'date de début de l''association nature de prestation/type de forfait';
  COMMENT ON COLUMN R_TB05.DT_FIN IS 'date de fin de l''association nature de prestation/type de';

CREATE TABLE IF NOT EXISTS R_TB06 (
  MODIFI_COD VARCHAR(1),
  DT_DEBUT DATE,
  UNI_AGEMIN VARCHAR(1),
  AGE_MIN NUMERIC(3),
  UNI_AGEMAX VARCHAR(1),
  AGE_MAX NUMERIC(3),
  DT_FIN DATE
);

COMMENT ON TABLE R_TB06 IS 'Compatibilités entre codes modificateurs et age du bénéficiaire';
  COMMENT ON COLUMN R_TB06.MODIFI_COD IS 'CCAM n° 6 Code modificateur de l''acte';
  COMMENT ON COLUMN R_TB06.DT_DEBUT IS 'Date d'' effet des compatibilités entre age et modificateur';
  COMMENT ON COLUMN R_TB06.UNI_AGEMIN IS 'Unité de temps de l''age min du modificateur';
  COMMENT ON COLUMN R_TB06.AGE_MIN IS 'Age minimum du bénéficaire';
  COMMENT ON COLUMN R_TB06.UNI_AGEMAX IS 'Unité de temps de l''age max';
  COMMENT ON COLUMN R_TB06.AGE_MAX IS 'Age maximum du bénéficaire';
  COMMENT ON COLUMN R_TB06.DT_FIN IS '';

CREATE TABLE IF NOT EXISTS R_TB07 (
  DT_DEBUT DATE,
  METROPOLE NUMERIC(7, 2),
  ANTILLES NUMERIC(7, 2),
  REUNION NUMERIC(7, 2),
  DT_FIN DATE,
  GUYANE NUMERIC(7, 2)
);

COMMENT ON TABLE R_TB07 IS 'seuil minimum pour exonération TM';
  COMMENT ON COLUMN R_TB07.DT_DEBUT IS 'date d''effet du seuil d''exonération TM';
  COMMENT ON COLUMN R_TB07.METROPOLE IS 'valeur du seuil minimum d''exonération du ticket modérateur pour la métropole';
  COMMENT ON COLUMN R_TB07.ANTILLES IS 'valeur du seuil minimum d''exonération du ticket modérateur pour les antilles';
  COMMENT ON COLUMN R_TB07.REUNION IS 'valeur du seuil minimum d''exonération du ticket modérateur pour la réunion';
  COMMENT ON COLUMN R_TB07.DT_FIN IS '';
  COMMENT ON COLUMN R_TB07.GUYANE IS 'valeur du seuil minimum d''exonération du ticket modérateur pour la guyane';

CREATE TABLE IF NOT EXISTS R_TB08 (
  COD_CAISSE NUMERIC(3),
  DT_DEBUT DATE,
  JOUR_FERIE NUMERIC(8),
  DT_FIN DATE,
  TYPE_JOUR VARCHAR(1)
);

COMMENT ON TABLE R_TB08 IS 'jours fériés spécifiques par caisse';
  COMMENT ON COLUMN R_TB08.COD_CAISSE IS 'code caisse concernée par les jours fériés';
  COMMENT ON COLUMN R_TB08.DT_DEBUT IS 'date d''effet du jour férié';
  COMMENT ON COLUMN R_TB08.JOUR_FERIE IS 'date du jour férié sous forme numérique';
  COMMENT ON COLUMN R_TB08.DT_FIN IS 'date fin du jour férié';
  COMMENT ON COLUMN R_TB08.TYPE_JOUR IS 'type du jour férié (fixe, variable)';

CREATE TABLE IF NOT EXISTS R_TB09 (
  MODIF_COD1 VARCHAR(2),
  MODIF_COD2 VARCHAR(1),
  DT_DEBUT DATE,
  DT_FIN DATE
);

COMMENT ON TABLE R_TB09 IS 'Compatibilités entre modificateurs';
  COMMENT ON COLUMN R_TB09.MODIF_COD1 IS 'CCAM n° 6 Code modificateur de l''acte+';
  COMMENT ON COLUMN R_TB09.MODIF_COD2 IS 'CCAM n° 6 Code modificateur de l''acte';
  COMMENT ON COLUMN R_TB09.DT_DEBUT IS 'Date d''effet de la compatibilité entre les modificateurs';
  COMMENT ON COLUMN R_TB09.DT_FIN IS 'date de fin de la compatibilité entre les modificateurs';

CREATE TABLE IF NOT EXISTS R_TB10 (
  MODIFI_COD VARCHAR(1),
  DT_DEBUT DATE,
  TOP_CTRLE VARCHAR(1),
  TOP_MULTIP VARCHAR(1),
  DT_FIN DATE
);

COMMENT ON TABLE R_TB10 IS 'Cohérence des modificateurs entre les différents codes activités';
  COMMENT ON COLUMN R_TB10.MODIFI_COD IS 'CCAM n° 6 Code modificateur de l''acte';
  COMMENT ON COLUMN R_TB10.DT_DEBUT IS 'Date d''effet de la cohérence des modificateurs';
  COMMENT ON COLUMN R_TB10.TOP_CTRLE IS 'Indication déclenchement contrôle cohérence du modificateur';
  COMMENT ON COLUMN R_TB10.TOP_MULTIP IS 'Présence multiple du modificateur possible dans une même facture';
  COMMENT ON COLUMN R_TB10.DT_FIN IS '';

CREATE TABLE IF NOT EXISTS R_TB11 (
  COD_MODIFI VARCHAR(1),
  DT_DEBUT DATE,
  GRILLE_COD NUMERIC(3),
  COEF NUMERIC(4, 3),
  LIBELLE VARCHAR(100),
  DT_FIN DATE,
  FORFAIT NUMERIC(7, 2)
);

COMMENT ON TABLE R_TB11 IS 'Commentaire Coefficient/forfait des codes modificateurs. Doit être rattaché à une grille tarifaire.';
  COMMENT ON COLUMN R_TB11.COD_MODIFI IS 'CCAM n° 6 Code modificateur de l''acte';
  COMMENT ON COLUMN R_TB11.DT_DEBUT IS 'Date d'' effet du modificateur';
  COMMENT ON COLUMN R_TB11.GRILLE_COD IS '';
  COMMENT ON COLUMN R_TB11.COEF IS 'Taux associé au modificateur';
  COMMENT ON COLUMN R_TB11.LIBELLE IS 'Libellé explicatif du modificateur';
  COMMENT ON COLUMN R_TB11.DT_FIN IS '';
  COMMENT ON COLUMN R_TB11.FORFAIT IS 'Forfait associé au modificateur';

CREATE TABLE IF NOT EXISTS R_TB12 (
  DT_DEBUT DATE,
  FORFAIT_RNC NUMERIC(7, 2),
  COEF_RNC NUMERIC(4, 3),
  DT_FIN DATE
);

COMMENT ON TABLE R_TB12 IS 'réduction base de remboursement pour non conventionnés';
  COMMENT ON COLUMN R_TB12.DT_DEBUT IS 'date d''effet de la réduction base de remboursement pour non conventionnés';
  COMMENT ON COLUMN R_TB12.FORFAIT_RNC IS 'forfait pour la réduction base de remboursement pour non conventionnés';
  COMMENT ON COLUMN R_TB12.COEF_RNC IS 'taux pour la réduction base de remboursement pour non conventionnés';
  COMMENT ON COLUMN R_TB12.DT_FIN IS '';

CREATE TABLE IF NOT EXISTS R_TB13 (
  COD_NATURE VARCHAR(3),
  DT_DEBUT DATE,
  DT_FIN DATE,
  TOP_LC VARCHAR(1)
);

COMMENT ON TABLE R_TB13 IS 'natures de prestation caractérisant le codage CCAM';
  COMMENT ON COLUMN R_TB13.COD_NATURE IS 'code nature prestation';
  COMMENT ON COLUMN R_TB13.DT_DEBUT IS 'date début de la nature de prestation';
  COMMENT ON COLUMN R_TB13.DT_FIN IS 'date fin de la nature de prestation';
  COMMENT ON COLUMN R_TB13.TOP_LC IS 'top équivalent lettre clé de la nature de prestation';

CREATE TABLE IF NOT EXISTS R_TB14 (
  COD_DMT NUMERIC(3),
  DT_DEBUT DATE,
  CLDMT_COD VARCHAR(2),
  LIBELLE VARCHAR(100),
  DT_FIN DATE
);

COMMENT ON TABLE R_TB14 IS 'DMT regroupées dans les classes';
  COMMENT ON COLUMN R_TB14.COD_DMT IS 'code DMT';
  COMMENT ON COLUMN R_TB14.DT_DEBUT IS 'date d''effet de l''association DMT/classe de DMT';
  COMMENT ON COLUMN R_TB14.CLDMT_COD IS '';
  COMMENT ON COLUMN R_TB14.LIBELLE IS 'libellé DMT';
  COMMENT ON COLUMN R_TB14.DT_FIN IS '';

CREATE TABLE IF NOT EXISTS R_TB15 (
  MODIFI_COD VARCHAR(1),
  DT_DEBUT DATE,
  DT_FIN DATE,
  COD_MODOC VARCHAR(1)
);

COMMENT ON TABLE R_TB15 IS 'Codes modificateurs donnant lieu à information aux OC (organismes complémentaires)';
  COMMENT ON COLUMN R_TB15.MODIFI_COD IS 'CCAM n° 6 Code modificateur de l''acte';
  COMMENT ON COLUMN R_TB15.DT_DEBUT IS 'Date début info OC du modificateur';
  COMMENT ON COLUMN R_TB15.DT_FIN IS 'date fin info OC du modificateur';
  COMMENT ON COLUMN R_TB15.COD_MODOC IS 'Code modificateur transmis au organismes complémentaires';

CREATE TABLE IF NOT EXISTS R_TB17 (
  COD_NATURE VARCHAR(3),
  SPECIA_COD VARCHAR(3),
  DT_OBLIGAT DATE
);

COMMENT ON TABLE R_TB17 IS 'dates d''obligation du codage CCAM pour les natures de prestations NGAP';
  COMMENT ON COLUMN R_TB17.COD_NATURE IS 'code nature prestation';
  COMMENT ON COLUMN R_TB17.SPECIA_COD IS 'code spécialité';
  COMMENT ON COLUMN R_TB17.DT_OBLIGAT IS 'date d''obligation du codage CCAM pour les natures de prestations NGAP';

CREATE TABLE IF NOT EXISTS R_TB18 (
  LOC_DENT VARCHAR(2),
  DT_DEBUT DATE,
  DT_FIN DATE
);

COMMENT ON TABLE R_TB18 IS 'localisations des dents';
  COMMENT ON COLUMN R_TB18.LOC_DENT IS 'localisation de la dent';
  COMMENT ON COLUMN R_TB18.DT_DEBUT IS 'date d''effet de la localisation de la dent';
  COMMENT ON COLUMN R_TB18.DT_FIN IS '';

CREATE TABLE IF NOT EXISTS R_TB19 (
  DT_DEBUT DATE,
  NB_MODIFICATEURS NUMERIC(1),
  DT_FIN DATE
);

COMMENT ON TABLE R_TB19 IS 'nombre de modificateurs tarifants';
  COMMENT ON COLUMN R_TB19.DT_DEBUT IS 'date d''effet du nombre de modificateurs tarifants';
  COMMENT ON COLUMN R_TB19.NB_MODIFICATEURS IS 'nombre de modificateurs tarifants';
  COMMENT ON COLUMN R_TB19.DT_FIN IS '';

CREATE TABLE IF NOT EXISTS R_TB20 (
  CONCEPT VARCHAR(5),
  CODE VARCHAR(10),
  DT_DEBUT DATE,
  DT_FIN DATE
);

COMMENT ON TABLE R_TB20 IS 'table de transcodage des évolutions des codifications';
  COMMENT ON COLUMN R_TB20.CONCEPT IS 'type de codification';
  COMMENT ON COLUMN R_TB20.CODE IS 'code permanent';
  COMMENT ON COLUMN R_TB20.DT_DEBUT IS 'date d''effet du transcodage';
  COMMENT ON COLUMN R_TB20.DT_FIN IS '';

CREATE TABLE IF NOT EXISTS R_TB21 (
  CLE_FORFAITAIRE NUMERIC(4),
  COD_CMUC VARCHAR(3),
  DT_D DATE,
  INDICE_TARIFAIRE VARCHAR(2),
  DT_F DATE
);

COMMENT ON TABLE R_TB21 IS 'Forfait CMUC CCAM n°52 Pour connaitre le seuil max(Indice Tarifaire).';
  COMMENT ON COLUMN R_TB21.CLE_FORFAITAIRE IS 'Clé technique entre la table et la Table R_ACTE_CMUC';
  COMMENT ON COLUMN R_TB21.COD_CMUC IS 'Code Forfaitaire';
  COMMENT ON COLUMN R_TB21.DT_D IS 'date de début de validité forfait/indice tarifaire.';
  COMMENT ON COLUMN R_TB21.INDICE_TARIFAIRE IS 'Valeur de l''Indice Tarifaire';
  COMMENT ON COLUMN R_TB21.DT_F IS 'date de fin de validité forfait/indice tarifaire. ';

CREATE TABLE IF NOT EXISTS R_TB22 (
  COD_CONTXTPS NUMERIC(4),
  COD_CONTXTBEN NUMERIC(4),
  GRILLE_COD NUMERIC(3),
  DT_DEBUT DATE,
  DT_FIN DATE
);

COMMENT ON TABLE R_TB22 IS 'Affectation de la Grille en fonction des Contextes';
  COMMENT ON COLUMN R_TB22.COD_CONTXTPS IS 'Code du Contexte du Professionnel de Santé';
  COMMENT ON COLUMN R_TB22.COD_CONTXTBEN IS 'Code du Contexte du Bénéficiaire';
  COMMENT ON COLUMN R_TB22.GRILLE_COD IS 'Code de la Grille';
  COMMENT ON COLUMN R_TB22.DT_DEBUT IS 'Date de Début, pour historisation';
  COMMENT ON COLUMN R_TB22.DT_FIN IS 'date fin';

CREATE TABLE IF NOT EXISTS R_TB23 (
  COD_GRILLE NUMERIC(3),
  LIBELLE VARCHAR(100),
  DEFINITION VARCHAR(250)
);

COMMENT ON TABLE R_TB23 IS 'Grille Tarifaire Libellé et détail des Grilles Tarifaire';
  COMMENT ON COLUMN R_TB23.COD_GRILLE IS 'Code de la Grille';
  COMMENT ON COLUMN R_TB23.LIBELLE IS 'libelle de la grille';
  COMMENT ON COLUMN R_TB23.DEFINITION IS 'Description de la Grille Tarifaire';

CREATE TABLE IF NOT EXISTS R_TYPE (
  COD_TYPE NUMERIC(1),
  LIBELLE VARCHAR(80)
);

COMMENT ON TABLE R_TYPE IS 'Codification du Type d''Acte';
  COMMENT ON COLUMN R_TYPE.COD_TYPE IS 'Code Type Acte';
  COMMENT ON COLUMN R_TYPE.LIBELLE IS 'libellé type acte';

CREATE TABLE IF NOT EXISTS R_TYPE_NOTE (
  COD_TYPNOT NUMERIC(2),
  LIBELLE VARCHAR(100)
);

COMMENT ON TABLE R_TYPE_NOTE IS 'Liste des Types de note possibles';
  COMMENT ON COLUMN R_TYPE_NOTE.COD_TYPNOT IS 'code de type de note';
  COMMENT ON COLUMN R_TYPE_NOTE.LIBELLE IS 'CCAM n° 11 Libellé de type de note';

CREATE TABLE IF NOT EXISTS R_UNITE_OEUVRE (
  COD_UOEUVR VARCHAR(3),
  DT_EFFET DATE,
  VALEUR NUMERIC(6, 2)
);

COMMENT ON TABLE R_UNITE_OEUVRE IS 'Table de valeurs des unités d''oeuvre';
  COMMENT ON COLUMN R_UNITE_OEUVRE.COD_UOEUVR IS 'Code de l''unité d''oeuvre';
  COMMENT ON COLUMN R_UNITE_OEUVRE.DT_EFFET IS 'date d''effet de l''unité d''oeuvre';
  COMMENT ON COLUMN R_UNITE_OEUVRE.VALEUR IS 'valeur associée à l''unité d''oeuvre';

