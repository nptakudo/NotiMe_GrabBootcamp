package com.example.frontend.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.example.frontend.R

private val Poppins = FontFamily(
    Font(R.font.poppins_thin, FontWeight.Thin),
    Font(R.font.poppins_thin_italic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.poppins_extralight, FontWeight.ExtraLight),
    Font(R.font.poppins_extralight_italic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
    Font(R.font.poppins_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),
)

private val Merriweather = FontFamily(
    Font(R.font.merriweather_light, FontWeight.Light),
    Font(R.font.merriweather_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.merriweather_regular, FontWeight.Normal),
    Font(R.font.merriweather_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.merriweather_bold, FontWeight.Bold),
    Font(R.font.merriweather_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.merriweather_black, FontWeight.Black),
    Font(R.font.merriweather_black_italic, FontWeight.Black, FontStyle.Italic),
)

private val defaultPoppinsTextStyle = TextStyle(
    fontFamily = Poppins,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    ),
)

val buttonTextStyle = defaultPoppinsTextStyle.copy(
    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp
)

val PoppinsTypography = Typography(
    displayLarge = defaultPoppinsTextStyle.copy(
        fontSize = 48.sp, lineHeight = 50.sp, letterSpacing = 0.sp
    ),
    displayMedium = defaultPoppinsTextStyle.copy(
        fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = 0.sp
    ),
    displaySmall = defaultPoppinsTextStyle.copy(
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp
    ),
    headlineLarge = defaultPoppinsTextStyle.copy(
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    headlineMedium = defaultPoppinsTextStyle.copy(
        fontSize = 22.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    headlineSmall = defaultPoppinsTextStyle.copy(
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    titleLarge = defaultPoppinsTextStyle.copy(
        fontSize = 19.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    titleMedium = defaultPoppinsTextStyle.copy(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    titleSmall = defaultPoppinsTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    labelLarge = defaultPoppinsTextStyle.copy(
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp, fontWeight = FontWeight.Medium
    ),
    labelMedium = defaultPoppinsTextStyle.copy(
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium
    ),
    labelSmall = defaultPoppinsTextStyle.copy(
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium
    ),
    bodyLarge = defaultPoppinsTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        lineBreak = LineBreak.Paragraph
    ),
    bodyMedium = defaultPoppinsTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        lineBreak = LineBreak.Paragraph
    ),
    bodySmall = defaultPoppinsTextStyle.copy(
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        lineBreak = LineBreak.Paragraph
    ),
)

object ReaderTextStyle {
    private val defaultMerriweatherTextStyle = TextStyle(
        fontFamily = Merriweather,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None
        ),
    )
    val title = defaultPoppinsTextStyle.copy(
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
        lineBreak = LineBreak.Heading,
        lineHeight = 40.sp
    )
    val body = defaultMerriweatherTextStyle.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        lineBreak = LineBreak.Paragraph,
        lineHeight = 30.sp
    )
    val credit = defaultMerriweatherTextStyle.copy(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
        letterSpacing = 0.sp,
        lineHeight = 26.sp
    )
}