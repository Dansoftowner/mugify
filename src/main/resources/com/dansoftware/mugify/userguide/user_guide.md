# Mugify felhasználói útmutató

*Készítette: Györffy Dániel Programtervező informatikus hallgató*

> Az alkalmazás az Objektumorientált Programozás (GKNB_MSTM070) nevű tárgy félévi követelményeként készült ([sze.hu/~pusztai](http://www.sze.hu/~pusztai/)).

## Rövid ismertető

A ***Mugify*** egy bögre-tervező program, aminek a segítségével egy bögrét lehet testreszabni.

A bögrét, amit szerkesztünk meg lehet tekinteni **felülnézetben**, **oldalnézetben**, **alulnézetben**, sőt
egy **3D-ben forgatható változatban** is. Természetesen a munkáinkat tudjuk menteni (`.mugify` kiterjesztésű) fájlokba,
amiket később bármikor újra megnyithatunk.

Amikor a program elindul, egy kiinduló bögre példány már rendelkezésre áll, amit a `Bögre szerkesztő panel` segítségével
ízlésünk szerint alakíthatunk. Van lehetőség **új, véletlenszerű bögrék generálására** is, ha úgy kívánjuk.


## Az alkalmazás felületének struktúrája

A grafikus felület az alábbi fő részekből áll:
* (**1**) Menüsáv (legfelül)
* (**2**) Felső eszköztár (közvetlenül a menüsáv alatt)
* (**3**) "Bögre tulajdonságai" panel (bal oldalon) 
* (**4**) "Bögre szerkesztő" panel (jobb oldalon)
* (**5**) A bögre tekintő felület (középen) 
* (**6**) Alsó eszköztár

![Alkalmazás felépítése](img/app_structure.png)

## Felhasználói felület témái

Az alkalmazás rendelkezik **világos** és **sötét** móddal is. Sőt, arra is van lehetőség, hogy az alkalmazás felülete
igazodjon az operációs rendszeren beállított felhasználói témához.

Ez azonkívül, hogy felhasználó-barátabbá teszi a programot, egy olyan gyakorlati haszonnal is rendelkezik, hogy ha 
olyan szín(eke)t állítunk be a bögre példányunknak, ami nem jól látszik az adott módban ("beleolvad" a környezetbe), akkor van lehetőség a másik módra átváltani.

![A felület világos és sötét módban](img/ui_themes.png)

Következőképpen tudjuk ezeket a módokat váltogatni:
1. Menüsávból: `Nézet` > `Téma` > `Sötét`/`Világos`/`Szinkronizálás az op.rendszerrel`
    - ![](img/ui_themes_menubar.png)
2. Vagy az alsó eszköztárból:
    - ![](img/ui_themes_bottombar.png)

## A felhasználói felület nyelve

A program úgy lett megírva, hogy több nyelvet is tudjon támogatni.
Demonstrációként a program támogatja a magyar nyelven kívül az angol nyelvet is.

Következőképpen tudjuk a nyelveket váltogatni:
1. Menüsávból: `Nyelv` > `angol`/`magyar`
    - ![](img/lang_menubar.png)
2. Vagy az alsó eszköztárból:
    - ![](img/lang_bottombar.png)

Amint valamelyik nyelvre átváltunk, a felület azonnal átvált a kívánt nyelvre.

