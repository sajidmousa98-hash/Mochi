package com.example.data.model

data class HabitatZone(
  val id: String,
  val name: String,
  val description: String,
  val skyGradientTop: Long,
  val skyGradientBottom: Long,
  val groundColor: Long,
  val ambientParticles: String,
  val unlockLevel: Int,
  val iconEmoji: String
)

object HabitatCatalog {
  val zones: List<HabitatZone> = listOf(
    HabitatZone(
      id = "flower_meadow",
      name = "Flower Meadow",
      description = "Sunlit rolling hills covered in pastel clover blossoms and dancing butterflies.",
      skyGradientTop = 0xFFFFF0F5,
      skyGradientBottom = 0xFFBEE3F8,
      groundColor = 0xFFC8E6C9,
      ambientParticles = "petals",
      unlockLevel = 1,
      iconEmoji = "🌸"
    ),
    HabitatZone(
      id = "cozy_home",
      name = "Cozy Animal Home",
      description = "Warm indoor haven with sweet wooden floors, pastel rugs, and sunny window perches.",
      skyGradientTop = 0xFFFFF8E7,
      skyGradientBottom = 0xFFFFE0B2,
      groundColor = 0xFFD7CCC8,
      ambientParticles = "dust_motes",
      unlockLevel = 1,
      iconEmoji = "🏡"
    ),
    HabitatZone(
      id = "enchanted_forest",
      name = "Enchanted Forest",
      description = "Ancient mossy trees with bioluminescent fairy mushrooms and gentle morning mist.",
      skyGradientTop = 0xFFE1BEE7,
      skyGradientBottom = 0xFFC5CAE9,
      groundColor = 0xFFA5D6A7,
      ambientParticles = "spores",
      unlockLevel = 3,
      iconEmoji = "🌲"
    ),
    HabitatZone(
      id = "peaceful_pond",
      name = "Peaceful Pond",
      description = "Crystal clear waters with floating giant water lilies, weeping willows, and wooden docks.",
      skyGradientTop = 0xFFE0F7FA,
      skyGradientBottom = 0xFFB2EBF2,
      groundColor = 0xFF80CBC4,
      ambientParticles = "ripples",
      unlockLevel = 4,
      iconEmoji = "🌊"
    ),
    HabitatZone(
      id = "snowy_habitat",
      name = "Snowy Habitat",
      description = "Soft powdered snowbanks kissed by dancing pastel northern lights.",
      skyGradientTop = 0xFFE8EAF6,
      skyGradientBottom = 0xFFC5CAE9,
      groundColor = 0xFFECEFF1,
      ambientParticles = "snowflakes",
      unlockLevel = 5,
      iconEmoji = "❄️"
    ),
    HabitatZone(
      id = "tropical_garden",
      name = "Tropical Garden",
      description = "Warm golden sands, hibiscus blossoms, and shady mini coconut palms.",
      skyGradientTop = 0xFFFFF9C4,
      skyGradientBottom = 0xFFFFE082,
      groundColor = 0xFFFFE0B2,
      ambientParticles = "sunbeams",
      unlockLevel = 6,
      iconEmoji = "🌺"
    ),
    HabitatZone(
      id = "night_forest",
      name = "Magical Night Forest",
      description = "Starry midnight canopy illuminated by glowing moonbeams and twinkling fireflies.",
      skyGradientTop = 0xFF1A1A2E,
      skyGradientBottom = 0xFF3F2B96,
      groundColor = 0xFF2D3250,
      ambientParticles = "fireflies",
      unlockLevel = 7,
      iconEmoji = "🌙"
    )
  )

  fun getById(id: String): HabitatZone {
    return zones.firstOrNull { it.id == id } ?: zones.first()
  }
}

enum class ItemCategory(val label: String) {
  SHELTER("Shelters"),
  BED("Beds & Rugs"),
  FLORA("Trees & Flowers"),
  TOY("Toys & Play"),
  DECOR("Decorations")
}

data class DecorationItemType(
  val id: String,
  val name: String,
  val category: ItemCategory,
  val costCoins: Int,
  val costStars: Int = 0,
  val unlockLevel: Int = 1,
  val iconEmoji: String,
  val primaryColor: Long,
  val description: String
)

