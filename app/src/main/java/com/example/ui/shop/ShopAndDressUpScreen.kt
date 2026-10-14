package com.example.ui.shop

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.DecorationCatalog
import com.example.data.model.GameState
import com.example.data.model.ItemCategory
import com.example.data.model.OutfitCatalog
import com.example.data.model.OutfitItem
import com.example.data.model.SpeciesCatalog
import com.example.ui.art.MochiAnimalView
import com.example.ui.theme.MochiButterYellow
import com.example.ui.theme.MochiDarkBrown
import com.example.ui.theme.MochiLavender
import com.example.ui.theme.MochiSakuraDeep
import com.example.ui.theme.MochiSakuraLight
import com.example.ui.theme.MochiSakuraPink
import com.example.ui.theme.MochiSoftBrown

@Composable
fun ShopAndDressUpScreen(
  gameState: GameState,
  onBuyDecoration: (String) -> Boolean,
  onBuyOutfit: (String) -> Boolean,
  onEquipOutfit: (animalId: String, outfitId: String, isHat: Boolean) -> Unit,
  onUnequipOutfit: (animalId: String, isHat: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Shop, 1 = Dress-Up

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(MochiSakuraLight, MochiLavender.copy(alpha = 0.3f), Color.White)
        )
      )
  ) {
    // Top Tabs
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
        text = { Text("Sanctuary Shop 🛍️", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = {
          SoundManager.playPop()
          selectedTab = 1
        },
        text = { Text("Dress-Up Studio 👗", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
      )
    }

    if (selectedTab == 0) {
      SanctuaryShopContent(
        gameState = gameState,
        onBuyDecoration = onBuyDecoration,
        onBuyOutfit = onBuyOutfit
      )
    } else {
      DressUpStudioContent(
        gameState = gameState,
        onEquipOutfit = onEquipOutfit,
        onUnequipOutfit = onUnequipOutfit
      )
    }
  }
}

@Composable
fun SanctuaryShopContent(
  gameState: GameState,
  onBuyDecoration: (String) -> Boolean,
  onBuyOutfit: (String) -> Boolean
) {
  var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // Categories filter chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      item {
        FilterChipItem(
          label = "All Items",
          isSelected = selectedCategory == null,
          onClick = { selectedCategory = null }
        )
      }
      items(ItemCategory.values()) { cat ->
        FilterChipItem(
          label = cat.label,
          isSelected = selectedCategory == cat,
          onClick = { selectedCategory = cat }
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Decor Items
    Text("Sanctuary Furniture & Decor", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
    Spacer(modifier = Modifier.height(8.dp))

    val decorList = DecorationCatalog.items.filter {
      selectedCategory == null || it.category == selectedCategory
    }

    decorList.forEach { item ->
      val isOwned = gameState.ownedDecorationIds.contains(item.id)
      val canAfford = gameState.playerCoins >= item.costCoins && gameState.playerStars >= item.costStars

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MochiSakuraLight),
            contentAlignment = Alignment.Center
          ) {
            Text(item.iconEmoji, fontSize = 26.sp)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
            Text(item.description, fontSize = 10.sp, color = MochiSoftBrown, lineHeight = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
              Text("🪙 ${item.costCoins}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MochiDarkBrown)
              if (item.costStars > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Text("⭐ ${item.costStars}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MochiSakuraDeep)
              }
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = { onBuyDecoration(item.id) },
            enabled = !isOwned && canAfford,
            colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(34.dp).testTag("buy_decor_${item.id}")
          ) {
            Text(
              if (isOwned) "Owned" else "Buy",
              fontSize = 11.sp,
              color = Color.White
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Outfit Accessories to buy
    Text("Animal Hats & Outfits", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
    Spacer(modifier = Modifier.height(8.dp))

    OutfitCatalog.outfits.forEach { outfit ->
      val isOwned = gameState.ownedOutfitIds.contains(outfit.id)
      val canAfford = gameState.playerCoins >= outfit.costCoins && gameState.playerStars >= outfit.costStars

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MochiLavender.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
          ) {
            Text(outfit.iconEmoji, fontSize = 24.sp)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(outfit.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
            Text(outfit.description, fontSize = 10.sp, color = MochiSoftBrown, lineHeight = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
              Text("🪙 ${outfit.costCoins}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MochiDarkBrown)
              if (outfit.costStars > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Text("⭐ ${outfit.costStars}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MochiSakuraDeep)
              }
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = { onBuyOutfit(outfit.id) },
            enabled = !isOwned && canAfford,
            colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraDeep),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(34.dp).testTag("buy_outfit_${outfit.id}")
          ) {
            Text(
              if (isOwned) "Owned" else "Buy",
              fontSize = 11.sp,
              color = Color.White
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
fun DressUpStudioContent(
  gameState: GameState,
  onEquipOutfit: (animalId: String, outfitId: String, isHat: Boolean) -> Unit,
  onUnequipOutfit: (animalId: String, isHat: Boolean) -> Unit
) {
  var selectedPetId by remember {
    mutableStateOf(gameState.activeAnimalId.ifEmpty { gameState.adoptedAnimals.firstOrNull()?.id ?: "" })
  }

  val currentPet = gameState.adoptedAnimals.firstOrNull { it.id == selectedPetId }

  if (currentPet == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Adopt an animal first to dress them up! 🌸", color = MochiDarkBrown)
    }
    return
  }

  val species = SpeciesCatalog.getById(currentPet.speciesId)

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Pet Carousel to pick who to dress up
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(gameState.adoptedAnimals) { pet ->
        val isSel = pet.id == selectedPetId
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (isSel) MochiSakuraDeep else Color.White,
          modifier = Modifier.clickable {
            SoundManager.playPop()
            selectedPetId = pet.id
          }
        ) {
          Text(
            text = pet.customName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSel) Color.White else MochiDarkBrown,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Big Live Model Showcase Card
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(MochiSakuraLight),
          contentAlignment = Alignment.Center
        ) {
          MochiAnimalView(
            species = species,
            sizeDp = 130.dp,
            equippedHat = currentPet.equippedHat,
            equippedAccessory = currentPet.equippedAccessory,
            isExcited = true
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(currentPet.customName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
          if (currentPet.equippedHat != null) {
            Button(
              onClick = { onUnequipOutfit(currentPet.id, true) },
              colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraLight),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Text("Remove Hat", fontSize = 10.sp, color = MochiDarkBrown)
            }
          }
          if (currentPet.equippedAccessory != null) {
            Button(
              onClick = { onUnequipOutfit(currentPet.id, false) },
              colors = ButtonDefaults.buttonColors(containerColor = MochiSakuraLight),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Text("Remove Accessory", fontSize = 10.sp, color = MochiDarkBrown)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text("Wardrobe Inventory (Owned Outfits)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
    Spacer(modifier = Modifier.height(8.dp))

    val ownedOutfitsList = OutfitCatalog.outfits.filter { gameState.ownedOutfitIds.contains(it.id) }

    if (ownedOutfitsList.isEmpty()) {
      Text("Visit the Shop tab to buy your first hats and bows! 🎀", fontSize = 11.sp, color = MochiSoftBrown)
    } else {
      ownedOutfitsList.forEach { outfit ->
        val isEquipped = (outfit.isHat && currentPet.equippedHat == outfit.id) ||
                         (!outfit.isHat && currentPet.equippedAccessory == outfit.id)

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(outfit.iconEmoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(outfit.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MochiDarkBrown)
              Text(if (outfit.isHat) "Headwear" else "Accessory", fontSize = 10.sp, color = MochiSoftBrown)
            }
            Button(
              onClick = {
                if (isEquipped) {
                  onUnequipOutfit(currentPet.id, outfit.isHat)
                } else {
                  onEquipOutfit(currentPet.id, outfit.id, outfit.isHat)
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isEquipped) MochiSakuraLight else MochiSakuraDeep
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text(
                if (isEquipped) "Unequip" else "Equip ✨",
                fontSize = 11.sp,
                color = if (isEquipped) MochiDarkBrown else Color.White
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
private fun FilterChipItem(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) MochiSakuraDeep else Color.White,
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = if (isSelected) Color.White else MochiDarkBrown,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
  }
}
