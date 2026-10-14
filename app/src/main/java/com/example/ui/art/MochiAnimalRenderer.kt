package com.example.ui.art

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.AnimalSpecies
import com.example.data.model.EarShape
import kotlin.math.sin

@Composable
fun MochiAnimalView(
  species: AnimalSpecies,
  modifier: Modifier = Modifier,
  sizeDp: Dp = 100.dp,
  equippedHat: String? = null,
  equippedAccessory: String? = null,
  isSleeping: Boolean = false,
  isExcited: Boolean = false,
  reducedMotion: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "mochi_anim")

  // Gentle breathing / squash-stretch
  val breathScale by if (reducedMotion) {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
  } else {
    infiniteTransition.animateFloat(
      initialValue = 0.98f,
      targetValue = 1.03f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = if (isExcited) 300 else 1400, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "breath"
    )
  }

  // Blinking cycle (mostly 0 = open, briefly 1 = closed)
  val blinkProgress by if (reducedMotion || isSleeping) {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
  } else {
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 3200, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      ),
      label = "blink"
    )
  }

  val earWiggle by if (reducedMotion) {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
  } else {
    infiniteTransition.animateFloat(
      initialValue = -3f,
      targetValue = 3f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "ear"
    )
  }

  val isEyeBlinking = blinkProgress > 0.94f

  Box(
    modifier = modifier.size(sizeDp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(sizeDp)) {
      val w = size.width
      val h = size.height

      val basePrimary = Color(species.primaryColor)
      val baseSecondary = Color(species.secondaryColor)
      val cheekCol = Color(species.cheekColor)
      val eyeCol = Color(species.eyeColor)

      // Center and scale with breathing
      val cx = w / 2f
      val cy = h / 2f + 4f

      val bodyW = (w * 0.65f) * (2f - breathScale)
      val bodyH = (h * 0.58f) * breathScale

      // 1. Draw Angel Wings Accessory Behind Body if equipped
      if (equippedAccessory == "acc_angel_wings") {
        drawAngelWings(cx, cy, w, h)
      }

      // 2. Draw Ears Behind Head
      drawMochiEars(
        species = species,
        cx = cx,
        cy = cy - bodyH * 0.35f,
        bodyW = bodyW,
        bodyH = bodyH,
        baseColor = basePrimary,
        innerColor = baseSecondary,
        earWiggle = earWiggle
      )

      // 3. Draw Body (Round Chubby Mochi Shape)
      val bodyTop = cy - bodyH / 2f
      val bodyLeft = cx - bodyW / 2f

      // Soft shadow under animal
      drawOval(
        color = Color(0x224A3E3D),
        topLeft = Offset(cx - bodyW * 0.45f, cy + bodyH * 0.42f),
        size = Size(bodyW * 0.9f, bodyH * 0.22f)
      )

      // Body gradient (soft highlights on top, cozy shadow on bottom)
      val bodyBrush = Brush.verticalGradient(
        colors = listOf(
          basePrimary,
          basePrimary.copy(alpha = 0.95f),
          baseSecondary.copy(alpha = 0.9f)
        ),
        startY = bodyTop,
        endY = bodyTop + bodyH
      )

      drawRoundRect(
        brush = bodyBrush,
        topLeft = Offset(bodyLeft, bodyTop),
        size = Size(bodyW, bodyH),
        cornerRadius = CornerRadius(bodyW * 0.48f, bodyH * 0.48f)
      )

      // 4. Species Specific Patterns
      drawSpeciesFeatures(species, cx, cy, bodyW, bodyH, baseSecondary)

      // 5. Cheeks
      val cheekRadius = bodyW * 0.09f
      val cheekY = cy + bodyH * 0.06f
      drawCircle(
        color = cheekCol.copy(alpha = 0.55f),
        radius = cheekRadius,
        center = Offset(cx - bodyW * 0.28f, cheekY)
      )
      drawCircle(
        color = cheekCol.copy(alpha = 0.55f),
        radius = cheekRadius,
        center = Offset(cx + bodyW * 0.28f, cheekY)
      )

      // 6. Eyes
      val eyeY = cy - bodyH * 0.04f
      val eyeOffsetX = bodyW * 0.20f
      if (isSleeping || isEyeBlinking || species.id == "capybara_zen") {
        // Sleepy curved eye lines ^^
        val eyeW = bodyW * 0.10f
        val leftEyePath = Path().apply {
          moveTo(cx - eyeOffsetX - eyeW, eyeY)
          quadraticBezierTo(cx - eyeOffsetX, eyeY - 6f, cx - eyeOffsetX + eyeW, eyeY)
        }
        val rightEyePath = Path().apply {
          moveTo(cx + eyeOffsetX - eyeW, eyeY)
          quadraticBezierTo(cx + eyeOffsetX, eyeY - 6f, cx + eyeOffsetX + eyeW, eyeY)
        }
        drawPath(leftEyePath, eyeCol, style = Stroke(width = 3.5f))
        drawPath(rightEyePath, eyeCol, style = Stroke(width = 3.5f))
      } else {
        // Sparkly anime eyes with highlights
        val eyeR = bodyW * 0.075f
        drawCircle(color = eyeCol, radius = eyeR, center = Offset(cx - eyeOffsetX, eyeY))
        drawCircle(color = eyeCol, radius = eyeR, center = Offset(cx + eyeOffsetX, eyeY))
        // Catchlights
        drawCircle(color = Color.White, radius = eyeR * 0.45f, center = Offset(cx - eyeOffsetX - 2f, eyeY - 2.5f))
        drawCircle(color = Color.White, radius = eyeR * 0.45f, center = Offset(cx + eyeOffsetX - 2f, eyeY - 2.5f))
        drawCircle(color = Color.White, radius = eyeR * 0.2f, center = Offset(cx - eyeOffsetX + 2.5f, eyeY + 2.5f))
        drawCircle(color = Color.White, radius = eyeR * 0.2f, center = Offset(cx + eyeOffsetX + 2.5f, eyeY + 2.5f))
      }

      // 7. Nose & Mouth / Beak
      drawMouthAndNose(species, cx, cy, bodyW, bodyH, eyeCol)

      // 8. Whiskers if needed
      if (species.hasWhiskers) {
        drawWhiskers(cx, cy, bodyW, bodyH, eyeCol)
      }

      // 9. Legendary / Fantasy Celestial Marks
      drawFantasyCelestialMarks(species, cx, cy, bodyW, bodyH)

      // 10. Front Paws (Adorable little paws resting at bottom)
      val pawY = cy + bodyH * 0.38f
      val pawR = bodyW * 0.08f
      drawCircle(color = basePrimary, radius = pawR, center = Offset(cx - bodyW * 0.18f, pawY))
      drawCircle(color = basePrimary, radius = pawR, center = Offset(cx + bodyW * 0.18f, pawY))
      drawCircle(color = Color.White.copy(alpha = 0.4f), radius = pawR * 0.6f, center = Offset(cx - bodyW * 0.18f, pawY))
      drawCircle(color = Color.White.copy(alpha = 0.4f), radius = pawR * 0.6f, center = Offset(cx + bodyW * 0.18f, pawY))

      // 11. Overlaid Outfits
      if (equippedAccessory != null && equippedAccessory != "acc_angel_wings") {
        drawAccessory(equippedAccessory, cx, cy + bodyH * 0.28f, bodyW)
      }
      if (equippedHat != null) {
        drawHat(equippedHat, cx, cy - bodyH * 0.45f, bodyW)
      }
    }
  }
}

