package com.example.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.SpeciesCatalog
import com.example.ui.art.MochiAnimalView
import com.example.ui.theme.MochiBabyBlue
import com.example.ui.theme.MochiButterYellow
import com.example.ui.theme.MochiDarkBrown
import com.example.ui.theme.MochiLavender
import com.example.ui.theme.MochiMint
import com.example.ui.theme.MochiSakuraDeep
import com.example.ui.theme.MochiSakuraLight
import com.example.ui.theme.MochiSakuraPink
import com.example.ui.theme.MochiSoftBrown
import com.example.ui.theme.MochiStarGold
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

enum class ActiveMiniGame {
  NONE,
  STAR_CATCHER,
  MOCHI_MEMORY,
  FEEDING_FRENZY,
  ANIMAL_RESCUE
}

@Composable
fun MiniGamesScreen(
  highScore: Int,
  onRewardEarned: (score: Int, coins: Int, stars: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var activeGame by remember { mutableStateOf(ActiveMiniGame.NONE) }

  when (activeGame) {
    ActiveMiniGame.NONE -> {
      MiniGameHub(
        highScore = highScore,
        onSelectGame = { activeGame = it },
        modifier = modifier
      )
    }
    ActiveMiniGame.STAR_CATCHER -> {
      StarCatcherGame(
        onFinish = { score ->
          val coins = (score * 1.5).toInt()
          val stars = if (score >= 100) 2 else if (score >= 50) 1 else 0
          onRewardEarned(score, coins, stars)
          activeGame = ActiveMiniGame.NONE
        },
        onExit = { activeGame = ActiveMiniGame.NONE },
        modifier = modifier
      )
    }
    ActiveMiniGame.MOCHI_MEMORY -> {
      MochiMemoryGame(
        onFinish = { score ->
          onRewardEarned(score, score * 2, 2)
          activeGame = ActiveMiniGame.NONE
        },
        onExit = { activeGame = ActiveMiniGame.NONE },
        modifier = modifier
      )
    }
    ActiveMiniGame.FEEDING_FRENZY -> {
      FeedingFrenzyGame(
        onFinish = { score ->
          onRewardEarned(score, score * 2, if (score >= 80) 2 else 1)
          activeGame = ActiveMiniGame.NONE
        },
        onExit = { activeGame = ActiveMiniGame.NONE },
        modifier = modifier
      )
    }
    ActiveMiniGame.ANIMAL_RESCUE -> {
      AnimalRescueAdventureGame(
        onFinish = { score ->
          onRewardEarned(score, 180, 3)
          activeGame = ActiveMiniGame.NONE
        },
        onExit = { activeGame = ActiveMiniGame.NONE },
        modifier = modifier
      )
    }
  }
}

@Composable
fun MiniGameHub(
  highScore: Int,
  onSelectGame: (ActiveMiniGame) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(MochiSakuraLight, MochiLavender.copy(alpha = 0.35f), Color.White)
        )
      )
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Text(
      text = "🎮 Sanctuary Arcade",
      fontSize = 20.sp,
      fontWeight = FontWeight.Black,
      color = MochiDarkBrown
    )
    Text(
      text = "Play cozy mini-games to earn coins, stars, and rare treats!",
      fontSize = 12.sp,
      color = MochiSoftBrown
    )

    Spacer(modifier = Modifier.height(14.dp))

    GameCardItem(
      title = "⭐ Star Catcher",
      desc = "Catch falling starlight and glowing meteor cookies in your cloud basket!",
      reward = "+Coins & Stars",
      icon = "🧺",
      containerColor = MochiButterYellow.copy(alpha = 0.3f),
      onClick = { onSelectGame(ActiveMiniGame.STAR_CATCHER) },
      tag = "play_star_catcher_button"
    )

    Spacer(modifier = Modifier.height(10.dp))

    GameCardItem(
      title = "🃏 Mochi Memory",
      desc = "Match pairs of adorable illustrated sanctuary animals before time runs out!",
      reward = "+Bonus Coins",
      icon = "🐾",
      containerColor = MochiSakuraLight,
      onClick = { onSelectGame(ActiveMiniGame.MOCHI_MEMORY) },
      tag = "play_mochi_memory_button"
    )

    Spacer(modifier = Modifier.height(10.dp))

    GameCardItem(
      title = "🍓 Feeding Frenzy",
      desc = "Hungry animals are ordering treats! Tap the matching food dispenser quickly!",
      reward = "+Treats & XP",
      icon = "🍱",
      containerColor = MochiMint.copy(alpha = 0.3f),
      onClick = { onSelectGame(ActiveMiniGame.FEEDING_FRENZY) },
      tag = "play_feeding_frenzy_button"
    )

    Spacer(modifier = Modifier.height(10.dp))

    GameCardItem(
      title = "🗺️ Animal Rescue",
      desc = "Guide lost baby animals across stepping stones back to the sanctuary gate!",
      reward = "+Sanctuary Rescues",
      icon = "🛟",
      containerColor = MochiBabyBlue.copy(alpha = 0.3f),
      onClick = { onSelectGame(ActiveMiniGame.ANIMAL_RESCUE) },
      tag = "play_animal_rescue_button"
    )

    Spacer(modifier = Modifier.height(20.dp))
  }
}

