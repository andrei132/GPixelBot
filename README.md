# Proiect PA 2022 Halite2016 
## Girnet Andrei si Codreanu Dan 321CBa

## Conţinutul repository-ului:
---
 - GPixelBot/           -> Pachet cu toate clasele scrise de noi, pentru functionarea corecta a bot-ului
 - HaliteParty/         -> Pachet cu clasele care vin in started pack cu mici modificari
 - ./                   -> Makefile si clasa executabil

## Regulile jocului
---
Regulile jocului pot fi gasite [aici](https://2016.halite.io/rules_game.html)
 
## Rulare
---
Makefile-ul are 3 reguli:
 + build    -> Build-eaza botul 
 + run      -> Ruleaza botul
 + clean    -> Sterge tot ce sa creeat la build

## Clase importante
---
 + MyBot        -> Clasa care are metoda main
 + GPixelBot    -> Clasa care reprezinta AI bot-ului
 + UtilsMap     -> Clasa care se ocupa de hartile care ajuta in luarea deciziilor
 + GameMap      -> Clasa care tine informatii despre harta de joc

## Harta de costuri
---
Este o harta pe baza carui botul stie unde sa se extinda, practic se calculeaza pentru fiecare site (productie/putere) * 1000.

Aceasta harta este recalculata pe parcursul jocului.

## Harta de influenta a inamicilor
---
Aceasta harta e formata din posibilii pasi cu o predictie de 3 miscari a unui unit inamic, daca acelasi inamic poate ajunge intr-un site, se aduna la influenta inamicului, influenta lui scade odata cu departarea de la unit

## Strategii
---
 + Pace - Doar ne extindem
 + Atac - Atacam agresiv, in centrul influentei inamicului
 + Aparare - Cel mai bun mod de aparare e atacul, si incercarea inchiderii zonelor deschise

## Functionare
---
Ideea principala consta in formarea unor tinte pe harta si acapararea lor cat mai rapid, cu speranta ca nimeni altcineva nu le va acapara pana nu le luam noi. Daca cumva avem celule la frontiera cu un inamic, atunci inseamna ca se renunta la extendere pentru resurse si se trece in mod de atac, unde tintele devin locurile cu o influenta mai mare a inamicului asa cum asta inseamna ca acolo e o adunatura mai mare de forte, deci exista o posibilitate ca vom putea da un overkill bun.

Dupa instantierea clasei GPixelBot se creaza harta de costuri, dupa care mai departe se va extinde bot-ul si se anunta ca botul e pregatit
```java
    GPixelBot gPixelBot = new GPixelBot(myID, gameMap);
    gPixelBot.getUtilsMap().createCostMap();
    Networking.sendInit("GPixelBot");
```

Deasemenea toate locatiile au o directie viitoare, din start directiile vor fi cele mai favorabile, insa apoi se vor regla pentru a avea cat mai putine combinari peste 265. Acest lucru se obtine prin interschimbare de obicei.
```java
    HashMap<Location, Direction> movesHashTable = new HashMap<>();
    Networking.updateFrame(gameMap);
    gPixelBot.getUtilsMap().createCostMap();
    ArrayList<Location> targetLocations = gPixelBot.getTargetProds();
```

Se actualizeaza celulele de la frontiera si in acelsi timp se verifica daca inca nu suntem in mod de atac.
```java
    gPixelBot.getUtilsMap().updateBorderCells(gPixelBot.getID());
    if (gPixelBot.isAtPiece()) 
        gPixelBot.getUtilsMap().sortExpansionTarget(targetLocations);
    else 
        gPixelBot.getUtilsMap().sortAttackTargets(targetLocations);
```

Se creaza o harta de miscari unde toti aleg sa stea pe loc si se creeaza harta de influenta a inamicilor
```java
    gPixelBot.getUtilsMap().createMoveMap(gPixelBot.getID());
    gPixelBot.getUtilsMap().createEnemyInfluenceMap(gPixelBot.getID());
```

Iteram prin toate locatiile si vedem daca este celula noastra, vedem ce ar fi bine sa facem cu ea, sa stea pe loc sau sa se indrepte catre cea mai apropiata tinta si salvam asta in HashMap-ul cu locatii
```java
 movesHashTable.put(location,gPixelBot.getMoveExpansion(location, targetLocations));
```

Apoi se incearca sa se recalculeze toate miscarile, poate putem da un overkill mai mare, cu conditia ca suntem in atac, incercam sa facem un o aparare a frontierei pentru ca inamicul sa faca un overkill mai mic, si incercam sa nu avem coliziuni mai mari de 265.
```java
    gameMap.getMaxOverkill(movesHashTable, gPixelBot.getUtilsMap().getBorderCells(), gPixelBot.getID());
    gPixelBot.borderDefence();
    gameMap.tryToAvoidCollision(movesHashTable, gPixelBot.getID(), 6);
```
## Bibliografie
---
[Bot-ul din care ne-am inspirat](https://github.com/frabi/halite-2016-bot)

[Articolul cu harta de influenta(1)](http://gameschoolgems.blogspot.com/2009/12/influence-maps-i.html)

[Articolul cu harta de influenta(2)](https://web.archive.org/web/20190717210940/http://aigamedev.com/open/tutorial/influence-map-mechanics/)