private fun DrawScope.drawMochiEars(
  species: AnimalSpecies,
  cx: Float,
  cy: Float,
  bodyW: Float,
  bodyH: Float,
  baseColor: Color,
  innerColor: Color,
  earWiggle: Float
) {
  when (species.earShape) {
    EarShape.LONG_BUNNY -> {
      val earW = bodyW * 0.18f
      val earH = bodyH * 0.65f
      // Left Ear
      drawRoundRect(
        color = baseColor,
        topLeft = Offset(cx - bodyW * 0.32f + earWiggle, cy - earH * 0.85f),
        size = Size(earW, earH),
        cornerRadius = CornerRadius(earW / 2f, earW / 2f)
      )
      drawRoundRect(
        color = innerColor,
        topLeft = Offset(cx - bodyW * 0.32f + earWiggle + 3f, cy - earH * 0.75f),
        size = Size(earW - 6f, earH * 0.75f),
        cornerRadius = CornerRadius(earW / 2f, earW / 2f)
      )
      // Right Ear
      drawRoundRect(
        color = baseColor,
        topLeft = Offset(cx + bodyW * 0.14f - earWiggle, cy - earH * 0.85f),
        size = Size(earW, earH),
        cornerRadius = CornerRadius(earW / 2f, earW / 2f)
      )
      drawRoundRect(
        color = innerColor,
        topLeft = Offset(cx + bodyW * 0.14f - earWiggle + 3f, cy - earH * 0.75f),
        size = Size(earW - 6f, earH * 0.75f),
        cornerRadius = CornerRadius(earW / 2f, earW / 2f)
      )
    }
    EarShape.POINTY_CAT, EarShape.FOX_LARGE -> {
      val earSize = if (species.earShape == EarShape.FOX_LARGE) bodyW * 0.32f else bodyW * 0.24f
      // Left Cat Ear Path
      val leftEar = Path().apply {
        moveTo(cx - bodyW * 0.45f, cy + earSize * 0.2f)
        lineTo(cx - bodyW * 0.32f, cy - earSize)
        lineTo(cx - bodyW * 0.10f, cy)
        close()
      }
      drawPath(leftEar, baseColor)
      val innerLeft = Path().apply {
        moveTo(cx - bodyW * 0.40f, cy)
        lineTo(cx - bodyW * 0.32f, cy - earSize * 0.75f)
        lineTo(cx - bodyW * 0.16f, cy - 2f)
        close()
      }
      drawPath(innerLeft, innerColor)

      // Right Cat Ear Path
      val rightEar = Path().apply {
        moveTo(cx + bodyW * 0.10f, cy)
        lineTo(cx + bodyW * 0.32f, cy - earSize)
        lineTo(cx + bodyW * 0.45f, cy + earSize * 0.2f)
        close()
      }
      drawPath(rightEar, baseColor)
      val innerRight = Path().apply {
        moveTo(cx + bodyW * 0.16f, cy - 2f)
        lineTo(cx + bodyW * 0.32f, cy - earSize * 0.75f)
        lineTo(cx + bodyW * 0.40f, cy)
        close()
      }
      drawPath(innerRight, innerColor)
    }
    EarShape.FLOPPY_DOG -> {
      val earW = bodyW * 0.26f
      val earH = bodyH * 0.50f
      drawRoundRect(
        color = innerColor,
        topLeft = Offset(cx - bodyW * 0.52f, cy - earH * 0.2f),
        size = Size(earW, earH),
        cornerRadius = CornerRadius(earW * 0.45f, earH * 0.45f)
      )
      drawRoundRect(
        color = innerColor,
        topLeft = Offset(cx + bodyW * 0.26f, cy - earH * 0.2f),
        size = Size(earW, earH),
        cornerRadius = CornerRadius(earW * 0.45f, earH * 0.45f)
      )
    }
    EarShape.HORN_GOAT -> {
      val hornR = bodyW * 0.14f
      drawCircle(color = innerColor, radius = hornR, center = Offset(cx - bodyW * 0.25f, cy - bodyH * 0.35f))
      drawCircle(color = innerColor, radius = hornR, center = Offset(cx + bodyW * 0.25f, cy - bodyH * 0.35f))
    }
    EarShape.ROUND, EarShape.TINY_HAMSTER -> {
      val earR = if (species.earShape == EarShape.TINY_HAMSTER) bodyW * 0.10f else bodyW * 0.16f
      drawCircle(color = baseColor, radius = earR, center = Offset(cx - bodyW * 0.35f, cy - earR * 0.5f))
      drawCircle(color = innerColor, radius = earR * 0.6f, center = Offset(cx - bodyW * 0.35f, cy - earR * 0.5f))
      drawCircle(color = baseColor, radius = earR, center = Offset(cx + bodyW * 0.35f, cy - earR * 0.5f))
      drawCircle(color = innerColor, radius = earR * 0.6f, center = Offset(cx + bodyW * 0.35f, cy - earR * 0.5f))
    }
    EarShape.NONE -> {}
  }
}

