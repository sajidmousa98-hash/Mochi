package com.example.data.model

data class GameState(
  val playerCoins: Int = 300,
  val playerStars: Int = 5,
  val sanctuaryLevel: Int = 1,
  val currentExp: Int = 0,
  val adoptedAnimals: List<AdoptedAnimal> = emptyList(),
  val activeAnimalId: String = "",
  val placedDecorations: List<PlacedDecoration> = emptyList(),
  val unlockedZoneIds: Set<String> = setOf("flower_meadow", "cozy_home"),
  val activeZoneId: String = "flower_meadow",
  val ownedDecorationIds: List<String> = listOf("bed_cloud", "flora_sakura", "toy_yarn"),
  val ownedOutfitIds: Set<String> = setOf("hat_flower_crown", "acc_ribbon_bow"),
  val encyclopediaDiscoveredIds: Set<String> = emptySet(),
  val dailyQuests: List<DailyQuest> = DefaultQuestsAndAchievements.generateDailyQuests(),
  val claimedAchievementIds: Set<String> = emptySet(),
  val totalPetsComforted: Int = 0,
  val totalGamesPlayed: Int = 0,
  val highStarScore: Int = 0,
  val isFirstLaunch: Boolean = true,
  val soundEnabled: Boolean = true,
  val reducedMotion: Boolean = false
) {
  val requiredExpForNextLevel: Int
    get() = sanctuaryLevel * 100

  val activeAnimal: AdoptedAnimal?
    get() = adoptedAnimals.firstOrNull { it.id == activeAnimalId } ?: adoptedAnimals.firstOrNull()
}
