package com.example.dynalar_frontend_v1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dynalar_frontend_v1.ui.theme.ButtonPrimary

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


/*CustomisableCircle(
size = 50.dp,
color = Color.Green
)*/



@Composable
fun CustomisableRectangle(
    width: Dp = 50.dp,
    height: Dp = 24.dp,
    color: Color = ButtonPrimary,
    elevation: Dp = 4.dp,
    cornerRadius: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape)
            .size(width = width, height = height)
            .clip(CircleShape)
            .background(color)
    )
}


/*CustomisableRectangle(
    width = 100.dp,
    height = 50.dp,
    elevation = 4.dp,
    cornerRadius = 12.dp
)*/

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