private fun DrawScope.drawSpeciesFeatures(
  species: AnimalSpecies,
  cx: Float,
  cy: Float,
  bodyW: Float,
  bodyH: Float,
  secondaryColor: Color
) {
  when (species.id) {
    "cat_calico" -> {
      // Soft orange patch on head
      drawCircle(
        color = secondaryColor,
        radius = bodyW * 0.22f,
        center = Offset(cx - bodyW * 0.22f, cy - bodyH * 0.25f)
      )
      // Soft dark grey patch on opposite side
      drawCircle(
        color = Color(0xFF78909C),
        radius = bodyW * 0.16f,
        center = Offset(cx + bodyW * 0.26f, cy - bodyH * 0.18f)
      )
    }
    "panda_bamboo" -> {
      // Black eye patches
      val patchR = bodyW * 0.13f
      drawOval(
        color = Color(0xFF263238),
        topLeft = Offset(cx - bodyW * 0.32f, cy - bodyH * 0.12f),
        size = Size(patchR * 2f, patchR * 1.6f)
      )
      drawOval(
        color = Color(0xFF263238),
        topLeft = Offset(cx + bodyW * 0.08f, cy - bodyH * 0.12f),
        size = Size(patchR * 2f, patchR * 1.6f)
      )
    }
    "deer_blossom" -> {
      // Delicate blossom spots
      val spotR = 2.5f
      drawCircle(Color.White, spotR, Offset(cx - bodyW * 0.22f, cy - bodyH * 0.20f))
      drawCircle(Color.White, spotR, Offset(cx - bodyW * 0.15f, cy - bodyH * 0.25f))
      drawCircle(Color.White, spotR, Offset(cx + bodyW * 0.20f, cy - bodyH * 0.22f))
      drawCircle(Color.White, spotR, Offset(cx + bodyW * 0.14f, cy - bodyH * 0.28f))
    }
    "turtle_mossy" -> {
      // Emerald rim / shell
      drawRoundRect(
        color = Color(0xFF388E3C),
        topLeft = Offset(cx - bodyW * 0.48f, cy - bodyH * 0.45f),
        size = Size(bodyW * 0.96f, bodyH * 0.9f),
        cornerRadius = CornerRadius(bodyW * 0.45f, bodyH * 0.45f),
        style = Stroke(width = 5f)
      )
    }
    "penguin_tuxedo" -> {
      // White belly tummy
      drawOval(
        color = Color.White,
        topLeft = Offset(cx - bodyW * 0.28f, cy - bodyH * 0.1f),
        size = Size(bodyW * 0.56f, bodyH * 0.56f)
      )
    }
    "cow_strawberry" -> {
      // Cute pink patches
      drawCircle(secondaryColor.copy(alpha = 0.8f), bodyW * 0.18f, Offset(cx - bodyW * 0.25f, cy - bodyH * 0.2f))
      drawCircle(secondaryColor.copy(alpha = 0.8f), bodyW * 0.14f, Offset(cx + bodyW * 0.22f, cy + bodyH * 0.1f))
    }
    "elephant_peanut" -> {
      // Cute little trunk
      val trunkPath = Path().apply {
        moveTo(cx - 5f, cy + 4f)
        quadraticBezierTo(cx - 8f, cy + bodyH * 0.35f, cx + 10f, cy + bodyH * 0.32f)
        lineTo(cx + 8f, cy + bodyH * 0.24f)
        quadraticBezierTo(cx, cy + bodyH * 0.22f, cx + 5f, cy + 4f)
        close()
      }
      drawPath(trunkPath, secondaryColor)
    }
  }
}

