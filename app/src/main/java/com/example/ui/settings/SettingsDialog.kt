package com.example.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.ui.theme.MochiDarkBrown
import com.example.ui.theme.MochiSakuraDeep
import com.example.ui.theme.MochiSakuraLight
import com.example.ui.theme.MochiSakuraPink
import com.example.ui.theme.MochiSoftBrown

@Composable
fun SettingsDialog(
  soundEnabled: Boolean,
  reducedMotion: Boolean,
  onToggleSound: () -> Unit,
  onToggleReducedMotion: () -> Unit,
  onResetProgress: () -> Unit,
  onDismiss: () -> Unit
) {
  var showResetConfirmation by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "⚙️ Sanctuary Settings",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MochiDarkBrown
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Sound Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Sound Effects & Chimes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
            Text("Cute pops, sparkles, and lullabies", fontSize = 11.sp, color = MochiSoftBrown)
          }
          Switch(
            checked = soundEnabled,
            onCheckedChange = { onToggleSound() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = MochiSakuraDeep,
              uncheckedThumbColor = Color.White,
              uncheckedTrackColor = Color.LightGray
            ),
            modifier = Modifier.testTag("toggle_sound_switch")
          )
        }

        // Reduced Motion Switch
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Reduced Motion", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
            Text("Calmer animations for comfort", fontSize = 11.sp, color = MochiSoftBrown)
          }
          Switch(
            checked = reducedMotion,
            onCheckedChange = { onToggleReducedMotion() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = MochiSakuraDeep,
              uncheckedThumbColor = Color.White,
              uncheckedTrackColor = Color.LightGray
            ),
            modifier = Modifier.testTag("toggle_motion_switch")
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Reset Data Button
        Button(
          onClick = { showResetConfirmation = true },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().testTag("reset_progress_button")
        ) {
          Text("Reset Sanctuary Progress", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }

        // About / Credits
        Text(
          text = "Mochi World: Animal Sanctuary v1.0\nCrafted with love for animal fans everywhere 🌸",
          fontSize = 10.sp,
          color = MochiSoftBrown,
          lineHeight = 14.sp
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text("Done", color = Color.White)
      }
    }
  )

  if (showResetConfirmation) {
    AlertDialog(
      onDismissRequest = { showResetConfirmation = false },
      title = { Text("Reset All Progress?", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F)) },
      text = {
        Text("This will wipe all rescued animals, decor, and coins and restart your sanctuary. Are you sure?")
      },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmation = false
            onResetProgress()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
          Text("Reset Everything", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmation = false }) {
          Text("Cancel", color = MochiDarkBrown)
        }
      }
    )
  }
}
