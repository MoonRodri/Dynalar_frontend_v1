package com.example.dynalar_frontend_v1.ui.components

import android.R.attr.text
import android.R.attr.title
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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




//Boton de navegacion global
@Composable
fun Customisable_Button(
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

/*Customisable_Button(
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
