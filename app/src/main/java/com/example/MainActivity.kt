package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.care.AnimalCareScreen
import com.example.ui.components.SanctuaryBottomNavBar
import com.example.ui.components.SanctuaryTopBar
import com.example.ui.encyclopedia.EncyclopediaScreen
import com.example.ui.games.MiniGamesScreen
import com.example.ui.intro.IntroScreen
import com.example.ui.quests.QuestsScreen
import com.example.ui.rescue.RescueScreen
import com.example.ui.sanctuary.SanctuaryScreen
import com.example.ui.settings.SettingsDialog
import com.example.ui.shop.ShopAndDressUpScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
  private val viewModel: GameViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MochiWorldApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MochiWorldApp(viewModel: GameViewModel) {
  val gameState by viewModel.gameState.collectAsStateWithLifecycle()
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val isEditMode by viewModel.isSanctuaryEditMode.collectAsStateWithLifecycle()
  val particles by viewModel.particles.collectAsStateWithLifecycle()

  var showSettings by remember { mutableStateOf(false) }

  // System Back Navigation
  BackHandler(enabled = currentScreen != AppScreen.SANCTUARY || isEditMode) {
    if (isEditMode) {
      viewModel.toggleEditMode()
    } else if (currentScreen != AppScreen.SANCTUARY) {
      viewModel.navigateTo(AppScreen.SANCTUARY)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      if (currentScreen != AppScreen.INTRO) {
        SanctuaryTopBar(
          gameState = gameState,
          isEditMode = isEditMode,
          onToggleEditMode = { viewModel.toggleEditMode() },
          onOpenSettings = { showSettings = true }
        )
      }
    },
    bottomBar = {
      if (currentScreen != AppScreen.INTRO && !isEditMode) {
        SanctuaryBottomNavBar(
          currentScreen = currentScreen,
          onSelectScreen = { screen -> viewModel.navigateTo(screen) }
        )
      }
    }
  ) { innerPadding ->
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      val totalW = maxWidth
      val totalH = maxHeight

      when (currentScreen) {
        AppScreen.INTRO -> {
          IntroScreen(
            onAdoptFirst = { speciesId, name ->
              viewModel.adoptFirstAnimal(speciesId, name)
            }
          )
        }
        AppScreen.SANCTUARY -> {
          SanctuaryScreen(
            gameState = gameState,
            isEditMode = isEditMode,
            onSwitchZone = { viewModel.switchZone(it) },
            onPlaceDecoration = { typeId, x, y -> viewModel.placeDecoration(typeId, x, y) },
            onMoveDecoration = { id, x, y -> viewModel.moveDecoration(id, x, y) },
            onRotateDecoration = { viewModel.rotateDecoration(it) },
            onRemoveDecoration = { viewModel.removeDecoration(it) },
            onPetAnimal = { viewModel.petAnimal(it) },
            onSelectAnimalForCare = {
              viewModel.setActiveAnimal(it)
              viewModel.navigateTo(AppScreen.CARE)
            },
            onToggleEditMode = { viewModel.toggleEditMode() }
          )
        }
        AppScreen.CARE -> {
          AnimalCareScreen(
            adoptedAnimals = gameState.adoptedAnimals,
            activeAnimalId = gameState.activeAnimalId,
            onSelectActiveAnimal = { viewModel.setActiveAnimal(it) },
            onPet = { viewModel.petAnimal(it) },
            onFeed = { id, food -> viewModel.feedAnimal(id, food) },
            onBath = { viewModel.bathAnimal(it) },
            onBrush = { viewModel.brushAnimal(it) },
            onToggleSleep = { viewModel.toggleSleepAnimal(it) },
            onRename = { id, name -> viewModel.renameAnimal(id, name) }
          )
        }
        AppScreen.RESCUE -> {
          RescueScreen(
            gameState = gameState,
            onRescueAnimal = { speciesId, name -> viewModel.rescueAnimal(speciesId, name) }
          )
        }
        AppScreen.GAMES -> {
          MiniGamesScreen(
            highScore = gameState.highStarScore,
            onRewardEarned = { score, coins, stars ->
              viewModel.recordMiniGameResult(score, coins, stars)
            }
          )
        }
        AppScreen.DRESS_UP, AppScreen.SHOP -> {
          ShopAndDressUpScreen(
            gameState = gameState,
            onBuyDecoration = { viewModel.buyDecorationItem(it) },
            onBuyOutfit = { viewModel.buyOutfitItem(it) },
            onEquipOutfit = { animalId, outfitId, isHat ->
              viewModel.equipOutfit(animalId, outfitId, isHat)
            },
            onUnequipOutfit = { animalId, isHat ->
              viewModel.unequipOutfit(animalId, isHat)
            }
          )
        }
        AppScreen.ENCYCLOPEDIA -> {
          EncyclopediaScreen(
            gameState = gameState
          )
        }
        AppScreen.QUESTS -> {
          QuestsScreen(
            gameState = gameState,
            onClaimQuest = { viewModel.claimQuestReward(it) },
            onClaimAchievement = { id, coins, stars ->
              viewModel.claimAchievementReward(id, coins, stars)
            }
          )
        }
        AppScreen.SETTINGS -> {
          // Fallback if navigated to settings screen directly
          SanctuaryScreen(
            gameState = gameState,
            isEditMode = false,
            onSwitchZone = { viewModel.switchZone(it) },
            onPlaceDecoration = { typeId, x, y -> viewModel.placeDecoration(typeId, x, y) },
            onMoveDecoration = { id, x, y -> viewModel.moveDecoration(id, x, y) },
            onRotateDecoration = { viewModel.rotateDecoration(it) },
            onRemoveDecoration = { viewModel.removeDecoration(it) },
            onPetAnimal = { viewModel.petAnimal(it) },
            onSelectAnimalForCare = {
              viewModel.setActiveAnimal(it)
              viewModel.navigateTo(AppScreen.CARE)
            },
            onToggleEditMode = { viewModel.toggleEditMode() }
          )
        }
      }

      // Floating celebratory particles (e.g. hearts, bubbles, stars when petting/feeding)
      particles.forEach { p ->
        val px = totalW * p.startXRatio
        val py = totalH * p.startYRatio
        Text(
          text = p.emoji,
          fontSize = 24.sp,
          modifier = Modifier
            .offset { IntOffset(px.roundToPx(), py.roundToPx()) }
        )
      }
    }
  }

  // Settings Dialog
  if (showSettings) {
    SettingsDialog(
      soundEnabled = gameState.soundEnabled,
      reducedMotion = gameState.reducedMotion,
      onToggleSound = { viewModel.toggleSound() },
      onToggleReducedMotion = { viewModel.toggleReducedMotion() },
      onResetProgress = { viewModel.resetSaveData() },
      onDismiss = { showSettings = false }
    )
  }
}
