KURULUM (Forge 1.20.1 - 47.x):
1. forge.minecraftforge.net adresinden 1.20.1 MDK'yi indirip bir klasore cikarin.
2. MDK icindeki src klasorunu silin, bu projedeki src klasorunu yerine koyun.
3. gradle.properties dosyasinda: mod_id=footballstadium , mod_group_id=com.futbol.stadyum
   (mods.toml bu projede sabit yazildigi icin MDK'deki mods.toml ile ayni degildir; bizimki gecerli olur.)
4. Komut satirinda:  gradlew build
5. build/libs icindeki .jar dosyasini .minecraft/mods klasorune atin.
