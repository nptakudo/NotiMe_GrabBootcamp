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

private val Lato = FontFamily(
    Font(R.font.lato_thin, FontWeight.Thin),
    Font(R.font.lato_thinitalic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.lato_light, FontWeight.Light),
    Font(R.font.lato_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.lato_regular, FontWeight.Normal),
    Font(R.font.lato_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.lato_bold, FontWeight.SemiBold),
    Font(R.font.lato_bolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.lato_black, FontWeight.Bold),
    Font(R.font.lato_blackitalic, FontWeight.Bold, FontStyle.Italic),
)

private val defaultLatoTextStyle = TextStyle(
    fontFamily = Lato,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    ),
)

val buttonTextStyle = defaultLatoTextStyle.copy(
    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp
)

val PoppinsTypography = Typography(
    displayLarge = defaultLatoTextStyle.copy(
        fontSize = 48.sp, lineHeight = 50.sp, letterSpacing = 0.sp
    ),
    displayMedium = defaultLatoTextStyle.copy(
        fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = 0.sp
    ),
    displaySmall = defaultLatoTextStyle.copy(
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp
    ),
    headlineLarge = defaultLatoTextStyle.copy(
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    headlineMedium = defaultLatoTextStyle.copy(
        fontSize = 22.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    headlineSmall = defaultLatoTextStyle.copy(
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    titleLarge = defaultLatoTextStyle.copy(
        fontSize = 19.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    titleMedium = defaultLatoTextStyle.copy(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    titleSmall = defaultLatoTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.SemiBold,
        lineBreak = LineBreak.Heading
    ),
    labelLarge = defaultLatoTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.SemiBold
    ),
    labelMedium = defaultLatoTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.SemiBold
    ),
    labelSmall = defaultLatoTextStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = defaultLatoTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        lineBreak = LineBreak.Paragraph
    ),
    bodyMedium = defaultLatoTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        lineBreak = LineBreak.Paragraph
    ),
    bodySmall = defaultLatoTextStyle.copy(
        fontSize = 12.sp,
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
    val title = defaultLatoTextStyle.copy(
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
    val bodyResource = R.font.merriweather_regular
    val credit = defaultMerriweatherTextStyle.copy(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
        letterSpacing = 0.sp,
        lineHeight = 26.sp
    )
}