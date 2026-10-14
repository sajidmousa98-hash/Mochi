package com.example.ui.intro

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.SpeciesCatalog
import com.example.ui.art.MochiAnimalView
import com.example.ui.theme.MochiBabyBlue
import com.example.ui.theme.MochiButterYellow
import com.example.ui.theme.MochiDarkBrown
import com.example.ui.theme.MochiLavender
import com.example.ui.theme.MochiSakuraDeep
import com.example.ui.theme.MochiSakuraLight
import com.example.ui.theme.MochiSakuraPink
import com.example.ui.theme.MochiSoftBrown

@Composable
fun IntroScreen(
  onAdoptFirst: (speciesId: String, name: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val starterSpeciesIds = listOf("rabbit_fluffy", "cat_calico", "dog_golden")
  var selectedSpeciesId by remember { mutableStateOf("rabbit_fluffy") }
  var petName by remember { mutableStateOf("Mochi") }

  val nameSuggestions = listOf("Mochi", "Boba", "Pudding", "Peaches", "Clover", "Sunny", "Cookie", "Snowball")

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(MochiSakuraLight, MochiLavender.copy(alpha = 0.5f), MochiBabyBlue.copy(alpha = 0.4f))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Glowing title badge
      Surface(
        color = MochiSakuraPink.copy(alpha = 0.25f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(bottom = 8.dp)
      ) {
        Text(
          text = "🌸 WELCOME TO 🌸",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
          color = MochiSakuraDeep,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
      }

      Text(
        text = "MOCHI WORLD",
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        color = MochiDarkBrown,
        textAlign = TextAlign.Center
      )
      Text(
        text = "ANIMAL SANCTUARY",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        color = MochiSoftBrown,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Cozy intro card
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "A gentle sanctuary awaits you! Every stray, playful, and magical creature in the valley is dreaming of a cozy home filled with warmth, treats, and friends.",
            fontSize = 13.sp,
            color = MochiSoftBrown,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Large preview of selected starter
          val currentSpecies = SpeciesCatalog.getById(selectedSpeciesId)
          Box(
            modifier = Modifier
              .size(130.dp)
              .clip(CircleShape)
              .background(MochiSakuraLight),
            contentAlignment = Alignment.Center
          ) {
            MochiAnimalView(
              species = currentSpecies,
              sizeDp = 110.dp,
              isExcited = true
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = currentSpecies.name,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MochiDarkBrown
          )
          Text(
            text = currentSpecies.personality,
            fontSize = 12.sp,
            color = MochiSakuraDeep
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Choose Your First Rescue Companion:",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MochiDarkBrown
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Starter choices row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        starterSpeciesIds.forEach { id ->
          val sp = SpeciesCatalog.getById(id)
          val isSelected = selectedSpeciesId == id
          Card(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) MochiSakuraDeep else Color.White,
                shape = RoundedCornerShape(16.dp)
              )
              .clickable {
                SoundManager.playPop()
                selectedSpeciesId = id
              }
              .testTag("starter_choice_$id"),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MochiSakuraLight else Color.White
            )
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier.size(54.dp),
                contentAlignment = Alignment.Center
              ) {
                MochiAnimalView(
                  species = sp,
                  sizeDp = 48.dp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = sp.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MochiDarkBrown,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Custom Name Field
      OutlinedTextField(
        value = petName,
        onValueChange = { petName = it },
        label = { Text("Give your pet a cozy name:") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MochiSakuraDeep,
          unfocusedBorderColor = MochiSakuraPink,
          focusedLabelColor = MochiSakuraDeep,
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("starter_name_input")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Quick Name Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        nameSuggestions.take(4).forEach { sug ->
          Surface(
            color = Color.White.copy(alpha = 0.85f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.clickable {
              SoundManager.playPop()
              petName = sug
            }
          ) {
            Text(
              text = sug,
              fontSize = 11.sp,
              color = MochiDarkBrown,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Start Button
      Button(
        onClick = {
          onAdoptFirst(selectedSpeciesId, petName)
        },
        colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("begin_journey_button")
      ) {
        Text(
          text = "✨ Open Sanctuary Gate ✨",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
