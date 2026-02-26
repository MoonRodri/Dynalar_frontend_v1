package com.example.dynalar_frontend_v1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with

val Typography = Typography(

    //Nombre de usuarios...
    //Grande
    titleMedium = TextStyle(
        fontFamily = AGBookRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),

    //Tipografia Cuerpo (Generico)
    //Normal
    bodyLarge = TextStyle(
        fontFamily = RegularFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    //Respuestas a lo pedido del usuarioo/ calendario(dentro de los boxes)
    //Pequeño Grande
    bodyMedium = TextStyle(
        fontFamily = AGBookRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    //Nombres de usuario Pequeño
    //Pequeño Normal
    titleSmall = TextStyle(
        fontFamily = AGBookRounded,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    //Pop ups//Caldendario
    //Pequeño Elegante
    labelLarge = TextStyle(
        fontFamily = AbyssinicaSIL,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )

)