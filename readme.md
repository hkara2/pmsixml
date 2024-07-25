<!--:encoding=UTF-8:-->
# PmsiXml

## Introduction
pmsixml est au départ une librairie destinée à gérer les formats texte
à position fixe du PMSI, de manière à pouvoir produire à partir des fichiers
PMSI des fichiers XML pour un traitement plus aisé dans d'autres outils.

La librairie a été créée début 2016 par Harry Karadimas au CHU Henri Mondor,
pour explorer les possibilités d'ajouter des métadonnnées aux différents
fichier PMSI fournis par ATIH, Ameli, et autres.

## Documentation
Les documents sont dans le répertoire "docs".

La documentation principale est écrite au format [asciidoc](https://asciidoctor.org/), dans le fichier `pmsixml.adoc`,
puis transformée en html (fichier [pmsixml.html](docs/pmsixml.html) ) 
et pdf (fichier [pmsixml.pdf](docs/pmsixml.pdf) )

<hr>


Par la suite, j'ai ajouté la possibilité d'écrire des scripts en Groovy pour
utiliser la librairie, ce qui a ouvert beaucoup plus de possilités. Cette
partie a été externalisée dans un projet séparé appelé gpmsi.

L'ensemble du projet a été rebaptisé en interne Obélisque (pour tous ceux qui 
n'ont pas encore accès à la potion des druides) mais le nom pmxixml reste
malgré tout.

Les différents scripts sont utilisés au CHSE depuis le début de 2017.

L'encodage à utiliser pour tous les fichiers textuels est le UTF-8 pour tendre
vers la portabilité entre machines. Pour les fichiers .csv ouverts avec Excel
et les fichiers BAT, continuer à utiliser l'encodage windows-1252.
Pour les fichiers BAT le mieux est de ne pas utiliser de caractères accentués.
Cependant pour les métadonnées qui se trouvent dans le répertoire "resources" 
au format .csv, pour avoir un maximum de portabilité, les fichiers doivent être
sauvegardés avec l'encodage UTF-8. Open Office est la méthode de référence pour
produire ces fichiers car il donne le choix de l'encodage au moment de
sauvegarder les .csv .

Pour construire le projet, on utilise maintenant gradle (cf. https://gradle.org/).
Dans le répertoire du projet, faire :

gradle build
pour faire la compilation et construire les fichiers .class

gradle jar
pour générer le jar à distribuer.

gradle javadoc
pour générer la documentation javadoc

gradle jekyll
pour générer la documentation html

gradle dist
pour contruire une distribution dans le repertoire dist

Pour faire l'ensemble :
gradle build jar javadoc jekyll dist

(recopier à la main le jar produit dans gpmsi lorsque le jar est prêt à
être utilisé)

