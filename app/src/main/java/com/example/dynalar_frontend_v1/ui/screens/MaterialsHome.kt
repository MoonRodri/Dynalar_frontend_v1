package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dynalar_frontend_v1.R
import com.example.dynalar_frontend_v1.ui.components.CustomTopBar
import com.example.dynalar_frontend_v1.ui.components.CustomisableButtonMaterials

@Composable
fun MaterialsHome(
    onNavigateBack: () -> Unit,
    onNavigateStock: () -> Unit,
    onNavigateProtocolo: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // 1. Margen superior real
        Spacer(modifier = Modifier.height(27.dp))

        // 2. La barra superior
        CustomTopBar(
            title = "Gestió de Materials",
            onNavigateBack = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )


        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Buttons_MaterialsPage(
                onNavigateStock = onNavigateStock,
                onNavigateProtocolo = onNavigateProtocolo
            )
        }
    }
}

@Composable
fun Buttons_MaterialsPage(
    modifier: Modifier = Modifier,
    onNavigateStock: () -> Unit,
    onNavigateProtocolo: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp) // Espacio elegante entre botones
    ) {
        CustomisableButtonMaterials(
            iconRes = R.drawable.stock,
            title = "Stock Materials",
            subtitle = "Gestión de inventario stock",
            onClick = onNavigateStock
        )

        Spacer(modifier = Modifier.height(60.dp))

        CustomisableButtonMaterials(
            iconRes = R.drawable.protocolo,
            title = "Protocol",
            subtitle = "Materiales de dentista",
            onClick = onNavigateProtocolo
        )
    }
}