private fun DrawScope.drawMouthAndNose(
  species: AnimalSpecies,
  cx: Float,
  cy: Float,
  bodyW: Float,
  bodyH: Float,
  eyeCol: Color
) {
  val mouthY = cy + bodyH * 0.08f
  if (species.hasBeak) {
    // Orange beak
    val beakPath = Path().apply {
      moveTo(cx - bodyW * 0.09f, mouthY - 3f)
      lineTo(cx, mouthY + bodyW * 0.08f)
      lineTo(cx + bodyW * 0.09f, mouthY - 3f)
      close()
    }
    drawPath(beakPath, Color(0xFFFF9800))
  } else {
    // Tiny heart or dot nose
    drawCircle(
      color = Color(0xFFFF8DA1),
      radius = 3.5f,
      center = Offset(cx, mouthY - 4f)
    )
    // Cute kitty mouth `w`
    val mouthPath = Path().apply {
      moveTo(cx - bodyW * 0.08f, mouthY + 1f)
      quadraticBezierTo(cx - bodyW * 0.04f, mouthY + 7f, cx, mouthY + 2f)
      quadraticBezierTo(cx + bodyW * 0.04f, mouthY + 7f, cx + bodyW * 0.08f, mouthY + 1f)
    }
    drawPath(mouthPath, eyeCol, style = Stroke(width = 2.5f))
  }
}

