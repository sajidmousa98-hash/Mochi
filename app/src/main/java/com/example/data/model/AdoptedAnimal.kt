package com.example.data.model

data class AdoptedAnimal(
  val id: String,
  val speciesId: String,
  val customName: String,
  val hunger: Int = 85,
  val happiness: Int = 90,
  val cleanliness: Int = 80,
  val energy: Int = 95,
  val affectionLevel: Int = 1,
  val affectionExp: Int = 0,
  val equippedHat: String? = null,
  val equippedAccessory: String? = null,
  val variant: String = "NORMAL",
  val assignedZoneId: String = "flower_meadow",
  val posXRatio: Float = 0.5f,
  val posYRatio: Float = 0.6f,
  val isSleeping: Boolean = false,
  val adoptedAtTimestamp: Long = System.currentTimeMillis()
) {
  val averageCare: Int
    get() = (hunger + happiness + cleanliness + energy) / 4

  val moodDescription: String
    get() = when {
      averageCare >= 80 -> "Ecstatic & Full of Joy!"
      averageCare >= 50 -> "Content & Peaceful"
      else -> "Needs Love & Snackies"
    }
}
