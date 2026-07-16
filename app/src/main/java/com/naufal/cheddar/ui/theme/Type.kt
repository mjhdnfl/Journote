package com.naufal.cheddar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.naufal.cheddar.R

// 1. The Generator: This builds a specific Flex configuration.
// In the future, you can hook this up to read user SharedPreferences/DataStore!
@OptIn(ExperimentalTextApi::class)
private fun getFlexFontFamily(
    weight: Int,
    grade: Int,
    width: Float,
    roundness: Float,
    slant: Float = 0f
): FontFamily {
    return FontFamily(
        Font(
            resId = R.font.google_sans_flex,
            weight = FontWeight(weight),
            variationSettings = FontVariation.Settings(
                FontVariation.weight(weight),
                FontVariation.Setting("GRAD", grade.toFloat()),
                FontVariation.Setting("wdth", width),
                FontVariation.Setting("ROND", roundness),
                FontVariation.Setting("slnt", slant)
            )
        )
    )
}

// 2. The Typography Factory: Maps your 5 specific configurations to Material roles
fun getJournoteTypography(): Typography {

    // Group 1: Journote title
    val appTitleFont = getFlexFontFamily(weight = 700, grade = 100, width = 125f, roundness = 0f)

    // Group 2: Screen titles (Library, Settings, etc.)
    val screenTitleFont = getFlexFontFamily(weight = 600, grade = 100, width = 100f, roundness = 100f)

    // Group 3: Subheaders / Option Text
    val optionsFont = getFlexFontFamily(weight = 500, grade = 50, width = 100f, roundness = 100f)

    // Group 4: Regular text
    val regularFont = getFlexFontFamily(weight = 400, grade = 0, width = 100f, roundness = 100f)

    // Group 5: Note creation/edit titles
    val editorTitleFont = getFlexFontFamily(weight = 600, grade = 100, width = 105f, roundness = 100f)

    val baseline = Typography()

    return Typography(
        // Maps to Group 1: Used on EntriesScreen main title
        displayLarge = baseline.displayLarge.copy(fontFamily = appTitleFont),

        // Maps to Group 2: Used on the TopAppBar of all screens
        displayMedium = baseline.displayMedium.copy(fontFamily = screenTitleFont),
        displaySmall = baseline.displaySmall.copy(fontFamily = screenTitleFont),

        // Maps to Group 5: We assign this to Headline so you can use it in EditorScreen
        headlineLarge = baseline.headlineLarge.copy(fontFamily = editorTitleFont),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = editorTitleFont),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = editorTitleFont),

        // Maps to Group 3: Used for settings options, about screen labels, etc.
        titleLarge = baseline.titleLarge.copy(fontFamily = optionsFont),
        titleMedium = baseline.titleMedium.copy(fontFamily = optionsFont),
        titleSmall = baseline.titleSmall.copy(fontFamily = optionsFont),

        // Maps to Group 4: Standard body text and labels
        bodyLarge = baseline.bodyLarge.copy(fontFamily = regularFont),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = regularFont),
        bodySmall = baseline.bodySmall.copy(fontFamily = regularFont),
        labelLarge = baseline.labelLarge.copy(fontFamily = regularFont),
        labelMedium = baseline.labelMedium.copy(fontFamily = regularFont),
        labelSmall = baseline.labelSmall.copy(fontFamily = regularFont)
    )
}