private fun DrawScope.drawWhiskers(
  cx: Float,
  cy: Float,
  bodyW: Float,
  bodyH: Float,
  eyeCol: Color
) {
  val whiskerY = cy + bodyH * 0.06f
  val wStroke = Stroke(width = 1.8f)
  // Left whiskers
  drawLine(eyeCol.copy(alpha = 0.5f), Offset(cx - bodyW * 0.22f, whiskerY - 2f), Offset(cx - bodyW * 0.44f, whiskerY - 6f), strokeWidth = 1.8f)
  drawLine(eyeCol.copy(alpha = 0.5f), Offset(cx - bodyW * 0.22f, whiskerY + 4f), Offset(cx - bodyW * 0.44f, whiskerY + 6f), strokeWidth = 1.8f)
  // Right whiskers
  drawLine(eyeCol.copy(alpha = 0.5f), Offset(cx + bodyW * 0.22f, whiskerY - 2f), Offset(cx + bodyW * 0.44f, whiskerY - 6f), strokeWidth = 1.8f)
  drawLine(eyeCol.copy(alpha = 0.5f), Offset(cx + bodyW * 0.22f, whiskerY + 4f), Offset(cx + bodyW * 0.44f, whiskerY + 6f), strokeWidth = 1.8f)
}

private fun DrawScope.drawFantasyCelestialMarks(
  species: AnimalSpecies,
  cx: Float,
  cy: Float,
  bodyW: Float,
  bodyH: Float
) {
  when (species.id) {
    "moon_kitten" -> {
      // Golden glowing crescent on forehead
      val moonPath = Path().apply {
        val mx = cx
        val my = cy - bodyH * 0.26f
        moveTo(mx, my - 7f)
        cubicTo(mx + 6f, my - 7f, mx + 6f, my + 7f, mx, my + 7f)
        cubicTo(mx + 2f, my + 4f, mx + 2f, my - 4f, mx, my - 7f)
        close()
      }
      drawPath(moonPath, Color(0xFFFFD54F))
    }
    "star_fox" -> {
      // Golden sparkling 4-pointed star
      val starX = cx
      val starY = cy - bodyH * 0.26f
      drawCircle(Color(0xFFFFD700), 4f, Offset(starX, starY))
    }
    "pastel_bunny_magic" -> {
      // Glowing rainbow ring halo
      drawCircle(
        color = Color(0xFFFF80AB).copy(alpha = 0.4f),
        radius = bodyW * 0.42f,
        center = Offset(cx, cy),
        style = Stroke(width = 3f)
      )
    }
  }
}

