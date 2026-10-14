package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AdoptedAnimal
import com.example.data.model.DefaultQuestsAndAchievements
import com.example.data.model.GameState
import com.example.data.model.PlacedDecoration
import com.example.data.model.DailyQuest
import com.example.data.model.QuestType
import org.json.JSONArray
import org.json.JSONObject

class GameRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("mochi_world_game_prefs", Context.MODE_PRIVATE)

  fun saveGameState(state: GameState) {
    try {
      val json = JSONObject().apply {
        put("playerCoins", state.playerCoins)
        put("playerStars", state.playerStars)
        put("sanctuaryLevel", state.sanctuaryLevel)
        put("currentExp", state.currentExp)
        put("activeAnimalId", state.activeAnimalId)
        put("activeZoneId", state.activeZoneId)
        put("totalPetsComforted", state.totalPetsComforted)
        put("totalGamesPlayed", state.totalGamesPlayed)
        put("highStarScore", state.highStarScore)
        put("isFirstLaunch", state.isFirstLaunch)
        put("soundEnabled", state.soundEnabled)
        put("reducedMotion", state.reducedMotion)

        // Adopted animals
        val animalsArr = JSONArray()
        for (a in state.adoptedAnimals) {
          animalsArr.put(JSONObject().apply {
            put("id", a.id)
            put("speciesId", a.speciesId)
            put("customName", a.customName)
            put("hunger", a.hunger)
            put("happiness", a.happiness)
            put("cleanliness", a.cleanliness)
            put("energy", a.energy)
            put("affectionLevel", a.affectionLevel)
            put("affectionExp", a.affectionExp)
            put("equippedHat", a.equippedHat ?: "")
            put("equippedAccessory", a.equippedAccessory ?: "")
            put("variant", a.variant)
            put("assignedZoneId", a.assignedZoneId)
            put("posXRatio", a.posXRatio.toDouble())
            put("posYRatio", a.posYRatio.toDouble())
            put("isSleeping", a.isSleeping)
            put("adoptedAtTimestamp", a.adoptedAtTimestamp)
          })
        }
        put("adoptedAnimals", animalsArr)

        // Placed decorations
        val decorArr = JSONArray()
        for (d in state.placedDecorations) {
          decorArr.put(JSONObject().apply {
            put("id", d.id)
            put("itemTypeId", d.itemTypeId)
            put("zoneId", d.zoneId)
            put("xRatio", d.xRatio.toDouble())
            put("yRatio", d.yRatio.toDouble())
            put("rotation", d.rotation.toDouble())
          })
        }
        put("placedDecorations", decorArr)

        // String sets
        put("unlockedZoneIds", JSONArray(state.unlockedZoneIds))
        put("ownedDecorationIds", JSONArray(state.ownedDecorationIds))
        put("ownedOutfitIds", JSONArray(state.ownedOutfitIds))
        put("encyclopediaDiscoveredIds", JSONArray(state.encyclopediaDiscoveredIds))
        put("claimedAchievementIds", JSONArray(state.claimedAchievementIds))

        // Daily quests
        val questsArr = JSONArray()
        for (q in state.dailyQuests) {
          questsArr.put(JSONObject().apply {
            put("id", q.id)
            put("title", q.title)
            put("description", q.description)
            put("questType", q.questType.name)
            put("targetCount", q.targetCount)
            put("currentCount", q.currentCount)
            put("rewardCoins", q.rewardCoins)
            put("rewardStars", q.rewardStars)
            put("isClaimed", q.isClaimed)
          })
        }
        put("dailyQuests", questsArr)
      }

      prefs.edit().putString(KEY_GAME_STATE, json.toString()).apply()
    } catch (_: Exception) {
      // In case of write error, preserve previous save
    }
  }

  fun loadGameState(): GameState {
    val rawJson = prefs.getString(KEY_GAME_STATE, null) ?: return defaultInitialState()
    return try {
      val json = JSONObject(rawJson)
      val coins = json.optInt("playerCoins", 300)
      val stars = json.optInt("playerStars", 5)
      val level = json.optInt("sanctuaryLevel", 1)
      val exp = json.optInt("currentExp", 0)
      val activeAnimalId = json.optString("activeAnimalId", "")
      val activeZoneId = json.optString("activeZoneId", "flower_meadow")
      val totalPetsComforted = json.optInt("totalPetsComforted", 0)
      val totalGamesPlayed = json.optInt("totalGamesPlayed", 0)
      val highStarScore = json.optInt("highStarScore", 0)
      val isFirstLaunch = json.optBoolean("isFirstLaunch", true)
      val soundEnabled = json.optBoolean("soundEnabled", true)
      val reducedMotion = json.optBoolean("reducedMotion", false)

      // Animals
      val animals = mutableListOf<AdoptedAnimal>()
      val animalsArr = json.optJSONArray("adoptedAnimals")
      if (animalsArr != null) {
        for (i in 0 until animalsArr.length()) {
          val obj = animalsArr.getJSONObject(i)
          val hat = obj.optString("equippedHat", "").takeIf { it.isNotEmpty() }
          val acc = obj.optString("equippedAccessory", "").takeIf { it.isNotEmpty() }
          animals.add(
            AdoptedAnimal(
              id = obj.getString("id"),
              speciesId = obj.getString("speciesId"),
              customName = obj.optString("customName", "Mochi"),
              hunger = obj.optInt("hunger", 80),
              happiness = obj.optInt("happiness", 90),
              cleanliness = obj.optInt("cleanliness", 85),
              energy = obj.optInt("energy", 90),
              affectionLevel = obj.optInt("affectionLevel", 1),
              affectionExp = obj.optInt("affectionExp", 0),
              equippedHat = hat,
              equippedAccessory = acc,
              variant = obj.optString("variant", "NORMAL"),
              assignedZoneId = obj.optString("assignedZoneId", "flower_meadow"),
              posXRatio = obj.optDouble("posXRatio", 0.5).toFloat(),
              posYRatio = obj.optDouble("posYRatio", 0.6).toFloat(),
              isSleeping = obj.optBoolean("isSleeping", false),
              adoptedAtTimestamp = obj.optLong("adoptedAtTimestamp", System.currentTimeMillis())
            )
          )
        }
      }

      // Decorations
      val decorations = mutableListOf<PlacedDecoration>()
      val decorArr = json.optJSONArray("placedDecorations")
      if (decorArr != null) {
        for (i in 0 until decorArr.length()) {
          val obj = decorArr.getJSONObject(i)
          decorations.add(
            PlacedDecoration(
              id = obj.getString("id"),
              itemTypeId = obj.getString("itemTypeId"),
              zoneId = obj.optString("zoneId", "flower_meadow"),
              xRatio = obj.optDouble("xRatio", 0.5).toFloat(),
              yRatio = obj.optDouble("yRatio", 0.5).toFloat(),
              rotation = obj.optDouble("rotation", 0.0).toFloat()
            )
          )
        }
      }

      val unlockedZones = jsonArrayToSet(json.optJSONArray("unlockedZoneIds"))
        .ifEmpty { setOf("flower_meadow", "cozy_home") }
      val ownedDecor = jsonArrayToList(json.optJSONArray("ownedDecorationIds"))
        .ifEmpty { listOf("bed_cloud", "flora_sakura", "toy_yarn") }
      val ownedOutfits = jsonArrayToSet(json.optJSONArray("ownedOutfitIds"))
        .ifEmpty { setOf("hat_flower_crown", "acc_ribbon_bow") }
      val encyclopediaDiscovered = jsonArrayToSet(json.optJSONArray("encyclopediaDiscoveredIds"))
      val claimedAchievements = jsonArrayToSet(json.optJSONArray("claimedAchievementIds"))

      val quests = mutableListOf<DailyQuest>()
      val questsArr = json.optJSONArray("dailyQuests")
      if (questsArr != null && questsArr.length() > 0) {
        for (i in 0 until questsArr.length()) {
          val obj = questsArr.getJSONObject(i)
          quests.add(
            DailyQuest(
              id = obj.getString("id"),
              title = obj.getString("title"),
              description = obj.getString("description"),
              questType = runCatching { QuestType.valueOf(obj.getString("questType")) }.getOrDefault(QuestType.PET),
              targetCount = obj.getInt("targetCount"),
              currentCount = obj.optInt("currentCount", 0),
              rewardCoins = obj.getInt("rewardCoins"),
              rewardStars = obj.optInt("rewardStars", 0),
              isClaimed = obj.optBoolean("isClaimed", false)
            )
          )
        }
      } else {
        quests.addAll(DefaultQuestsAndAchievements.generateDailyQuests())
      }

      GameState(
        playerCoins = coins,
        playerStars = stars,
        sanctuaryLevel = level,
        currentExp = exp,
        adoptedAnimals = animals,
        activeAnimalId = activeAnimalId,
        placedDecorations = decorations,
        unlockedZoneIds = unlockedZones,
        activeZoneId = activeZoneId,
        ownedDecorationIds = ownedDecor,
        ownedOutfitIds = ownedOutfits,
        encyclopediaDiscoveredIds = encyclopediaDiscovered,
        dailyQuests = quests,
        claimedAchievementIds = claimedAchievements,
        totalPetsComforted = totalPetsComforted,
        totalGamesPlayed = totalGamesPlayed,
        highStarScore = highStarScore,
        isFirstLaunch = isFirstLaunch,
        soundEnabled = soundEnabled,
        reducedMotion = reducedMotion
      )
    } catch (_: Exception) {
      defaultInitialState()
    }
  }

  private fun defaultInitialState(): GameState {
    return GameState(
      playerCoins = 400,
      playerStars = 10,
      sanctuaryLevel = 1,
      currentExp = 0,
      adoptedAnimals = emptyList(),
      placedDecorations = listOf(
        PlacedDecoration("init_bed", "bed_cloud", "flower_meadow", 0.35f, 0.65f),
        PlacedDecoration("init_tree", "flora_sakura", "flower_meadow", 0.75f, 0.45f)
      ),
      unlockedZoneIds = setOf("flower_meadow", "cozy_home"),
      activeZoneId = "flower_meadow",
      ownedDecorationIds = listOf("bed_cloud", "flora_sakura", "toy_yarn"),
      ownedOutfitIds = setOf("hat_flower_crown", "acc_ribbon_bow"),
      encyclopediaDiscoveredIds = emptySet(),
      isFirstLaunch = true
    )
  }

  private fun jsonArrayToSet(arr: JSONArray?): Set<String> {
    if (arr == null) return emptySet()
    val set = mutableSetOf<String>()
    for (i in 0 until arr.length()) {
      set.add(arr.getString(i))
    }
    return set
  }

  private fun jsonArrayToList(arr: JSONArray?): List<String> {
    if (arr == null) return emptyList()
    val list = mutableListOf<String>()
    for (i in 0 until arr.length()) {
      list.add(arr.getString(i))
    }
    return list
  }

  fun clearAllData() {
    prefs.edit().clear().apply()
  }

  companion object {
    private const val KEY_GAME_STATE = "mochi_game_state_json"
  }
}