@Composable
private fun GameCardItem(
  title: String,
  desc: String,
  reward: String,
  icon: String,
  containerColor: Color,
  onClick: () -> Unit,
  tag: String
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag(tag)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(containerColor),
        contentAlignment = Alignment.Center
      ) {
        Text(icon, fontSize = 28.sp)
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
        Spacer(modifier = Modifier.height(2.dp))
        Text(desc, fontSize = 11.sp, color = MochiSoftBrown, lineHeight = 15.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MochiSakuraLight
        ) {
          Text(
            text = reward,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MochiSakuraDeep,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

// ==========================================
// GAME 1: STAR CATCHER
// ==========================================

data class FallingStar(
  val id: Long = System.nanoTime(),
  var xRatio: Float,
  var yRatio: Float,
  val isRainbow: Boolean,
  val isCloudObstacle: Boolean
)

@Composable
fun StarCatcherGame(
  onFinish: (Int) -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier
) {
  var basketXRatio by remember { mutableFloatStateOf(0.5f) }
  var score by remember { mutableIntStateOf(0) }
  var timeLeftSec by remember { mutableIntStateOf(25) }
  var isGameOver by remember { mutableStateOf(false) }

  val stars = remember { mutableStateListOf<FallingStar>() }

  // Game loop
  LaunchedEffect(isGameOver) {
    if (isGameOver) return@LaunchedEffect

    // Spawn and tick loop
    var tickCounter = 0
    while (timeLeftSec > 0) {
      delay(50)
      tickCounter++

      // Decrement timer every 1s (20 ticks of 50ms)
      if (tickCounter % 20 == 0) {
        timeLeftSec--
      }

      // Spawn stars
      if (tickCounter % 14 == 0) {
        val rand = Random.nextFloat()
        val isRainbow = rand < 0.20f
        val isCloud = rand > 0.85f
        stars.add(
          FallingStar(
            xRatio = 0.1f + Random.nextFloat() * 0.8f,
            yRatio = 0f,
            isRainbow = isRainbow,
            isCloudObstacle = isCloud
          )
        )
      }

      // Update positions & check collisions
      val toRemove = mutableListOf<FallingStar>()
      for (star in stars) {
        star.yRatio += 0.025f

        // Check if caught by basket (basket at yRatio ~ 0.85f)
        if (star.yRatio in 0.80f..0.92f && kotlin.math.abs(star.xRatio - basketXRatio) < 0.14f) {
          toRemove.add(star)
          if (star.isCloudObstacle) {
            score = (score - 10).coerceAtLeast(0)
            SoundManager.playMiss()
          } else if (star.isRainbow) {
            score += 25
            SoundManager.playSparkle()
          } else {
            score += 10
            SoundManager.playStarCatch()
          }
        } else if (star.yRatio > 1.0f) {
          toRemove.add(star)
        }
      }
      stars.removeAll(toRemove)
    }

    isGameOver = true
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFF2C3E50), Color(0xFF3498DB), Color(0xFF85C1E9))
        )
      )
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          basketXRatio = (basketXRatio + dragAmount.x / size.width).coerceIn(0.1f, 0.9f)
        }
      }
  ) {
    val screenW = maxWidth
    val screenH = maxHeight

    // Header HUD
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = 0.9f)) {
        Text("⭐ Score: $score", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
      }
      Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = 0.9f)) {
        Text("⏳ $timeLeftSec s", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
      }
      Button(onClick = onExit, colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.8f))) {
        Text("Exit", color = MochiDarkBrown, fontSize = 12.sp)
      }
    }

    // Falling Stars
    stars.forEach { star ->
      val starX = screenW * star.xRatio
      val starY = screenH * star.yRatio
      Box(
        modifier = Modifier
          .offset { IntOffset(starX.roundToPx() - 16.dp.roundToPx(), starY.roundToPx() - 16.dp.roundToPx()) }
      ) {
        Text(
          text = if (star.isCloudObstacle) "⛈️" else if (star.isRainbow) "🌟" else "⭐",
          fontSize = if (star.isRainbow) 30.sp else 24.sp
        )
      }
    }

    // Cloud Basket
    val basketX = screenW * basketXRatio
    val basketY = screenH * 0.86f
    Box(
      modifier = Modifier
        .offset { IntOffset(basketX.roundToPx() - 40.dp.roundToPx(), basketY.roundToPx() - 20.dp.roundToPx()) }
        .size(80.dp, 40.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color.White.copy(alpha = 0.95f))
        .border(2.dp, MochiButterYellow, RoundedCornerShape(20.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text("🧺 Cloud Basket", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
    }

    // Game Over Celebration Card
    if (isGameOver) {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
          .align(Alignment.Center)
          .padding(32.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("🎉 Starlight Collected! 🎉", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MochiDarkBrown)
          Spacer(modifier = Modifier.height(10.dp))
          Text("Final Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MochiSakuraDeep)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Rewards: +${(score * 1.5).toInt()} Coins • +${if (score >= 100) 2 else if (score >= 50) 1 else 0} Stars", fontSize = 13.sp, color = MochiSoftBrown)
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { onFinish(score) },
            colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Claim Rewards", fontSize = 14.sp, color = Color.White)
          }
        }
      }
    }
  }
}

