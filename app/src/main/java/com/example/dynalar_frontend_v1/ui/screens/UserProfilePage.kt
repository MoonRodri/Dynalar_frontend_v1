package com.example.dynalar_frontend_v1.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynalar_frontend_v1.ui.components.BannerGenericProfile
import com.example.dynalar_frontend_v1.ui.components.InputField
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.ui.theme.Dynalar_frontend_v1Theme
import com.example.dynalar_frontend_v1.R


@Composable
fun UserProfilePage(
    onBackClick: () -> Unit = {}
) {

    BannerGenericProfile(
        userName = "Cassius Tedesco",
        userRole = "Usuario",

        profileImage = {
            Image(
                painter = painterResource(R.drawable.usuario_hombre),
                contentDescription = "Profile",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        },
        onBackClick = onBackClick,

        content = {
            UserInfoContent()
        }
    )
}



@Composable
fun UserInfoContent(){
    Column(verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()) {
        InputField(
            label = "Nombre",
            value = "Cassius")//se cambiara esto
        InputField(
            label = "Apellido",
            value = "Tedesco")
        InputField(
            label = "DNI",
            value = "12345678A")
        InputField(
            label = "Email",
            value = "cassiustedesco@gmail.com")

        Row(horizontalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .padding(top = 22.dp, end = 5.dp)
                    .height(48.dp)
                    .width(75.dp)
                    .background(ButtonPrimary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+34",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            InputField(label = "Teléfono", value = "1234567")
        }

    }
}



@Preview(showBackground = true)
@Composable
fun UserProfilePagePreview() {
    Dynalar_frontend_v1Theme {
        UserProfilePage()
    }
}
