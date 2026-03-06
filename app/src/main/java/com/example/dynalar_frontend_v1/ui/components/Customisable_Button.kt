package com.example.dynalar_frontend_v1.ui.components

import android.R.attr.text
import android.R.attr.title
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.dynalar_frontend_v1.R


//Boton de navegacion global
@Composable
fun Generic_Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ButtonPrimary,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/*Generic_Button(
  text = "Entrar",
   onClick = {
       login
   },
   modifier = Modifier.fillMaxWidth()
*/


//Circulo ( lo he utilizado para hacer el boton global
@Composable
fun CustomisableCircle(
    size: Dp = 24.dp,
    color: Color = ButtonPrimary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

//Botones que te permiten navegar(los del homePage)
@Composable
fun CustomisableRectangleButton(

    title: String,
    subtitle: String,
    width: Dp = 350.dp,
    height: Dp = 60.dp,
    circleColor: Color = Color.Green,
    elevation: Dp = 3.dp,
    cornerRadius: Dp = 15.dp,
    backgroundColor: Color = ButtonPrimary,
    onClick: () -> Unit = {}
) {

    val shape = RoundedCornerShape(cornerRadius)

    Box(

        modifier = Modifier
            .size(width, height)
            .shadow(elevation, shape)
            .clip(shape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp)) {

            CustomisableCircle(
                size = 50.dp,
                color = circleColor,
                modifier = Modifier
                    .offset(x = 5.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column{
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

            }

        }

    }
}

//Desplejable en un futuro se tiene que enlazar con la base de datos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomisableDynamicDropdownMenu(
    selectedItem: T?,
    options: List<T>,
    label: String,
    displayText: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        OutlinedTextField(
            readOnly = true,
            value = selectedItem?.let { displayText(it) } ?: "",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(displayText(item)) },
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    }
                )
            }
        }
    }
}

/*
//
Ejemplo de como usarlo

val treatments by viewModel.treatmentList.collectAsState()
val selectedTreatment by viewModel.selectedTreatment.collectAsState()

DynamicDropdownMenu(
    selectedItem = selectedTreatment,
    options = treatmentList,
    label = "Tratamiento odontológico",
    displayText = { it.name },
    onItemSelected = { treatment ->
        viewModel.onTreatmentSelected(treatment)
    }
)
)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    backgroundColor: Color = Color.Red,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false, // Solo deslizar de derecha a izquierda
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 8.dp) // Mismo padding que la Card
                    .clip(RoundedCornerShape(12.dp)) // Mismas esquinas que la Card (ajusta según necesites)
                    .background(backgroundColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Eliminar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }
    ) {
        // El contenido (la Card) se dibuja encima del fondo
        content()
    }
}

//Forma de arriba de los perfiles
@Composable
fun BannerGenericProfile(
    userName: String = "",
    userRole: String = "",
    profileImage: @Composable () -> Unit = {
        Image(
            painter = painterResource(id = R.drawable.usuario_hombre),
            contentDescription = "Imagen de perfil",
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    },
    content: @Composable ColumnScope.() -> Unit,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB2CBD2))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                profileImage()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            if (userRole.isNotEmpty()) {
                Text(
                    text = userRole,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp, vertical = 40.dp)
                        .verticalScroll(rememberScrollState()),
                    content = content
                )
            }
        }

        BackButton(
            onClick = onBackClick,
            iconRes = R.drawable.general_volver,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 50.dp)
        )
    }
}
//Ejemplo
/*@Composable
fun UserProfileScreen() {
    ProfileLayout(
        userName = user.name,
        userRole = "Usuario",
        profileImage = { /* imagen */ }
    ) {
        UserInfoContent(user)
    }
}*/

@Composable
fun BackButton(
    onClick: () -> Unit,
    iconRes: Int,
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Volver",
            modifier = Modifier.size(iconSize),
            contentScale = ContentScale.Fit
        )
    }
}
/* BackButton(
        onClick = onBackClick,
        iconRes = backIconRes
    )*/