private fun DrawScope.drawAngelWings(cx: Float, cy: Float, w: Float, h: Float) {
  val wingColor = Color.White.copy(alpha = 0.9f)
  val wingStroke = Color(0xFFE0E0E0)
  // Left Wing
  val leftWing = Path().apply {
    moveTo(cx - w * 0.15f, cy)
    cubicTo(cx - w * 0.50f, cy - h * 0.35f, cx - w * 0.45f, cy + h * 0.15f, cx - w * 0.15f, cy + h * 0.20f)
    close()
  }
  drawPath(leftWing, wingColor)
  drawPath(leftWing, wingStroke, style = Stroke(width = 2f))

  // Right Wing
  val rightWing = Path().apply {
    moveTo(cx + w * 0.15f, cy)
    cubicTo(cx + w * 0.50f, cy - h * 0.35f, cx + w * 0.45f, cy + h * 0.15f, cx + w * 0.15f, cy + h * 0.20f)
    close()
  }
  drawPath(rightWing, wingColor)
  drawPath(rightWing, wingStroke, style = Stroke(width = 2f))
}

private fun DrawScope.drawAccessory(accessoryId: String, cx: Float, cy: Float, bodyW: Float) {
  when (accessoryId) {
    "acc_ribbon_bow" -> {
      // Cute pink ribbon
      val bowColor = Color(0xFFFF4081)
      drawCircle(bowColor, 4.5f, Offset(cx, cy))
      val leftLoop = Path().apply {
        moveTo(cx, cy)
        lineTo(cx - bodyW * 0.16f, cy - 6f)
        lineTo(cx - bodyW * 0.16f, cy + 6f)
        close()
      }
      val rightLoop = Path().apply {
        moveTo(cx, cy)
        lineTo(cx + bodyW * 0.16f, cy - 6f)
        lineTo(cx + bodyW * 0.16f, cy + 6f)
        close()
      }
      drawPath(leftLoop, bowColor)
      drawPath(rightLoop, bowColor)
    }
    "acc_scarf" -> {
      // Knitted peach scarf band
      drawRoundRect(
        color = Color(0xFFFFAB91),
        topLeft = Offset(cx - bodyW * 0.35f, cy - 6f),
        size = Size(bodyW * 0.70f, 13f),
        cornerRadius = CornerRadius(6f, 6f)
      )
      // Hanging tail
      drawRoundRect(
        color = Color(0xFFFFAB91),
        topLeft = Offset(cx + bodyW * 0.10f, cy),
        size = Size(11f, 18f),
        cornerRadius = CornerRadius(3f, 3f)
      )
    }
    "acc_bell_collar" -> {
      // Red collar band
      drawRoundRect(
        color = Color(0xFFE53935),
        topLeft = Offset(cx - bodyW * 0.32f, cy - 4f),
        size = Size(bodyW * 0.64f, 8f),
        cornerRadius = CornerRadius(4f, 4f)
      )
      // Golden bell
      drawCircle(Color(0xFFFFD700), 5.5f, Offset(cx, cy + 4f))
    }
    "acc_star_glasses" -> {
      // Golden star frames
      drawCircle(Color(0xFFFFD700), bodyW * 0.11f, Offset(cx - bodyW * 0.20f, cy - bodyH_approx(bodyW) * 0.35f), style = Stroke(width = 3f))
      drawCircle(Color(0xFFFFD700), bodyW * 0.11f, Offset(cx + bodyW * 0.20f, cy - bodyH_approx(bodyW) * 0.35f), style = Stroke(width = 3f))
      drawLine(Color(0xFFFFD700), Offset(cx - bodyW * 0.09f, cy - bodyH_approx(bodyW) * 0.35f), Offset(cx + bodyW * 0.09f, cy - bodyH_approx(bodyW) * 0.35f), strokeWidth = 3f)
    }
  }
}

