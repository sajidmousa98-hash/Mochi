package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.model.AdoptedAnimal
import com.example.data.model.DecorationCatalog
import com.example.data.model.DefaultQuestsAndAchievements
import com.example.data.model.GameState
import com.example.data.model.OutfitCatalog
import com.example.data.model.PlacedDecoration
import com.example.data.model.QuestType
import com.example.data.model.SpeciesCatalog
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
  INTRO,
  SANCTUARY,
  CARE,
  RESCUE,
  GAMES,
  DRESS_UP,
  SHOP,
  ENCYCLOPEDIA,
  QUESTS,
  SETTINGS
}

data class FloatingParticle(
  val id: Long = System.nanoTime(),
  val emoji: String,
  val startXRatio: Float,
  val startYRatio: Float
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = GameRepository(application)

  private val _gameState = MutableStateFlow(repository.loadGameState())
  val gameState: StateFlow<GameState> = _gameState.asStateFlow()

  private val _currentScreen = MutableStateFlow(
    if (_gameState.value.isFirstLaunch || _gameState.value.adoptedAnimals.isEmpty()) AppScreen.INTRO
    else AppScreen.SANCTUARY
  )
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _isSanctuaryEditMode = MutableStateFlow(false)
  val isSanctuaryEditMode: StateFlow<Boolean> = _isSanctuaryEditMode.asStateFlow()

  private val _particles = MutableStateFlow<List<FloatingParticle>>(emptyList())
  val particles: StateFlow<List<FloatingParticle>> = _particles.asStateFlow()

  init {
    SoundManager.isSoundEnabled = _gameState.value.soundEnabled
    // If no animals yet and not in intro, ensure discovery catalog
    if (_gameState.value.adoptedAnimals.isNotEmpty() && _gameState.value.activeAnimalId.isEmpty()) {
      _gameState.update { it.copy(activeAnimalId = it.adoptedAnimals.first().id) }
    }
  }

  fun navigateTo(screen: AppScreen) {
    SoundManager.playPop()
    _currentScreen.value = screen
    if (screen != AppScreen.SANCTUARY) {
      _isSanctuaryEditMode.value = false
    }
  }

  fun toggleEditMode() {
    SoundManager.playPop()
    _isSanctuaryEditMode.update { !it }
  }

  private fun save() {
    repository.saveGameState(_gameState.value)
  }

  private fun addExp(amount: Int) {
    _gameState.update { current ->
      var newExp = current.currentExp + amount
      var newLevel = current.sanctuaryLevel
      var coinsBonus = 0
      var starsBonus = 0

      while (newExp >= newLevel * 100) {
        newExp -= newLevel * 100
        newLevel++
        coinsBonus += 150 * newLevel
        starsBonus += 2
        SoundManager.playLevelUp()
      }

      current.copy(
        sanctuaryLevel = newLevel,
        currentExp = newExp,
        playerCoins = current.playerCoins + coinsBonus,
        playerStars = current.playerStars + starsBonus
      )
    }
    save()
  }

  private fun updateQuestProgress(type: QuestType, amount: Int = 1) {
    _gameState.update { current ->
      val updatedQuests = current.dailyQuests.map { q ->
        if (q.questType == type && !q.isClaimed) {
          q.copy(currentCount = (q.currentCount + amount).coerceAtMost(q.targetCount))
        } else q
      }
      current.copy(dailyQuests = updatedQuests)
    }
    save()
  }

  // --- ANIMAL CARE ---

  fun petAnimal(animalId: String) {
    SoundManager.playPetHeart()
    spawnParticle("💖", 0.5f, 0.5f)
    _gameState.update { current ->
      val updated = current.adoptedAnimals.map { a ->
        if (a.id == animalId) {
          a.copy(
            happiness = (a.happiness + 15).coerceAtMost(100),
            affectionExp = a.affectionExp + 10
          )
        } else a
      }
      current.copy(
        adoptedAnimals = updated,
        totalPetsComforted = current.totalPetsComforted + 1
      )
    }
    addExp(10)
    updateQuestProgress(QuestType.PET, 1)
  }

  fun feedAnimal(animalId: String, foodName: String) {
    SoundManager.playEatMunch()
    spawnParticle("🍓", 0.5f, 0.5f)
    _gameState.update { current ->
      val updated = current.adoptedAnimals.map { a ->
        if (a.id == animalId) {
          a.copy(
            hunger = (a.hunger + 25).coerceAtMost(100),
            happiness = (a.happiness + 10).coerceAtMost(100)
          )
        } else a
      }
      current.copy(adoptedAnimals = updated)
    }
    addExp(15)
    updateQuestProgress(QuestType.FEED, 1)
  }

  fun bathAnimal(animalId: String) {
    SoundManager.playCleanBubble()
    spawnParticle("🫧", 0.5f, 0.5f)
    _gameState.update { current ->
      val updated = current.adoptedAnimals.map { a ->
        if (a.id == animalId) {
          a.copy(
            cleanliness = (a.cleanliness + 30).coerceAtMost(100),
            happiness = (a.happiness + 5).coerceAtMost(100)
          )
        } else a
      }
      current.copy(adoptedAnimals = updated)
    }
    addExp(15)
    updateQuestProgress(QuestType.BATH, 1)
  }

  fun brushAnimal(animalId: String) {
    SoundManager.playSparkle()
    spawnParticle("✨", 0.5f, 0.5f)
    _gameState.update { current ->
      val updated = current.adoptedAnimals.map { a ->
        if (a.id == animalId) {
          a.copy(
            cleanliness = (a.cleanliness + 15).coerceAtMost(100),
            happiness = (a.happiness + 20).coerceAtMost(100)
          )
        } else a
      }
      current.copy(adoptedAnimals = updated)
    }
    addExp(12)
    updateQuestProgress(QuestType.BRUSH, 1)
  }

  fun toggleSleepAnimal(animalId: String) {
    SoundManager.playPop()
    _gameState.update { current ->
      val updated = current.adoptedAnimals.map { a ->
        if (a.id == animalId) {
          val nextSleep = !a.isSleeping
          val nextEnergy = if (nextSleep) (a.energy + 40).coerceAtMost(100) else a.energy
          a.copy(isSleeping = nextSleep, energy = nextEnergy)
        } else a
      }
      current.copy(adoptedAnimals = updated)
    }
    save()
  }

  fun renameAnimal(animalId: String, newName: String) {
    if (newName.isBlank()) return
    SoundManager.playPop()
    _gameState.update { current ->
      val updated = current.adoptedAnimals.map { a ->
        if (a.id == animalId) a.copy(customName = newName.trim()) else a
      }
      current.copy(adoptedAnimals = updated)
    }
    save()
  }

  fun setActiveAnimal(animalId: String) {
    SoundManager.playPop()
    _gameState.update { it.copy(activeAnimalId = animalId) }
    save()
  }

  // --- ADOPTION & RESCUE ---

  fun adoptFirstAnimal(speciesId: String, name: String) {
    val animalId = "pet_" + UUID.randomUUID().toString().take(8)
    val newPet = AdoptedAnimal(
      id = animalId,
      speciesId = speciesId,
      customName = if (name.isBlank()) SpeciesCatalog.getById(speciesId).name else name.trim(),
      assignedZoneId = "flower_meadow",
      posXRatio = 0.5f,
      posYRatio = 0.62f
    )
    _gameState.update { current ->
      current.copy(
        adoptedAnimals = listOf(newPet),
        activeAnimalId = animalId,
        encyclopediaDiscoveredIds = current.encyclopediaDiscoveredIds + speciesId,
        isFirstLaunch = false
      )
    }
    SoundManager.playLevelUp()
    save()
    _currentScreen.value = AppScreen.SANCTUARY
  }

  fun rescueAnimal(speciesId: String, name: String? = null) {
    val animalId = "pet_" + UUID.randomUUID().toString().take(8)
    val species = SpeciesCatalog.getById(speciesId)
    val petName = if (!name.isNullOrBlank()) name.trim() else species.name
    val zone = _gameState.value.activeZoneId
    val newPet = AdoptedAnimal(
      id = animalId,
      speciesId = speciesId,
      customName = petName,
      assignedZoneId = zone,
      posXRatio = (0.25f + Math.random().toFloat() * 0.5f),
      posYRatio = (0.50f + Math.random().toFloat() * 0.25f)
    )
    _gameState.update { current ->
      current.copy(
        adoptedAnimals = current.adoptedAnimals + newPet,
        activeAnimalId = animalId,
        encyclopediaDiscoveredIds = current.encyclopediaDiscoveredIds + speciesId
      )
    }
    SoundManager.playLevelUp()
    addExp(50)
    updateQuestProgress(QuestType.RESCUE, 1)
  }

  // --- HABITAT & DECORATIONS ---

  fun switchZone(zoneId: String) {
    SoundManager.playPop()
    _gameState.update { it.copy(activeZoneId = zoneId) }
    save()
  }

  fun placeDecoration(itemTypeId: String, xRatio: Float = 0.5f, yRatio: Float = 0.6f) {
    val newDecor = PlacedDecoration(
      id = "placed_" + UUID.randomUUID().toString().take(8),
      itemTypeId = itemTypeId,
      zoneId = _gameState.value.activeZoneId,
      xRatio = xRatio,
      yRatio = yRatio
    )
    _gameState.update { current ->
      current.copy(placedDecorations = current.placedDecorations + newDecor)
    }
    SoundManager.playPop()
    addExp(10)
    updateQuestProgress(QuestType.DECORATE, 1)
  }

  fun moveDecoration(placedId: String, newX: Float, newY: Float) {
    _gameState.update { current ->
      val updated = current.placedDecorations.map { d ->
        if (d.id == placedId) d.copy(xRatio = newX.coerceIn(0.1f, 0.9f), yRatio = newY.coerceIn(0.2f, 0.85f)) else d
      }
      current.copy(placedDecorations = updated)
    }
    save()
  }

  fun rotateDecoration(placedId: String) {
    SoundManager.playPop()
    _gameState.update { current ->
      val updated = current.placedDecorations.map { d ->
        if (d.id == placedId) d.copy(rotation = (d.rotation + 45f) % 360f) else d
      }
      current.copy(placedDecorations = updated)
    }
    save()
  }

  fun removeDecoration(placedId: String) {
    SoundManager.playPop()
    _gameState.update { current ->
      current.copy(placedDecorations = current.placedDecorations.filterNot { it.id == placedId })
    }
    save()
  }

  // --- MINI-GAMES ---

  fun recordMiniGameResult(score: Int, earnedCoins: Int, earnedStars: Int) {
    SoundManager.playLevelUp()
    _gameState.update { current ->
      val newHigh = current.highStarScore.coerceAtLeast(score)
      current.copy(
        playerCoins = current.playerCoins + earnedCoins,
        playerStars = current.playerStars + earnedStars,
        totalGamesPlayed = current.totalGamesPlayed + 1,
        highStarScore = newHigh
      )
    }
    addExp(score / 5)
    updateQuestProgress(QuestType.PLAY_MINIGAME, 1)
  }

  // --- SHOP & DRESS UP ---

  fun buyDecorationItem(itemTypeId: String): Boolean {
    val item = DecorationCatalog.getById(itemTypeId)
    val state = _gameState.value
    if (state.playerCoins < item.costCoins || state.playerStars < item.costStars) {
      SoundManager.playMiss()
      return false
    }
    SoundManager.playSparkle()
    _gameState.update { current ->
      current.copy(
        playerCoins = current.playerCoins - item.costCoins,
        playerStars = current.playerStars - item.costStars,
        ownedDecorationIds = current.ownedDecorationIds + itemTypeId
      )
    }
    save()
    return true
  }

  fun buyOutfitItem(outfitId: String): Boolean {
    val item = OutfitCatalog.getById(outfitId) ?: return false
    val state = _gameState.value
    if (state.playerCoins < item.costCoins || state.playerStars < item.costStars) {
      SoundManager.playMiss()
      return false
    }
    SoundManager.playSparkle()
    _gameState.update { current ->
      current.copy(
        playerCoins = current.playerCoins - item.costCoins,
        playerStars = current.playerStars - item.costStars,
        ownedOutfitIds = current.ownedOutfitIds + outfitId
      )
    }
    save()
    return true
  }

  fun equipOutfit(animalId: String, outfitId: String, isHat: Boolean) {
    SoundManager.playSparkle()
    _gameState.update { current ->
      val updated = current.adoptedAnimals.map { a ->
        if (a.id == animalId) {
          if (isHat) a.copy(equippedHat = outfitId) else a.copy(equippedAccessory = outfitId)
        } else a
      }
      current.copy(adoptedAnimals = updated)
    }
    save()
  }

  fun unequipOutfit(animalId: String, isHat: Boolean) {
    SoundManager.playPop()
    _gameState.update { current ->
      val updated = current.adoptedAnimals.map { a ->
        if (a.id == animalId) {
          if (isHat) a.copy(equippedHat = null) else a.copy(equippedAccessory = null)
        } else a
      }
      current.copy(adoptedAnimals = updated)
    }
    save()
  }

  // --- QUESTS & ACHIEVEMENTS ---

  fun claimQuestReward(questId: String) {
    val quest = _gameState.value.dailyQuests.firstOrNull { it.id == questId } ?: return
    if (!quest.isCompleted || quest.isClaimed) return
    SoundManager.playSparkle()
    _gameState.update { current ->
      val updated = current.dailyQuests.map { q ->
        if (q.id == questId) q.copy(isClaimed = true) else q
      }
      current.copy(
        playerCoins = current.playerCoins + quest.rewardCoins,
        playerStars = current.playerStars + quest.rewardStars,
        dailyQuests = updated
      )
    }
    addExp(30)
  }

  fun claimAchievementReward(achId: String, rewardCoins: Int, rewardStars: Int) {
    if (_gameState.value.claimedAchievementIds.contains(achId)) return
    SoundManager.playSparkle()
    _gameState.update { current ->
      current.copy(
        playerCoins = current.playerCoins + rewardCoins,
        playerStars = current.playerStars + rewardStars,
        claimedAchievementIds = current.claimedAchievementIds + achId
      )
    }
    addExp(50)
  }

  // --- SETTINGS ---

  fun toggleSound() {
    val next = !_gameState.value.soundEnabled
    SoundManager.isSoundEnabled = next
    _gameState.update { it.copy(soundEnabled = next) }
    save()
  }

  fun toggleReducedMotion() {
    _gameState.update { it.copy(reducedMotion = !it.reducedMotion) }
    save()
  }

  fun resetSaveData() {
    repository.clearAllData()
    _gameState.value = repository.loadGameState()
    _currentScreen.value = AppScreen.INTRO
  }

  private fun spawnParticle(emoji: String, x: Float, y: Float) {
    val p = FloatingParticle(emoji = emoji, startXRatio = x, startYRatio = y)
    _particles.update { (it + p).takeLast(6) }
    viewModelScope.launch {
      kotlinx.coroutines.delay(1200)
      _particles.update { current -> current.filterNot { it.id == p.id } }
    }
  }
}
