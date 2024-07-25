---
#:encoding=UTF-8:
layout: page
title: Modèle
permalink: /modelisation/
---

Le modèle des fichiers utilisés pour le PMSI est simple : chaque enregistrement est contenu
sur une ligne, et dans chaque enregistrement, une donnée est retrouvée à une position bien
définie.

Ainsi pour le fichier VIDHOSP qui contient les variables d'identification pour une hospitalisation,
il y a une ligne par hospitalisation, par exemple on sait par la documentation disponible sur le site
de l'ATIH que la date de l’hospitalisation a une longueur de 8 caractères, qu'elle commence
à la colonne 162 et qu'elle finit à la colonne 169.

Ce modèle très simple est très ancien, mais fastidieux à manipuler, car si on ajoute ou enlève
une donnée au début de la ligne, toutes les autres positions sont décalées. Si les positions ont
été entrées directement dans le code ou dans une formule excel, il faut faire une nouvelle version
du code ou de la formule, et tout décaler.




