package com.example.ui.sanctuary

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.DecorationCatalog
import com.example.data.model.GameState
import com.example.data.model.HabitatCatalog
import com.example.data.model.HabitatZone
import com.example.data.model.PlacedDecoration
import com.example.data.model.SpeciesCatalog
import com.example.ui.art.MochiAnimalView
import com.example.ui.theme.MochiDarkBrown
import com.example.ui.theme.MochiSakuraDeep
import com.example.ui.theme.MochiSakuraLight
import com.example.ui.theme.MochiSakuraPink
import com.example.ui.theme.MochiSoftBrown
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun SanctuaryScreen(
  gameState: GameState,
  isEditMode: Boolean,
  onSwitchZone: (String) -> Unit,
  onPlaceDecoration: (String, Float, Float) -> Unit,
  onMoveDecoration: (String, Float, Float) -> Unit,
  onRotateDecoration: (String) -> Unit,
  onRemoveDecoration: (String) -> Unit,
  onPetAnimal: (String) -> Unit,
  onSelectAnimalForCare: (String) -> Unit,
  onToggleEditMode: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentZone = HabitatCatalog.getById(gameState.activeZoneId)
  val zoneAnimals = gameState.adoptedAnimals.filter { it.assignedZoneId == currentZone.id }
    .ifEmpty { gameState.adoptedAnimals.take(3) }
  val zoneDecorations = gameState.placedDecorations.filter { it.zoneId == currentZone.id }

  var selectedPlacedItem by remember { mutableStateOf<PlacedDecoration?>(null) }
  var animalSpeechBubbleId by remember { mutableStateOf<String?>(null) }

  val transition = rememberInfiniteTransition(label = "sanctuary_ambient")
  val cloudOffset by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1000f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 40000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "clouds"
  )

  BoxWithConstraints(
    modifier = modifier.fillMaxSize()
  ) {
    val totalW = maxWidth
    val totalH = maxHeight

    // 1. Habitat Background Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Sky gradient
      val skyBrush = Brush.verticalGradient(
        colors = listOf(
          Color(currentZone.skyGradientTop),
          Color(currentZone.skyGradientBottom)
        ),
        startY = 0f,
        endY = h * 0.65f
      )
      drawRect(brush = skyBrush, size = Size(w, h * 0.65f))

      // Clouds
      val cloudX = (cloudOffset % (w + 200f)) - 100f
      drawCircle(Color.White.copy(alpha = 0.65f), 45f, Offset(cloudX, h * 0.16f))
      drawCircle(Color.White.copy(alpha = 0.65f), 55f, Offset(cloudX + 45f, h * 0.14f))
      drawCircle(Color.White.copy(alpha = 0.65f), 40f, Offset(cloudX + 90f, h * 0.16f))

      val cloudX2 = ((cloudOffset * 0.6f + 300f) % (w + 240f)) - 120f
      drawCircle(Color.White.copy(alpha = 0.5f), 35f, Offset(cloudX2, h * 0.28f))
      drawCircle(Color.White.copy(alpha = 0.5f), 45f, Offset(cloudX2 + 40f, h * 0.26f))
      drawCircle(Color.White.copy(alpha = 0.5f), 35f, Offset(cloudX2 + 80f, h * 0.28f))

      // Rolling Hills / Ground Path
      val groundTopY = h * 0.52f
      val groundColor = Color(currentZone.groundColor)
      val groundPath = Path().apply {
        moveTo(0f, groundTopY)
        quadraticBezierTo(w * 0.35f, groundTopY - 25f, w * 0.70f, groundTopY + 10f)
        quadraticBezierTo(w * 0.85f, groundTopY + 25f, w, groundTopY)
        lineTo(w, h)
        lineTo(0f, h)
        close()
      }
      drawPath(groundPath, groundColor)

      // Foreground lush meadow layer
      val forePath = Path().apply {
        moveTo(0f, groundTopY + 60f)
        quadraticBezierTo(w * 0.45f, groundTopY + 90f, w, groundTopY + 50f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
      }
      drawPath(forePath, groundColor.copy(alpha = 0.85f))

      // Tiny flowers / sparkles in meadow
      for (i in 0..12) {
        val fx = (w * (i * 0.08f + 0.04f)) % w
        val fy = groundTopY + 30f + ((i * 37) % (h * 0.4f).toInt())
        val fCol = if (i % 2 == 0) Color(0xFFFF80AB) else Color(0xFFFFF59D)
        drawCircle(fCol, 3.5f, Offset(fx, fy))
        drawCircle(Color.White, 1.5f, Offset(fx, fy))
      }
    }

    // 2. Placed Decorations
    zoneDecorations.forEach { decor ->
      val itemType = DecorationCatalog.getById(decor.itemTypeId)
      val posX = totalW * decor.xRatio
      val posY = totalH * decor.yRatio
      val isSelected = selectedPlacedItem?.id == decor.id

      Box(
        modifier = Modifier
          .offset { IntOffset(posX.roundToPx() - 30.dp.roundToPx(), posY.roundToPx() - 30.dp.roundToPx()) }
          .size(60.dp)
          .rotate(decor.rotation)
          .then(
            if (isEditMode) {
              Modifier.pointerInput(decor.id) {
                detectDragGestures { change, dragAmount ->
                  change.consume()
                  val newX = (posX + dragAmount.x.toDp()) / totalW
                  val newY = (posY + dragAmount.y.toDp()) / totalH
                  onMoveDecoration(decor.id, newX, newY)
                }
              }
            } else Modifier
          )
          .clickable {
            if (isEditMode) {
              selectedPlacedItem = if (isSelected) null else decor
            }
          },
        contentAlignment = Alignment.Center
      ) {
        // Outline if selected in edit mode
        if (isSelected && isEditMode) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .border(2.dp, MochiSakuraDeep, RoundedCornerShape(12.dp))
          )
        }
        Text(
          text = itemType.iconEmoji,
          fontSize = 36.sp
        )
      }
    }

    // 3. Roaming Animals
    zoneAnimals.forEachIndexed { index, pet ->
      val species = SpeciesCatalog.getById(pet.speciesId)
      // Slight offset for multiple pets
      val basePosX = (pet.posXRatio + (index * 0.12f) - 0.06f).coerceIn(0.15f, 0.85f)
      val basePosY = (pet.posYRatio + (index * 0.06f) - 0.03f).coerceIn(0.55f, 0.80f)

      val animalX = totalW * basePosX
      val animalY = totalH * basePosY

      Box(
        modifier = Modifier
          .offset { IntOffset(animalX.roundToPx() - 40.dp.roundToPx(), animalY.roundToPx() - 40.dp.roundToPx()) }
          .clickable {
            onPetAnimal(pet.id)
            animalSpeechBubbleId = pet.id
          }
          .testTag("sanctuary_pet_${pet.id}"),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          // Pet Name & Heart Pill
          Surface(
            color = Color.White.copy(alpha = 0.90f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(bottom = 2.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = pet.customName,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MochiDarkBrown
              )
              if (pet.averageCare >= 80) {
                Spacer(modifier = Modifier.width(2.dp))
                Text("💖", fontSize = 8.sp)
              }
            }
          }

          MochiAnimalView(
            species = species,
            sizeDp = 80.dp,
            equippedHat = pet.equippedHat,
            equippedAccessory = pet.equippedAccessory,
            isSleeping = pet.isSleeping,
            isExcited = animalSpeechBubbleId == pet.id
          )
        }
      }
    }

    // 4. Quick Pet Interaction Bubble Modal
    animalSpeechBubbleId?.let { petId ->
      val targetPet = gameState.adoptedAnimals.firstOrNull { it.id == petId }
      if (targetPet != null) {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier
            .align(Alignment.Center)
            .padding(24.dp)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "💖 ${targetPet.customName} is happy to see you!",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MochiDarkBrown
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = targetPet.moodDescription,
              fontSize = 12.sp,
              color = MochiSoftBrown
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = {
                  onPetAnimal(targetPet.id)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraLight),
                shape = RoundedCornerShape(14.dp)
              ) {
                Text("Pet Pet 💖", color = MochiDarkBrown, fontSize = 12.sp)
              }
              Button(
                onClick = {
                  animalSpeechBubbleId = null
                  onSelectAnimalForCare(targetPet.id)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
                shape = RoundedCornerShape(14.dp)
              ) {
                Text("Full Care 🐾", color = Color.White, fontSize = 12.sp)
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Close",
              fontSize = 11.sp,
              color = MochiSoftBrown,
              modifier = Modifier.clickable { animalSpeechBubbleId = null }
            )
          }
        }
      }
    }

    // 5. Zone Switcher Bar (Top)
    Row(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 8.dp)
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      HabitatCatalog.zones.forEach { zone ->
        val isUnlocked = gameState.sanctuaryLevel >= zone.unlockLevel
        val isSelected = zone.id == currentZone.id

        Surface(
          color = when {
            isSelected -> MochiSakuraDeep
            isUnlocked -> Color.White.copy(alpha = 0.90f)
            else -> Color.White.copy(alpha = 0.50f)
          },
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier
            .clickable(enabled = isUnlocked) {
              onSwitchZone(zone.id)
            }
            .testTag("zone_tab_${zone.id}")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(zone.iconEmoji, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isUnlocked) zone.name else "Lv${zone.unlockLevel} Lock",
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = when {
                isSelected -> Color.White
                isUnlocked -> MochiDarkBrown
                else -> MochiSoftBrown
              }
            )
          }
        }
      }
    }

    // 6. Selected Placed Item Toolbar (When in Edit Mode)
    if (isEditMode && selectedPlacedItem != null) {
      val item = selectedPlacedItem!!
      Surface(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 56.dp),
        color = Color.White.copy(alpha = 0.96f),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text("Selected Decor: ", fontSize = 11.sp, color = MochiDarkBrown)
          IconButton(
            onClick = { onRotateDecoration(item.id) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Rotate", tint = MochiDarkBrown)
          }
          IconButton(
            onClick = {
              onRemoveDecoration(item.id)
              selectedPlacedItem = null
            },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFE53935))
          }
        }
      }
    }

    // 7. Edit Mode Bottom Tray: Inventory Palette
    if (isEditMode) {
      Surface(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth(),
        color = Color.White.copy(alpha = 0.96f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 8.dp
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Sanctuary Decor Inventory",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MochiDarkBrown
            )
            Button(
              onClick = onToggleEditMode,
              colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text("Done", fontSize = 11.sp, color = Color.White)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(gameState.ownedDecorationIds) { itemId ->
              val decorItem = DecorationCatalog.getById(itemId)
              Card(
                colors = CardDefaults.cardColors(containerColor = MochiSakuraLight),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .clickable {
                    onPlaceDecoration(itemId, 0.5f, 0.65f)
                  }
                  .padding(2.dp)
              ) {
                Column(
                  modifier = Modifier.padding(10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(decorItem.iconEmoji, fontSize = 28.sp)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = decorItem.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MochiDarkBrown
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
