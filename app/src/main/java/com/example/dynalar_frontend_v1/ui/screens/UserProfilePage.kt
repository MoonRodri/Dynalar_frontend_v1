package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.ui.components.BannerGenericProfile
import com.example.dynalar_frontend_v1.ui.theme.AbyssinicaSIL
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import com.example.dynalar_frontend_v1.ui.theme.Dynalar_frontend_v1Theme
import com.example.dynalar_frontend_v1.ui.theme.SecondaryBg
import kotlin.math.round

@Composable
fun UserProfilePage(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    BannerGenericProfile(
        userName = "Cassius Tedesco",
        onBackClick = onBackClick,
        content = {UserInfoContent()}
    )
}

@Composable
fun UserInfoContent(){
    Column(verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()) {
        InputField(label = "Nombre", value = "Cassius")
        InputField(label = "Apellido", value = "Tedesco")
        InputField(label = "DNI", value = "12345678A")
        InputField(label = "Email", value = "cassiustedesco@gmail.com")
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


@Composable
fun InputField(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: String
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, SolidColor(Color.LightGray.copy(alpha = 0.4f)))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = value,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                )
            }
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