// ==========================================
// GAME 2: MOCHI MEMORY
// ==========================================

data class MemoryCard(
  val id: Int,
  val speciesId: String,
  var isFlipped: Boolean = false,
  var isMatched: Boolean = false
)

@Composable
fun MochiMemoryGame(
  onFinish: (Int) -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val speciesPairIds = remember {
    listOf("rabbit_fluffy", "cat_calico", "dog_golden", "fox_sunset", "capybara_zen", "panda_bamboo")
  }

  val cards = remember {
    val items = mutableListOf<MemoryCard>()
    var idCounter = 0
    for (spId in speciesPairIds) {
      items.add(MemoryCard(id = idCounter++, speciesId = spId))
      items.add(MemoryCard(id = idCounter++, speciesId = spId))
    }
    items.shuffle()
    mutableStateListOf<MemoryCard>().apply { addAll(items) }
  }

  var flippedIndices = remember { mutableStateListOf<Int>() }
  var movesCount by remember { mutableIntStateOf(0) }
  var matchesFound by remember { mutableIntStateOf(0) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MochiSakuraLight)
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("🃏 Moves: $movesCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
      Text("Pairs: $matchesFound / 6", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MochiSakuraDeep)
      Button(onClick = onExit, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
        Text("Exit", color = MochiDarkBrown, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f)
    ) {
      itemsIndexed(cards) { index, card ->
        val sp = SpeciesCatalog.getById(card.speciesId)
        val showFront = card.isFlipped || card.isMatched

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (card.isMatched) MochiMint.copy(alpha = 0.5f) else if (showFront) Color.White else MochiLavender
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .aspectRatio(0.85f)
            .clickable(enabled = !showFront && flippedIndices.size < 2) {
              SoundManager.playPop()
              card.isFlipped = true
              flippedIndices.add(index)

              if (flippedIndices.size == 2) {
                movesCount++
                val firstIdx = flippedIndices[0]
                val secondIdx = flippedIndices[1]
                if (cards[firstIdx].speciesId == cards[secondIdx].speciesId) {
                  cards[firstIdx].isMatched = true
                  cards[secondIdx].isMatched = true
                  matchesFound++
                  SoundManager.playSparkle()
                  flippedIndices.clear()
                }
              }
            }
        ) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (showFront) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MochiAnimalView(species = sp, sizeDp = 50.dp)
                Text(sp.name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
              }
            } else {
              Text("🌸", fontSize = 28.sp)
            }
          }
        }
      }
    }

    // Flip reset trigger
    LaunchedEffect(flippedIndices.size) {
      if (flippedIndices.size == 2) {
        val firstIdx = flippedIndices[0]
        val secondIdx = flippedIndices[1]
        if (cards[firstIdx].speciesId != cards[secondIdx].speciesId) {
          delay(800)
          cards[firstIdx].isFlipped = false
          cards[secondIdx].isFlipped = false
          flippedIndices.clear()
        }
      }
    }

    if (matchesFound >= 6) {
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = { onFinish(100 - (movesCount * 2).coerceAtMost(50)) },
        colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("All Pairs Matched! Claim Rewards 🎉", fontSize = 13.sp, color = Color.White)
      }
    }
  }
}

