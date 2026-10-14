package com.example.ui.rescue

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.AnimalRarity
import com.example.data.model.AnimalSpecies
import com.example.data.model.GameState
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

data class RescueRegion(
  val id: String,
  val name: String,
  val minLevel: Int,
  val costCoins: Int,
  val iconEmoji: String,
  val possibleSpeciesIds: List<String>,
  val description: String
)

@Composable
fun RescueScreen(
  gameState: GameState,
  onRescueAnimal: (speciesId: String, name: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val regions = listOf(
    RescueRegion(
      "reg_meadow",
      "Sunny Clover Valley",
      1,
      100,
      "🌸",
      listOf("rabbit_fluffy", "kitten_tabby", "dog_golden", "duck_butter", "sheep_cotton"),
      "Breezy hills where playful puppies and kittens roll in clover."
    ),
    RescueRegion(
      "reg_woods",
      "Whispering Woods",
      2,
      200,
      "🌲",
      listOf("hamster_cheeky", "guinea_toffee", "hedgehog_spiky", "squirrel_acorn", "fox_sunset"),
      "Shady trees where quiet woodland friends forage for seeds."
    ),
    RescueRegion(
      "reg_lakeside",
      "Peaceful Water Lily Lake",
      3,
      350,
      "🌊",
      listOf("capybara_zen", "otter_river", "swan_graceful", "turtle_mossy"),
      "Calm misty waters where otters float and capybaras meditate."
    ),
    RescueRegion(
      "reg_snow",
      "Frosty Aurora Peak",
      4,
      500,
      "❄️",
      listOf("seal_marshmallow", "penguin_tuxedo", "goat_mountain", "bear_honey"),
      "Chilly mountain slopes where fluffy seals slide on snow."
    ),
    RescueRegion(
      "reg_bamboo",
      "Tropical Sanctuary Glade",
      5,
      700,
      "🎋",
      listOf("panda_bamboo", "koala_eucalyptus", "elephant_peanut", "cow_strawberry"),
      "Lush sweet gardens with towering bamboo and fragrant blossoms."
    ),
    RescueRegion(
      "reg_celestial",
      "Starlight Celestial Haven",
      7,
      1200,
      "🌙",
      listOf("moon_kitten", "star_fox", "pastel_bunny_magic"),
      "Mythical floating peak touched by stardust and comet auras."
    )
  )

  var foundAnimal by remember { mutableStateOf<AnimalSpecies?>(null) }
  var petNickname by remember { mutableStateOf("") }
  var isSearching by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(MochiLavender.copy(alpha = 0.4f), MochiSakuraLight, Color.White)
        )
      )
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Text(
      text = "🗺️ Rescue Expeditions",
      fontSize = 20.sp,
      fontWeight = FontWeight.Black,
      color = MochiDarkBrown
    )
    Text(
      text = "Explore the valleys, rescue lost animals, and welcome them to Mochi World!",
      fontSize = 12.sp,
      color = MochiSoftBrown
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Active Rescue Result Modal
    foundAnimal?.let { species ->
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(2.dp, MochiSakuraDeep, RoundedCornerShape(24.dp))
          .padding(bottom = 16.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MochiSakuraLight
          ) {
            Text(
              text = "🐾 ANIMAL FOUND! 🐾",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = MochiSakuraDeep,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Box(
            modifier = Modifier
              .size(120.dp)
              .clip(CircleShape)
              .background(MochiSakuraLight),
            contentAlignment = Alignment.Center
          ) {
            MochiAnimalView(species = species, sizeDp = 105.dp, isExcited = true)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(species.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
          Text(
            text = species.bio,
            fontSize = 11.sp,
            color = MochiSoftBrown,
            modifier = Modifier.padding(horizontal = 10.dp),
            lineHeight = 15.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = petNickname,
            onValueChange = { petNickname = it },
            label = { Text("Name this sweet rescue:") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().testTag("rescue_nickname_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                onRescueAnimal(species.id, if (petNickname.isBlank()) species.name else petNickname)
                foundAnimal = null
                petNickname = ""
              },
              colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.weight(1f).height(44.dp).testTag("adopt_rescue_button")
            ) {
              Text("Adopt to Sanctuary 🏡", fontSize = 12.sp, color = Color.White)
            }
          }
        }
      }
    }

    // List of Regions to Explore
    regions.forEach { region ->
      val isUnlocked = gameState.sanctuaryLevel >= region.minLevel
      val canAfford = gameState.playerCoins >= region.costCoins

      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 10.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(region.iconEmoji, fontSize = 26.sp)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(region.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
                Text(
                  if (isUnlocked) "Cost: 🪙 ${region.costCoins}" else "Unlocks at Sanctuary Lv ${region.minLevel}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isUnlocked) MochiSakuraDeep else MochiSoftBrown
                )
              }
            }

            Button(
              onClick = {
                if (isUnlocked && canAfford) {
                  SoundManager.playSparkle()
                  val chosenSpeciesId = region.possibleSpeciesIds.random()
                  foundAnimal = SpeciesCatalog.getById(chosenSpeciesId)
                  petNickname = foundAnimal!!.name
                } else {
                  SoundManager.playMiss()
                }
              },
              enabled = isUnlocked && canAfford,
              colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.height(36.dp).testTag("explore_region_${region.id}")
            ) {
              Text(
                if (isUnlocked) "Explore 🔎" else "Locked",
                fontSize = 11.sp,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(region.description, fontSize = 11.sp, color = MochiSoftBrown, lineHeight = 15.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
