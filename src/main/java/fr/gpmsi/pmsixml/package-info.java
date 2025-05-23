/**
 * Package de lecture et d'écriture de fichiers à l'un des formats PMSI à positions fixes,
 * définis par l'ATIH.
 * 
 * <p>
 * Les métadonnées sont stockées  dans un répertoire extérieur et aussi en tant que "resources"
 * dans le package <code>fr.gpmsi.pmsixml</code> . 
 * Fournit un modèle objet pour traiter un document du PMSI de manière
structurée, pour le transformer en fichier XML.
<p>
<!-- N.B. Javadoc est appelé avec UTF-8. Donc utiliser UTF-8 pour l'encodage de ce fichier. -->
Les fichiers traditionnels du PMSI (RSS, RHS, RHA, VIDHOSP, etc.) ont un format
simple a écrire, mais fastidieux à décoder.
Pour aider à lire ces fichiers de façon plus simple j'ai fait des objets
qui permettent l'utilisation de fichier de métadonnées, permettant ainsi
de désigner un élément par un nom, plutôt que par un numéro de caractère.
L'utilisation se rapproche ainsi de l'utilisation des programmes qui 
manipulent du XML.
Par exemple, voici un exemple de code qui lit un RUM (monrum.txt) et lit le
numéro de dossier administratif local, appelé NADL :
<pre>
    String rss = FileUtils.read("monrum.txt");
    RssReader app = new RssReader();
    FszGroup gn = (FszGroup) app.readOne(rss);
    String nadl = gn.getChildField("NADL").getValue();
</pre>
L'avantage de cette approche est que si le champ est toujours nommé de la même
façon, le code restera toujours utilisable, quelle que soit la version de spécification
du RSS.
Les métadonnées de définition des fichiers se trouvent dans des emplacements
spéciaux qui permettent de les obtenir facilement.
<p>
Cette librairie développe tout son potentiel lorsqu'elle est utilisée avec
Groovy (<a href="https://groovy-lang.org/">https://groovy-lang.org/</a>),
cela permet l'écriture de scripts compacts pour traiter les principaux fichier
des PMSI MCO et RSS.
<p>
Quelques conventions :
<ul>
<li>Tous les index commencent à 0, sauf les numéros de ligne qui commencent à 1,
de manière à les retrouver facilement avec un éditeur de texte.
<li>Tous les fichiers de métadonnées tant en 'resource' qu'en fichier local
sont encodées en UTF-8.
</ul>
 */
package fr.gpmsi.pmsixml;