package com.example.data.model

enum class QuestType {
  FEED,
  PET,
  BATH,
  BRUSH,
  PLAY_MINIGAME,
  DECORATE,
  RESCUE
}

data class DailyQuest(
  val id: String,
  val title: String,
  val description: String,
  val questType: QuestType,
  val targetCount: Int,
  val currentCount: Int = 0,
  val rewardCoins: Int,
  val rewardStars: Int = 0,
  val isClaimed: Boolean = false
) {
  val isCompleted: Boolean
    get() = currentCount >= targetCount
}

data class AchievementBadge(
  val id: String,
  val title: String,
  val description: String,
  val iconEmoji: String,
  val target: Int,
  val current: Int,
  val rewardCoins: Int,
  val rewardStars: Int,
  val isClaimed: Boolean = false
) {
  val isCompleted: Boolean
    get() = current >= target
}

object DefaultQuestsAndAchievements {
  fun generateDailyQuests(): List<DailyQuest> {
    return listOf(
      DailyQuest("q_pet", "Gentle Touches", "Pet any animal 3 times with love", QuestType.PET, 3, 0, 100, 1),
      DailyQuest("q_feed", "Snack Time", "Feed yummy treats to 2 hungry animals", QuestType.FEED, 2, 0, 80, 0),
      DailyQuest("q_bath", "Bubble Scrub", "Give 1 warm bubbly bath", QuestType.BATH, 1, 0, 75, 0),
      DailyQuest("q_brush", "Fluffy Grooming", "Brush fur or feathers 2 times", QuestType.BRUSH, 2, 0, 90, 0),
      DailyQuest("q_game", "Arcade Fun", "Play any mini-game 1 time", QuestType.PLAY_MINIGAME, 1, 0, 120, 1),
      DailyQuest("q_decor", "Sanctuary Touch", "Place or move 1 decoration", QuestType.DECORATE, 1, 0, 150, 1)
    )
  }

  fun getAchievements(
    petCount: Int,
    rescuedCount: Int,
    decorCount: Int,
    gamesPlayed: Int,
    highStarScore: Int,
    claimedSet: Set<String>
  ): List<AchievementBadge> {
    return listOf(
      AchievementBadge("ach_first_rescue", "First Rescue", "Rescue your very first lost animal companion", "🐾", 1, rescuedCount, 150, 2, claimedSet.contains("ach_first_rescue")),
      AchievementBadge("ach_sanctuary_5", "Budding Haven", "Rescue 5 animals to live in the sanctuary", "🏡", 5, rescuedCount, 300, 5, claimedSet.contains("ach_sanctuary_5")),
      AchievementBadge("ach_sanctuary_12", "Sanctuary Paradise", "Welcome 12 happy animals to your sanctuary", "🌈", 12, rescuedCount, 800, 12, claimedSet.contains("ach_sanctuary_12")),
      AchievementBadge("ach_caretaker_10", "Tender Heart", "Pet and comfort animals 10 times", "💖", 10, petCount, 100, 1, claimedSet.contains("ach_caretaker_10")),
      AchievementBadge("ach_caretaker_50", "Master Whisperer", "Pet and comfort animals 50 times", "✨", 50, petCount, 400, 5, claimedSet.contains("ach_caretaker_50")),
      AchievementBadge("ach_decorator_5", "Cozy Stylist", "Place 5 decorations in your habitats", "🪴", 5, decorCount, 200, 2, claimedSet.contains("ach_decorator_5")),
      AchievementBadge("ach_decorator_15", "Landscape Artist", "Place 15 decorations across your zones", "🎨", 15, decorCount, 500, 6, claimedSet.contains("ach_decorator_15")),
      AchievementBadge("ach_gamer_5", "Playful Pal", "Play 5 sanctuary mini-games", "🎮", 5, gamesPlayed, 250, 3, claimedSet.contains("ach_gamer_5")),
      AchievementBadge("ach_star_catcher_150", "Star Gazer", "Score 150+ points in Star Catcher", "⭐", 150, highStarScore, 300, 4, claimedSet.contains("ach_star_catcher_150"))
    )
  }
}