object DecorationCatalog {
  val items: List<DecorationItemType> = listOf(
    DecorationItemType("shelter_gazebo", "Pastel Gazebo", ItemCategory.SHELTER, 250, 0, 1, "🛖", 0xFFFFCDD2, "A shady pink wooden pavilion with climbing wisteria."),
    DecorationItemType("shelter_cottage", "Cozy Cottage", ItemCategory.SHELTER, 600, 5, 3, "🏠", 0xFFFFF59D, "A tiny sanctuary cabin with chimney smoke."),
    DecorationItemType("shelter_mushroom", "Mushroom Den", ItemCategory.SHELTER, 450, 3, 2, "🍄", 0xFFFF8A80, "A hollowed fairy mushroom with cozy warm lantern."),

    DecorationItemType("bed_cloud", "Fluffy Cloud Bed", ItemCategory.BED, 150, 0, 1, "☁️", 0xFFFFFFFF, "Plump soft cushion that feels like floating."),
    DecorationItemType("bed_strawberry", "Strawberry Mat", ItemCategory.BED, 200, 0, 1, "🍓", 0xFFFF5252, "Quilted berry cushion shaped like a giant ripe strawberry."),
    DecorationItemType("bed_basket", "Woven Twig Nest", ItemCategory.BED, 180, 0, 2, "🧺", 0xFFD7CCC8, "Lined with velvet moss and silk fleece."),

    DecorationItemType("flora_sakura", "Sakura Tree", ItemCategory.FLORA, 300, 2, 1, "🌸", 0xFFFF80AB, "Ever-blooming cherry blossom tree that sheds pink petals."),
    DecorationItemType("flora_daisy", "Giant Daisy", ItemCategory.FLORA, 100, 0, 1, "🌼", 0xFFFFF59D, "Enormous friendly sunflower and daisy bouquet."),
    DecorationItemType("flora_bamboo", "Bamboo Grove", ItemCategory.FLORA, 220, 0, 2, "🎋", 0xFF81C784, "Fresh emerald bamboo shoots that Pandas adore."),
    DecorationItemType("flora_glowshroom", "Bioluminescent Shroom", ItemCategory.FLORA, 350, 3, 3, "✨", 0xFFCE93D8, "Glows softly in twilight with calming purple hues."),

    DecorationItemType("toy_trampoline", "Mochi Trampoline", ItemCategory.TOY, 280, 0, 1, "🎪", 0xFF80DEEA, "Springy bouncy pad where animals flip happily."),
    DecorationItemType("toy_yarn", "Giant Yarn Ball", ItemCategory.TOY, 120, 0, 1, "🧶", 0xFFFF8A80, "Soft wool ball that kittens roll across the grass."),
    DecorationItemType("toy_fountain", "Bubble Fountain", ItemCategory.TOY, 400, 4, 3, "⛲", 0xFF81D4FA, "Shoots gentle rainbow soap bubbles into the air."),
    DecorationItemType("toy_wheel", "Hamster Big Wheel", ItemCategory.TOY, 200, 0, 2, "🎡", 0xFFFFD54F, "Silent spinny exercise wheel for small sanctuary pets."),

    DecorationItemType("decor_bridge", "Rainbow Bridge", ItemCategory.DECOR, 500, 6, 4, "🌈", 0xFFFFAB91, "An arching wooden bridge painted in gentle pastel tones."),
    DecorationItemType("decor_lantern", "Starlight Lantern", ItemCategory.DECOR, 160, 0, 1, "🏮", 0xFFFFE082, "Hangs from a curved shepherd hook to light the path."),
    DecorationItemType("decor_fence", "White Picket Fence", ItemCategory.DECOR, 90, 0, 1, "🪴", 0xFFFFFFFF, "Clean border fence to frame cozy gardens.")
  )

  fun getById(id: String): DecorationItemType {
    return items.firstOrNull { it.id == id } ?: items.first()
  }
}

data class PlacedDecoration(
  val id: String,
  val itemTypeId: String,
  val zoneId: String,
  val xRatio: Float,
  val yRatio: Float,
  val rotation: Float = 0f
)

data class OutfitItem(
  val id: String,
  val name: String,
  val isHat: Boolean,
  val costCoins: Int,
  val costStars: Int = 0,
  val iconEmoji: String,
  val color: Long,
  val description: String
)

object OutfitCatalog {
  val outfits: List<OutfitItem> = listOf(
    // Hats
    OutfitItem("hat_flower_crown", "Blossom Crown", true, 180, 0, "👑", 0xFFFF80AB, "Delicate wreath of tiny buttercups and cherry petals."),
    OutfitItem("hat_chef", "Chef Toque", true, 220, 0, "🧑‍🍳", 0xFFFFFFFF, "For talented little mochi bakers and treat makers."),
    OutfitItem("hat_straw", "Sunny Straw Hat", true, 150, 0, "👒", 0xFFFFE082, "Braided straw hat that shields gentle eyes from the sun."),
    OutfitItem("hat_beret", "Strawberry Beret", true, 200, 0, "🍓", 0xFFFF5252, "Chic red wool beret with a tiny green stem on top."),
    OutfitItem("hat_wizard", "Starry Wizard Hat", true, 350, 3, "🧙", 0xFF7E57C2, "Pointed indigo hat with floating cosmic stardust."),
    OutfitItem("hat_party", "Birthday Cone", true, 120, 0, "🎉", 0xFFFF4081, "Polka-dotted party hat with a fluffy yellow pom-pom."),
    OutfitItem("hat_nightcap", "Sweet Dreams Cap", true, 160, 0, "🌙", 0xFF90CAF9, "Droopy bedtime hat with a tiny sleeping moon bell."),

    // Accessories
    OutfitItem("acc_ribbon_bow", "Pastel Silk Bow", false, 140, 0, "🎀", 0xFFFF4081, "Fluffy satin bow worn right on the chest or neck."),
    OutfitItem("acc_scarf", "Cozy Knitted Scarf", false, 180, 0, "🧣", 0xFFFFAB91, "Soft wool scarf to stay snug during chilly morning strolls."),
    OutfitItem("acc_star_glasses", "Star Sunglasses", false, 250, 2, "⭐", 0xFFFFD700, "Golden star-shaped frames that make you look like a star."),
    OutfitItem("acc_angel_wings", "Pegasus Feather Wings", false, 500, 5, "🪽", 0xFFFFFFFF, "Tiny fluttering angel wings that shimmer in sunlight."),
    OutfitItem("acc_bell_collar", "Golden Bell Collar", false, 150, 0, "🔔", 0xFFFFD54F, "Tinkles with a soothing chime whenever the animal walks.")
  )

  fun getById(id: String): OutfitItem? {
    return outfits.firstOrNull { it.id == id }
  }
}