private fun bodyH_approx(bodyW: Float): Float = bodyW * 0.9f

private fun DrawScope.drawHat(hatId: String, cx: Float, cy: Float, bodyW: Float) {
  when (hatId) {
    "hat_flower_crown" -> {
      // Tiny blossom circlet
      for (i in -3..3) {
        val fx = cx + (i * bodyW * 0.09f)
        val fy = cy + (sin(i.toDouble()) * 3).toFloat()
        val petalCol = if (i % 2 == 0) Color(0xFFFF80AB) else Color(0xFFFFF59D)
        drawCircle(petalCol, 5f, Offset(fx, fy))
        drawCircle(Color.White, 2f, Offset(fx, fy))
      }
    }
    "hat_chef" -> {
      // Chef toque
      val hatTop = cy - 24f
      drawRoundRect(
        color = Color.White,
        topLeft = Offset(cx - bodyW * 0.26f, cy - 6f),
        size = Size(bodyW * 0.52f, 10f),
        cornerRadius = CornerRadius(2f, 2f)
      )
      drawCircle(Color.White, 16f, Offset(cx, hatTop + 4f))
      drawCircle(Color.White, 13f, Offset(cx - 12f, hatTop + 8f))
      drawCircle(Color.White, 13f, Offset(cx + 12f, hatTop + 8f))
    }
    "hat_straw" -> {
      // Straw hat brim
      drawOval(
        color = Color(0xFFFFE082),
        topLeft = Offset(cx - bodyW * 0.45f, cy - 5f),
        size = Size(bodyW * 0.90f, 12f)
      )
      drawRoundRect(
        color = Color(0xFFFFCA28),
        topLeft = Offset(cx - bodyW * 0.22f, cy - 20f),
        size = Size(bodyW * 0.44f, 18f),
        cornerRadius = CornerRadius(8f, 8f)
      )
    }
    "hat_beret" -> {
      // Strawberry Beret
      drawOval(
        color = Color(0xFFFF5252),
        topLeft = Offset(cx - bodyW * 0.30f + 5f, cy - 14f),
        size = Size(bodyW * 0.60f, 18f)
      )
      // Stem
      drawCircle(Color(0xFF4CAF50), 3f, Offset(cx + 6f, cy - 15f))
    }
    "hat_wizard" -> {
      // Wizard Cone Hat
      val hatPath = Path().apply {
        moveTo(cx - bodyW * 0.32f, cy)
        lineTo(cx, cy - 36f)
        lineTo(cx + bodyW * 0.32f, cy)
        close()
      }
      drawPath(hatPath, Color(0xFF7E57C2))
      // Little gold star on tip
      drawCircle(Color(0xFFFFD700), 4f, Offset(cx, cy - 36f))
    }
    "hat_party" -> {
      // Party cone
      val partyPath = Path().apply {
        moveTo(cx - bodyW * 0.20f, cy)
        lineTo(cx, cy - 32f)
        lineTo(cx + bodyW * 0.20f, cy)
        close()
      }
      drawPath(partyPath, Color(0xFFFF4081))
      drawCircle(Color(0xFFFFEE58), 5f, Offset(cx, cy - 32f))
    }
    "hat_nightcap" -> {
      // Droopy nightcap
      val nightcap = Path().apply {
        moveTo(cx - bodyW * 0.25f, cy)
        quadraticBezierTo(cx - 5f, cy - 28f, cx + bodyW * 0.30f, cy - 15f)
        lineTo(cx + bodyW * 0.15f, cy)
        close()
      }
      drawPath(nightcap, Color(0xFF90CAF9))
      drawCircle(Color(0xFFFFD54F), 5f, Offset(cx + bodyW * 0.30f, cy - 15f))
    }
  }
}
