package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
  var isSoundEnabled: Boolean = true
  private val scope = CoroutineScope(Dispatchers.Default)

  private fun playToneSequence(vararg notes: Pair<Float, Int>) {
    if (!isSoundEnabled) return
    scope.launch {
      try {
        val sampleRate = 22050
        var totalSamples = 0
        for ((_, dur) in notes) {
          totalSamples += (sampleRate * dur / 1000)
        }
        val buffer = ShortArray(totalSamples)
        var bufferIdx = 0

        for ((freq, durMs) in notes) {
          val samples = sampleRate * durMs / 1000
          for (i in 0 until samples) {
            val progress = i.toFloat() / samples
            // Envelope: fast attack, exponential decay for gentle chime
            val amp = (1f - progress * progress) * 0.7f
            val angle = 2.0 * Math.PI * i / (sampleRate / freq)
            // Sine with slight 2nd harmonic for warmth
            val sampleVal = (sin(angle) + 0.3 * sin(angle * 2.0)) * amp
            val clamped = (sampleVal * Short.MAX_VALUE * 0.5f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[bufferIdx++] = clamped.toShort()
          }
        }

        val track = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(buffer.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        // Release after finish
        kotlinx.coroutines.delay(1000)
        track.release()
      } catch (_: Exception) {
        // Fallback gracefully if audio hardware is busy
      }
    }
  }

  fun playPop() {
    playToneSequence(Pair(600f, 35), Pair(900f, 45))
  }

  fun playPetHeart() {
    playToneSequence(Pair(523.25f, 60), Pair(659.25f, 60), Pair(783.99f, 80), Pair(1046.50f, 120))
  }

  fun playSparkle() {
    playToneSequence(Pair(880f, 40), Pair(1174.66f, 40), Pair(1479.98f, 50), Pair(1760f, 90))
  }

  fun playEatMunch() {
    playToneSequence(Pair(440f, 50), Pair(554.37f, 50), Pair(440f, 60))
  }

  fun playCleanBubble() {
    playToneSequence(Pair(700f, 30), Pair(850f, 30), Pair(1100f, 40))
  }

  fun playLevelUp() {
    playToneSequence(
      Pair(523.25f, 70),
      Pair(659.25f, 70),
      Pair(783.99f, 70),
      Pair(1046.50f, 150),
      Pair(880f, 60),
      Pair(1046.50f, 250)
    )
  }

  fun playStarCatch() {
    playToneSequence(Pair(1046.50f, 40), Pair(1318.51f, 70))
  }

  fun playMiss() {
    playToneSequence(Pair(350f, 70), Pair(260f, 100))
  }
}
