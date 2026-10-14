package com.example.ui.care

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.AdoptedAnimal
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

@Composable
fun AnimalCareScreen(
  adoptedAnimals: List<AdoptedAnimal>,
  activeAnimalId: String,
  onSelectActiveAnimal: (String) -> Unit,
  onPet: (String) -> Unit,
  onFeed: (String, String) -> Unit,
  onBath: (String) -> Unit,
  onBrush: (String) -> Unit,
  onToggleSleep: (String) -> Unit,
  onRename: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val activePet = adoptedAnimals.firstOrNull { it.id == activeAnimalId } ?: adoptedAnimals.firstOrNull()

  var showFoodPicker by remember { mutableStateOf(false) }
  var showRenameDialog by remember { mutableStateOf(false) }
  var renameInput by remember { mutableStateOf("") }
  var hasBathBubbles by remember { mutableStateOf(false) }
  var actionStatusText by remember { mutableStateOf<String?>(null) }

  val snacks = listOf(
    Pair("🍓 Sweet Strawberry", "Strawberry"),
    Pair("🧋 Boba Milk Tea", "Boba"),
    Pair("🍯 Golden Honey Pot", "Honey"),
    Pair("🐟 Fresh Salmon Treat", "Fish"),
    Pair("🎋 Crisp Bamboo Shoot", "Bamboo"),
    Pair("🥕 Baby Sweet Carrot", "Carrot")
  )

  if (activePet == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No animals adopted yet! Head to Rescue to meet your first friend.", color = MochiDarkBrown)
    }
    return
  }

  val species = SpeciesCatalog.getById(activePet.speciesId)

  // Subtle floating hearts / zzz
  val transition = rememberInfiniteTransition(label = "care_fx")
  val floatAnim by transition.animateFloat(
    initialValue = 0f,
    targetValue = -18f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200),
      repeatMode = RepeatMode.Reverse
    ),
    label = "float"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(MochiSakuraLight, MochiLavender.copy(alpha = 0.4f), Color.White)
        )
      )
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top pet selector carousel if multiple
    if (adoptedAnimals.size > 1) {
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(adoptedAnimals) { pet ->
          val petSpecies = SpeciesCatalog.getById(pet.speciesId)
          val isSelected = pet.id == activePet.id
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MochiSakuraDeep else Color.White.copy(alpha = 0.9f),
            shadowElevation = if (isSelected) 3.dp else 1.dp,
            modifier = Modifier
              .clickable {
                SoundManager.playPop()
                onSelectActiveAnimal(pet.id)
              }
              .testTag("pet_carousel_${pet.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                MochiAnimalView(species = petSpecies, sizeDp = 22.dp)
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = pet.customName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MochiDarkBrown
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Main Pet Card with Large Interactive Mochi Pet
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Pet Name and species category
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = activePet.customName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MochiDarkBrown
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = CircleShape,
            color = MochiSakuraLight,
            modifier = Modifier.clickable {
              renameInput = activePet.customName
              showRenameDialog = true
            }
          ) {
            Text(
              text = "✏️",
              fontSize = 12.sp,
              modifier = Modifier.padding(5.dp)
            )
          }
        }

        Text(
          text = "${species.name} • ${species.personality}",
          fontSize = 11.sp,
          color = MochiSoftBrown
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Large Interactive Pet View
        Box(
          modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(MochiSakuraLight, Color.White)
              )
            )
            .clickable {
              onPet(activePet.id)
              actionStatusText = "Purr... Loving your gentle pets! 💖"
            }
            .testTag("interactive_pet_canvas"),
          contentAlignment = Alignment.Center
        ) {
          MochiAnimalView(
            species = species,
            sizeDp = 140.dp,
            equippedHat = activePet.equippedHat,
            equippedAccessory = activePet.equippedAccessory,
            isSleeping = activePet.isSleeping,
            isExcited = true
          )

          // Floating ZZZ if sleeping
          if (activePet.isSleeping) {
            Text(
              text = "💤",
              fontSize = 24.sp,
              modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-12).dp, y = (10 + floatAnim.toInt()).dp)
            )
          }

          // Soap Bubbles overlay
          if (hasBathBubbles) {
            Text(
              text = "🫧🫧🫧",
              fontSize = 22.sp,
              modifier = Modifier.align(Alignment.Center)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mood Status pill
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MochiSakuraPink.copy(alpha = 0.2f)
        ) {
          Text(
            text = actionStatusText ?: "Mood: ${activePet.moodDescription}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MochiSakuraDeep,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            textAlign = TextAlign.Center
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4 Care Stat Meters
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CareMeterRow("🥕 Hunger", activePet.hunger, Color(0xFFFF9800), "Feed favorite snacks")
        CareMeterRow("💖 Happiness", activePet.happiness, MochiSakuraDeep, "Pet, brush, play")
        CareMeterRow("🫧 Cleanliness", activePet.cleanliness, Color(0xFF29B6F6), "Warm bubble bath")
        CareMeterRow("⚡ Energy", activePet.energy, Color(0xFFAB47BC), "Tuck in for naps")
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Care Actions Bar (Grid of 6 cute buttons)
    Text(
      text = "Care & Play Interactions",
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = MochiDarkBrown
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      CareActionButton(
        icon = "🍓",
        label = "Feed",
        onClick = { showFoodPicker = !showFoodPicker },
        modifier = Modifier.weight(1f),
        tag = "care_feed_button"
      )
      CareActionButton(
        icon = "🫧",
        label = if (hasBathBubbles) "Rinse" else "Bath",
        onClick = {
          if (!hasBathBubbles) {
            hasBathBubbles = true
            SoundManager.playCleanBubble()
            actionStatusText = "Lathering soft fluffy bubbles! 🫧"
          } else {
            hasBathBubbles = false
            onBath(activePet.id)
            actionStatusText = "Sparkling clean and squeaky soft! ✨"
          }
        },
        modifier = Modifier.weight(1f),
        tag = "care_bath_button"
      )
      CareActionButton(
        icon = "🪮",
        label = "Brush",
        onClick = {
          onBrush(activePet.id)
          actionStatusText = "Brushing coat until it shines! ✨"
        },
        modifier = Modifier.weight(1f),
        tag = "care_brush_button"
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      CareActionButton(
        icon = "💖",
        label = "Pet",
        onClick = {
          onPet(activePet.id)
          actionStatusText = "Gentle loving strokes... Purr! 💖"
        },
        modifier = Modifier.weight(1f),
        tag = "care_pet_button"
      )
      CareActionButton(
        icon = if (activePet.isSleeping) "☀️" else "🌙",
        label = if (activePet.isSleeping) "Wake" else "Nap",
        onClick = {
          onToggleSleep(activePet.id)
          actionStatusText = if (!activePet.isSleeping) "Tucked in warmly for sweet dreams... 💤" else "Good morning sunshine! ☀️"
        },
        modifier = Modifier.weight(1f),
        tag = "care_sleep_button"
      )
      CareActionButton(
        icon = "🏷️",
        label = "Rename",
        onClick = {
          renameInput = activePet.customName
          showRenameDialog = true
        },
        modifier = Modifier.weight(1f),
        tag = "care_rename_button"
      )
    }

    // Snack Picker Tray
    AnimatedVisibility(visible = showFoodPicker) {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Select a Yummy Treat:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MochiDarkBrown
          )
          Spacer(modifier = Modifier.height(8.dp))
          snacks.chunked(2).forEach { rowSnacks ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowSnacks.forEach { (label, name) ->
                Button(
                  onClick = {
                    onFeed(activePet.id, name)
                    actionStatusText = "Yum! ${activePet.customName} devoured the $name! 🍓"
                    showFoodPicker = false
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraLight),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                ) {
                  Text(label, fontSize = 11.sp, color = MochiDarkBrown, maxLines = 1)
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }

  // Rename Dialog
  if (showRenameDialog) {
    AlertDialog(
      onDismissRequest = { showRenameDialog = false },
      title = { Text("Rename ${activePet.customName}", fontWeight = FontWeight.Bold) },
      text = {
        OutlinedTextField(
          value = renameInput,
          onValueChange = { renameInput = it },
          label = { Text("New pet name") },
          singleLine = true,
          shape = RoundedCornerShape(14.dp)
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onRename(activePet.id, renameInput)
            showRenameDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep)
        ) {
          Text("Save Name")
        }
      },
      dismissButton = {
        TextButton(onClick = { showRenameDialog = false }) {
          Text("Cancel", color = MochiSoftBrown)
        }
      }
    )
  }
}

@Composable
private fun CareMeterRow(
  label: String,
  value: Int,
  fillColor: Color,
  tooltip: String
) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MochiDarkBrown)
      Text("$value%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fillColor)
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { value / 100f },
      modifier = Modifier
        .fillMaxWidth()
        .height(7.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = fillColor,
      trackColor = fillColor.copy(alpha = 0.2f)
    )
  }
}

@Composable
private fun CareActionButton(
  icon: String,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  tag: String
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .clickable(onClick = onClick)
      .testTag(tag)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(icon, fontSize = 24.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MochiDarkBrown
      )
    }
  }
}
