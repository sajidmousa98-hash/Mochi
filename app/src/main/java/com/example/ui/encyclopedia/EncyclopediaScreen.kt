package com.example.ui.encyclopedia

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.AnimalCategory
import com.example.data.model.AnimalRarity
import com.example.data.model.AnimalSpecies
import com.example.data.model.GameState
import com.example.data.model.SpeciesCatalog
import com.example.ui.art.MochiAnimalView
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
fun EncyclopediaScreen(
  gameState: GameState,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf<AnimalCategory?>(null) }
  var selectedRarity by remember { mutableStateOf<AnimalRarity?>(null) }
  var inspectingSpecies by remember { mutableStateOf<AnimalSpecies?>(null) }

  val all = SpeciesCatalog.allSpecies
  val adoptedSpeciesIds = gameState.adoptedAnimals.map { it.speciesId }.toSet()
  val discoveredIds = gameState.encyclopediaDiscoveredIds + adoptedSpeciesIds

  val filteredSpecies = all.filter { sp ->
    (selectedCategory == null || sp.category == selectedCategory) &&
    (selectedRarity == null || sp.rarity == selectedRarity)
  }

  val totalDiscovered = discoveredIds.size
  val totalSpecies = all.size
  val percent = ((totalDiscovered.toFloat() / totalSpecies.toFloat()) * 100).toInt()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(MochiSakuraLight, MochiLavender.copy(alpha = 0.35f), Color.White)
        )
      )
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Header & Collection Progress
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text("📖 Mochi-Dex", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MochiDarkBrown)
        Text("Animal Sanctuary Encyclopedia", fontSize = 11.sp, color = MochiSoftBrown)
      }
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 2.dp
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalAlignment = Alignment.End
        ) {
          Text(
            text = "$totalDiscovered / $totalSpecies ($percent%)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MochiSakuraDeep
          )
          LinearProgressIndicator(
            progress = { totalDiscovered.toFloat() / totalSpecies.toFloat() },
            modifier = Modifier
              .width(70.dp)
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = MochiSakuraDeep,
            trackColor = MochiSakuraLight
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Category Filter Chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      item {
        CategoryChip("All", selectedCategory == null) { selectedCategory = null }
      }
      items(AnimalCategory.values()) { cat ->
        CategoryChip(cat.label, selectedCategory == cat) { selectedCategory = cat }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 30 Species Grid
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.weight(1f)
    ) {
      items(filteredSpecies) { species ->
        val isDiscovered = discoveredIds.contains(species.id)

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDiscovered) Color.White else Color.White.copy(alpha = 0.6f)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = if (isDiscovered) 2.dp else 0.dp),
          modifier = Modifier
            .aspectRatio(0.85f)
            .clickable(enabled = isDiscovered) {
              SoundManager.playPop()
              inspectingSpecies = species
            }
            .testTag("encyclopedia_card_${species.id}")
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            if (isDiscovered) {
              Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                MochiAnimalView(species = species, sizeDp = 48.dp)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = species.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MochiDarkBrown,
                maxLines = 1,
                textAlign = TextAlign.Center
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(species.rarity.badgeColor).copy(alpha = 0.2f)
              ) {
                Text(
                  text = species.rarity.label,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(species.rarity.badgeColor),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            } else {
              Text("🔒", fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("???", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MochiSoftBrown)
              Text("Lv${species.unlockLevel}", fontSize = 9.sp, color = MochiSoftBrown)
            }
          }
        }
      }
    }
  }

  // Educational Detail Modal
  inspectingSpecies?.let { sp ->
    AlertDialog(
      onDismissRequest = { inspectingSpecies = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(sp.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(sp.rarity.badgeColor).copy(alpha = 0.25f)
          ) {
            Text(
              text = sp.rarity.label,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(sp.rarity.badgeColor),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      },
      text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(110.dp)
              .clip(CircleShape)
              .background(MochiSakuraLight),
            contentAlignment = Alignment.Center
          ) {
            MochiAnimalView(species = sp, sizeDp = 95.dp, isExcited = true)
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = sp.bio,
            fontSize = 12.sp,
            color = MochiDarkBrown,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MochiSakuraLight,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("✨ Personality: ${sp.personality}", fontSize = 11.sp, color = MochiDarkBrown)
              Text("🍓 Favorite Food: ${sp.favoriteFood}", fontSize = 11.sp, color = MochiDarkBrown)
              Text("🏡 Preferred Home: ${sp.habitatPreference}", fontSize = 11.sp, color = MochiDarkBrown)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { inspectingSpecies = null },
          colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep)
        ) {
          Text("Close", color = Color.White)
        }
      }
    )
  }
}

@Composable
private fun CategoryChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isSelected) MochiSakuraDeep else Color.White,
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = if (isSelected) Color.White else MochiDarkBrown,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
    )
  }
}
