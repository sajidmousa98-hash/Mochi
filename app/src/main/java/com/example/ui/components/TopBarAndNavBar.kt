package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameState
import com.example.ui.theme.MochiButterYellow
import com.example.ui.theme.MochiDarkBrown
import com.example.ui.theme.MochiLavender
import com.example.ui.theme.MochiLavenderDeep
import com.example.ui.theme.MochiSakuraDeep
import com.example.ui.theme.MochiSakuraLight
import com.example.ui.theme.MochiSakuraPink
import com.example.ui.theme.MochiStarGold
import com.example.viewmodel.AppScreen

@Composable
fun SanctuaryTopBar(
  gameState: GameState,
  isEditMode: Boolean,
  onToggleEditMode: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding(),
    color = Color.Transparent
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Level Badge & XP bar
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color.White.copy(alpha = 0.92f))
          .padding(horizontal = 10.dp, vertical = 5.dp)
      ) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(MochiSakuraPink),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Lv${gameState.sanctuaryLevel}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          val progress = (gameState.currentExp.toFloat() / gameState.requiredExpForNextLevel.toFloat()).coerceIn(0f, 1f)
          Text(
            text = "Sanctuary XP",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MochiDarkBrown
          )
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .width(60.dp)
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = MochiSakuraDeep,
            trackColor = MochiSakuraLight,
          )
        }
      }

      // Currency Indicators
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Coins Pill
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
          Text("🪙", fontSize = 13.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${gameState.playerCoins}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MochiDarkBrown
          )
        }

        // Stars Pill
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
          Text("⭐", fontSize = 13.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${gameState.playerStars}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MochiDarkBrown
          )
        }

        // Edit Sanctuary Mode Toggle Button
        IconButton(
          onClick = onToggleEditMode,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isEditMode) MochiSakuraDeep else Color.White.copy(alpha = 0.92f))
            .testTag("toggle_edit_mode_button")
        ) {
          Icon(
            imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Build,
            contentDescription = "Edit Sanctuary",
            tint = if (isEditMode) Color.White else MochiDarkBrown,
            modifier = Modifier.size(17.dp)
          )
        }

        // Settings Button
        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f))
            .testTag("open_settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = MochiDarkBrown,
            modifier = Modifier.size(17.dp)
          )
        }
      }
    }
  }
}

data class NavTabItem(
  val screen: AppScreen,
  val label: String,
  val iconEmoji: String
)

@Composable
fun SanctuaryBottomNavBar(
  currentScreen: AppScreen,
  onSelectScreen: (AppScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val tabs = listOf(
    NavTabItem(AppScreen.SANCTUARY, "World", "🏡"),
    NavTabItem(AppScreen.CARE, "Care", "🐾"),
    NavTabItem(AppScreen.RESCUE, "Rescue", "🗺️"),
    NavTabItem(AppScreen.GAMES, "Arcade", "🎮"),
    NavTabItem(AppScreen.DRESS_UP, "Dress-Up", "👗"),
    NavTabItem(AppScreen.ENCYCLOPEDIA, "MochiDex", "📖"),
    NavTabItem(AppScreen.QUESTS, "Quests", "🏆")
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding(),
    color = Color.White.copy(alpha = 0.96f),
    shadowElevation = 8.dp,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      tabs.forEach { tab ->
        val isSelected = currentScreen == tab.screen
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelectScreen(tab.screen) }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("nav_tab_${tab.screen.name.lowercase()}")
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(
                if (isSelected) MochiSakuraPink else Color.Transparent
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = tab.iconEmoji,
              fontSize = if (isSelected) 18.sp else 16.sp
            )
          }
          Text(
            text = tab.label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MochiSakuraDeep else MochiDarkBrown.copy(alpha = 0.6f)
          )
        }
      }
    }
  }
}
