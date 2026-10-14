package com.example.ui.quests

import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.DefaultQuestsAndAchievements
import com.example.data.model.GameState
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
fun QuestsScreen(
  gameState: GameState,
  onClaimQuest: (String) -> Unit,
  onClaimAchievement: (String, Int, Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Daily Quests, 1 = Achievements

  val achievements = DefaultQuestsAndAchievements.getAchievements(
    petCount = gameState.totalPetsComforted,
    rescuedCount = gameState.adoptedAnimals.size,
    decorCount = gameState.placedDecorations.size,
    gamesPlayed = gameState.totalGamesPlayed,
    highStarScore = gameState.highStarScore,
    claimedSet = gameState.claimedAchievementIds
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(MochiSakuraLight, MochiLavender.copy(alpha = 0.3f), Color.White)
        )
      )
  ) {
    // Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = MochiSakuraDeep
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = {
          SoundManager.playPop()
          selectedTab = 0
        },
        text = { Text("Daily Quests 📋", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = {
          SoundManager.playPop()
          selectedTab = 1
        },
        text = { Text("Achievements 🏆", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      if (selectedTab == 0) {
        // Daily Quests Tab
        Text("Daily Sanctuary Goals", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MochiDarkBrown)
        Text("Refreshes every day! Complete goals for bonus coins & stars.", fontSize = 11.sp, color = MochiSoftBrown)

        Spacer(modifier = Modifier.height(12.dp))

        gameState.dailyQuests.forEach { quest ->
          val progress = (quest.currentCount.toFloat() / quest.targetCount.toFloat()).coerceIn(0f, 1f)

          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(quest.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
                Text(quest.description, fontSize = 11.sp, color = MochiSoftBrown)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                      .weight(1f)
                      .height(6.dp)
                      .clip(RoundedCornerShape(3.dp)),
                    color = MochiSakuraDeep,
                    trackColor = MochiSakuraLight
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "${quest.currentCount}/${quest.targetCount}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MochiDarkBrown
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                  Text("🪙 +${quest.rewardCoins}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
                  if (quest.rewardStars > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("⭐ +${quest.rewardStars}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MochiSakuraDeep)
                  }
                }
              }

              Spacer(modifier = Modifier.width(10.dp))

              Button(
                onClick = { onClaimQuest(quest.id) },
                enabled = quest.isCompleted && !quest.isClaimed,
                colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .height(36.dp)
                  .testTag("claim_quest_${quest.id}")
              ) {
                Text(
                  text = when {
                    quest.isClaimed -> "Claimed"
                    quest.isCompleted -> "Claim ✨"
                    else -> "In Progress"
                  },
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
            }
          }
        }
      } else {
        // Achievements Tab
        Text("Sanctuary Milestones", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MochiDarkBrown)
        Text("Honorary badges earned by loving and caring for animals.", fontSize = 11.sp, color = MochiSoftBrown)

        Spacer(modifier = Modifier.height(12.dp))

        achievements.forEach { ach ->
          val progress = (ach.current.toFloat() / ach.target.toFloat()).coerceIn(0f, 1f)

          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(
                    if (ach.isCompleted) MochiButterYellow.copy(alpha = 0.5f) else Color.LightGray.copy(alpha = 0.3f)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(ach.iconEmoji, fontSize = 24.sp)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(ach.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
                Text(ach.description, fontSize = 10.sp, color = MochiSoftBrown)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                  progress = { progress },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = MochiStarGold,
                  trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                  Text("🪙 +${ach.rewardCoins}  ⭐ +${ach.rewardStars}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              Button(
                onClick = { onClaimAchievement(ach.id, ach.rewardCoins, ach.rewardStars) },
                enabled = ach.isCompleted && !ach.isClaimed,
                colors = ButtonDefaults.buttonColors(containerColor = MochiStarGold),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .height(36.dp)
                  .testTag("claim_achievement_${ach.id}")
              ) {
                Text(
                  text = when {
                    ach.isClaimed -> "Claimed"
                    ach.isCompleted -> "Claim 🏆"
                    else -> "${ach.current}/${ach.target}"
                  },
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