// ==========================================
// GAME 3: FEEDING FRENZY
// ==========================================

data class FoodOrder(
  val animalSpeciesId: String,
  val requiredFood: String,
  val foodEmoji: String
)

@Composable
fun FeedingFrenzyGame(
  onFinish: (Int) -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier
) {
  var score by remember { mutableIntStateOf(0) }
  var timeLeft by remember { mutableIntStateOf(20) }
  var isOver by remember { mutableStateOf(false) }

  val foodOptions = listOf(
    Triple("Bamboo", "🎋", "panda_bamboo"),
    Triple("Fish", "🐟", "otter_river"),
    Triple("Carrot", "🥕", "rabbit_fluffy"),
    Triple("Honey", "🍯", "bear_honey")
  )

  var currentOrder by remember {
    mutableStateOf(
      FoodOrder("panda_bamboo", "Bamboo", "🎋")
    )
  }

  LaunchedEffect(isOver) {
    if (isOver) return@LaunchedEffect
    while (timeLeft > 0) {
      delay(1000)
      timeLeft--
    }
    isOver = true
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MochiMint.copy(alpha = 0.3f))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Score: $score", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
      Text("⏳ $timeLeft s", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
      Button(onClick = onExit, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
        Text("Exit", color = MochiDarkBrown, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Animal dining preview
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val customer = SpeciesCatalog.getById(currentOrder.animalSpeciesId)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MochiButterYellow.copy(alpha = 0.5f)
        ) {
          Text(
            text = "Craving: ${currentOrder.foodEmoji} ${currentOrder.requiredFood}!",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MochiDarkBrown,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        MochiAnimalView(species = customer, sizeDp = 120.dp, isExcited = true)
        Spacer(modifier = Modifier.height(6.dp))
        Text(customer.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Food Dispenser Buttons
    Text("Tap the matching treat:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      foodOptions.forEach { (name, emoji, spId) ->
        Button(
          onClick = {
            if (name == currentOrder.requiredFood) {
              score += 10
              SoundManager.playEatMunch()
              val nextOption = foodOptions.random()
              currentOrder = FoodOrder(nextOption.third, nextOption.first, nextOption.second)
            } else {
              SoundManager.playMiss()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.weight(1f).height(65.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 22.sp)
            Text(name, fontSize = 10.sp, color = MochiDarkBrown, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (isOver) {
      Button(
        onClick = { onFinish(score) },
        colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("Times Up! Claim $score Points 🎉", fontSize = 13.sp, color = Color.White)
      }
    }
  }
}

// ==========================================
// GAME 4: ANIMAL RESCUE ADVENTURE
// ==========================================

@Composable
fun AnimalRescueAdventureGame(
  onFinish: (Int) -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier
) {
  // 5x5 grid
  var playerX by remember { mutableIntStateOf(0) }
  var playerY by remember { mutableIntStateOf(0) }
  var blanketsCollected by remember { mutableIntStateOf(0) }
  var hasWon by remember { mutableStateOf(false) }

  val targetX = 4
  val targetY = 4
  val blanketCoords = listOf(Pair(1, 2), Pair(3, 1))
  val thornCoords = listOf(Pair(2, 2), Pair(3, 3), Pair(1, 3))

  val lostSpecies = SpeciesCatalog.getById("puppy_corgi")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MochiBabyBlue.copy(alpha = 0.35f))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Blankets: $blanketsCollected / 2 🧣", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
      Button(onClick = onExit, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
        Text("Exit", color = MochiDarkBrown, fontSize = 12.sp)
      }
    }

    Text("Guide the lost pup safely to the Sanctuary Gate! 🏡", fontSize = 11.sp, color = MochiSoftBrown)

    Spacer(modifier = Modifier.height(14.dp))

    // 5x5 Grid
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier.size(280.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        for (y in 0..4) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            for (x in 0..4) {
              val isPlayer = x == playerX && y == playerY
              val isTarget = x == targetX && y == targetY
              val isBlanket = blanketCoords.contains(Pair(x, y)) && blanketsCollected < 2
              val isThorn = thornCoords.contains(Pair(x, y))

              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    when {
                      isPlayer -> MochiSakuraLight
                      isTarget -> MochiMint.copy(alpha = 0.5f)
                      isThorn -> Color(0xFFFFCDD2)
                      else -> Color(0xFFF5F5F5)
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                when {
                  isPlayer -> MochiAnimalView(species = lostSpecies, sizeDp = 38.dp)
                  isTarget -> Text("🏡", fontSize = 20.sp)
                  isBlanket -> Text("🧣", fontSize = 18.sp)
                  isThorn -> Text("🌵", fontSize = 16.sp)
                  else -> Text("•", fontSize = 14.sp, color = Color.LightGray)
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Directional Pad
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Button(
        onClick = {
          if (playerY > 0 && !thornCoords.contains(Pair(playerX, playerY - 1))) {
            playerY--
            SoundManager.playPop()
          }
        },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = Modifier.size(50.dp)
      ) {
        Text("⬆️", fontSize = 18.sp)
      }
      Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Button(
          onClick = {
            if (playerX > 0 && !thornCoords.contains(Pair(playerX - 1, playerY))) {
              playerX--
              SoundManager.playPop()
            }
          },
          shape = CircleShape,
          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
          modifier = Modifier.size(50.dp)
        ) {
          Text("⬅️", fontSize = 18.sp)
        }
        Button(
          onClick = {
            if (playerX < 4 && !thornCoords.contains(Pair(playerX + 1, playerY))) {
              playerX++
              SoundManager.playPop()
            }
          },
          shape = CircleShape,
          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
          modifier = Modifier.size(50.dp)
        ) {
          Text("➡️", fontSize = 18.sp)
        }
      }
      Button(
        onClick = {
          if (playerY < 4 && !thornCoords.contains(Pair(playerX, playerY + 1))) {
            playerY++
            SoundManager.playPop()
          }
        },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = Modifier.size(50.dp)
      ) {
        Text("⬇️", fontSize = 18.sp)
      }
    }

    // Check blanket pickup
    LaunchedEffect(playerX, playerY) {
      if (blanketCoords.contains(Pair(playerX, playerY))) {
        blanketsCollected = (blanketsCollected + 1).coerceAtMost(2)
        SoundManager.playSparkle()
      }
      if (playerX == targetX && playerY == targetY) {
        hasWon = true
        SoundManager.playLevelUp()
      }
    }

    if (hasWon) {
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = { onFinish(150) },
        colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("Rescue Complete! Welcome Home 🎉", fontSize = 13.sp, color = Color.White)
      }
    }
  }
}
