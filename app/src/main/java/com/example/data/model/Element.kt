package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class ElementCategory(val persianTitle: String, val color: Color) {
    AlkaliMetal("فلز قلیایی", CatAlkaliMetal),
    AlkalineEarth("فلز قلیایی خاکی", CatAlkalineEarth),
    TransitionMetal("فلز واسطه", CatTransitionMetal),
    PostTransitionMetal("فلز پس‌واسطه", CatPostTransitionMetal),
    Metalloid("شبه‌فلز", CatMetalloid),
    ReactiveNonmetal("نافلز واکنش‌پذیر", CatReactiveNonmetal),
    Halogen("هالوژن", CatHalogen),
    NobleGas("گاز نجیب", CatNobleGas),
    Lanthanide("لانتانید", CatLanthanide),
    Actinide("آکتینید", CatActinide),
    Unknown("نامشخص", CatUnknown)
}

enum class ElementState(val persianTitle: String) {
    Solid("جامد"),
    Liquid("مایع"),
    Gas("گاز"),
    Synthetic("ساختگی")
}

data class Element(
    val atomicNumber: Int,
    val symbol: String,
    val persianName: String,
    val englishName: String,
    val atomicMass: Double,
    val period: Int,
    val group: Int,
    val block: String,
    val category: ElementCategory,
    val electronConfiguration: String,
    val electronegativity: Double?,
    val atomicRadiusPm: Int?,
    val meltingPointK: Double?,
    val boilingPointK: Double?,
    val stateAt298K: ElementState,
    val valenceElectrons: Int,
    val oxidationStates: String,
    val konkurTipsAndUses: String
)
