package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dynalar_frontend_v1.model.odontogram.ProcessType
import com.example.dynalar_frontend_v1.model.odontogram.ToothSurface

enum class OdontogramState {
    FULL, VERTICAL, QUADRANT
}

fun getTeethForQuadrant(quadrantId: Int): List<Int> {
    return when (quadrantId) {
        1 -> listOf(18, 17, 16, 15, 14, 13, 12, 11)
        2 -> listOf(21, 22, 23, 24, 25, 26, 27, 28)
        3 -> listOf(31, 32, 33, 34, 35, 36, 37, 38)
        4 -> listOf(48, 47, 46, 45, 44, 43, 42, 41)
        else -> emptyList()
    }
}

fun getTemporalTeethForQuadrant(quadrantId: Int): List<Int> {
    return when (quadrantId) {
        1 -> listOf(55, 54, 53, 52, 51)
        2 -> listOf(61, 62, 63, 64, 65)
        3 -> listOf(71, 72, 73, 74, 75)
        4 -> listOf(85, 84, 83, 82, 81)
        else -> emptyList()
    }
}


@Composable
fun OdontogramPage(onToothSelected: (Int) -> Unit) {
    var currentState by remember { mutableStateOf(OdontogramState.FULL) }
    var selectedQuadrant by remember { mutableStateOf<Int?>(null) }
    var quadrantName by remember { mutableStateOf("") }

    when (selectedQuadrant){
        1 -> quadrantName = "Superior Derecho"
        2 -> quadrantName = "Superior Izquierdo"
        3 -> quadrantName = "Inferior Derecho"
        4 -> quadrantName = "Inferior Izquierdo"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        AnimatedContent(
            targetState = currentState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "title"
        ) { state ->
            Text(
                text = when (state) {
                    OdontogramState.FULL     -> "Odontograma"
                    OdontogramState.VERTICAL -> "Odontograma"
                    OdontogramState.QUADRANT -> "Cuadrante ${quadrantName ?: ""}"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentState,
                transitionSpec = {
                    if (targetState == OdontogramState.QUADRANT) {
                        (scaleIn(
                            initialScale = 0.6f,
                            animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(400))) togetherWith
                                (scaleOut(
                                    targetScale = 1.4f,
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300)))
                    } else {
                        (scaleIn(
                            initialScale = 1.4f,
                            animationSpec = tween(400)
                        ) + fadeIn(animationSpec = tween(400))) togetherWith
                                (scaleOut(
                                    targetScale = 0.6f,
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300)))
                    }
                },
                label = "odontogramContent"
            ) { state ->
                when (state) {
                    OdontogramState.FULL -> {
                        OdontogramGeneralView(
                            onQuadrantClick = { quadrant ->
                                selectedQuadrant = quadrant
                                currentState = OdontogramState.QUADRANT
                            }
                        )
                    }
                    OdontogramState.VERTICAL->{
                        OdontogramVerticalView(onQuadrantClick = { quadrant ->
                            selectedQuadrant = quadrant
                            currentState = OdontogramState.QUADRANT
                        })
                    }

                    OdontogramState.QUADRANT -> {
                        QuadrantDetailView(
                            quadrantId = selectedQuadrant ?: 1,
                            onToothClick = { tooth ->
                                onToothSelected(tooth)
                            }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = currentState == OdontogramState.FULL,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                Button(
                    onClick = { currentState = OdontogramState.VERTICAL },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Ver Verticalmente")
                }

                Button(
                    onClick = { },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Ver Registros")
                }
            }
        }

        AnimatedVisibility(
            visible = currentState != OdontogramState.FULL,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                Button(
                    onClick = { currentState = OdontogramState.FULL },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Ver complet")
                }

                Button(
                    onClick = { },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Ver Registros")
                }
            }

        }
    }
}

@Composable
fun OdontogramVerticalView(onQuadrantClick: (Int) -> Unit) {
    val quadrantBackgroundColor = Color(0xFFF5F5F5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = quadrantBackgroundColor, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Superior Derecho", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            QuadrantTeethBlock(
                permanentTeeth = getTeethForQuadrant(1),
                temporalTeeth = getTemporalTeethForQuadrant(1),
                isUpper = true,
                toothSize = 32,
                temporalToothSize = 28,
                numberSize = 12,
                toothSpacing = 8,
                onClick = { onQuadrantClick(1) }
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = quadrantBackgroundColor, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Superior Izquierdo", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            QuadrantTeethBlock(
                permanentTeeth = getTeethForQuadrant(2),
                temporalTeeth = getTemporalTeethForQuadrant(2),
                toothSize = 32,
                temporalToothSize = 28,
                numberSize = 12,
                isUpper = true,
                toothSpacing = 8,
                onClick = { onQuadrantClick(2) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = quadrantBackgroundColor, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Inferior Derecho", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            QuadrantTeethBlock(
                permanentTeeth = getTeethForQuadrant(4),
                temporalTeeth = getTemporalTeethForQuadrant(4),
                isUpper = false,
                toothSize = 32,
                temporalToothSize = 28,
                numberSize = 12,
                toothSpacing = 8,
                onClick = { onQuadrantClick(4) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = quadrantBackgroundColor, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Inferior Izquierdo", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            QuadrantTeethBlock(
                permanentTeeth = getTeethForQuadrant(3),
                temporalTeeth = getTemporalTeethForQuadrant(3),
                isUpper = false,
                toothSize = 32,
                temporalToothSize = 28,
                numberSize = 12,
                toothSpacing = 8,
                onClick = { onQuadrantClick(3) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }


@Composable
fun OdontogramGeneralView(onQuadrantClick: (Int) -> Unit) {
    val quadrantBackgroundColor = Color(0xFFF5F5F5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // 2. Añadido weight(1f) para que ocupe la mitad del alto
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight() // 3. Se estira hasta llenar su Row
                    .background(color = quadrantBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // 4. Centra el contenido (dientes y texto)
            ) {
                Text("Superior Derecho", fontSize = 10.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                QuadrantTeethBlock(
                    permanentTeeth = getTeethForQuadrant(1),
                    temporalTeeth = getTemporalTeethForQuadrant(1),
                    isUpper = true,
                    onClick = { onQuadrantClick(1) }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color = quadrantBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Superior Izquierdo", fontSize = 10.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                QuadrantTeethBlock(
                    permanentTeeth = getTeethForQuadrant(2),
                    temporalTeeth = getTemporalTeethForQuadrant(2),
                    isUpper = true,
                    onClick = { onQuadrantClick(2) }
                )
            }
        }


        // --- FILA INFERIOR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // 2. Añadido weight(1f) para la otra mitad del alto
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight() // Se estira
                    .background(color = quadrantBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Centra
            ) {
                QuadrantTeethBlock(
                    permanentTeeth = getTeethForQuadrant(4),
                    temporalTeeth = getTemporalTeethForQuadrant(4),
                    isUpper = false,
                    onClick = { onQuadrantClick(4) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Inferior Derecho", fontSize = 10.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight() // Se estira
                    .background(color = quadrantBackgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Centra
            ) {
                QuadrantTeethBlock(
                    permanentTeeth = getTeethForQuadrant(3),
                    temporalTeeth = getTemporalTeethForQuadrant(3),
                    isUpper = false,
                    onClick = { onQuadrantClick(3) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Inferior Izquierdo", fontSize = 10.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun QuadrantTeethBlock(
    permanentTeeth: List<Int>,
    temporalTeeth: List<Int>,
    isUpper: Boolean,
    toothSize: Int = 18,
    temporalToothSize: Int = 16,
    numberSize: Int = 10,
    toothSpacing: Int = 0,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUpper) {
            Row (horizontalArrangement = Arrangement.spacedBy(toothSpacing.dp)){
                permanentTeeth.forEach { n ->
                    MiniToothItem(number = n, toothSize = toothSize, numberSize)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row (horizontalArrangement = Arrangement.spacedBy(toothSpacing.dp)){
                temporalTeeth.forEach { n ->
                    MiniToothItem(number = n, toothSize = temporalToothSize, numberSize)
                }
            }
        } else {
            Row (horizontalArrangement = Arrangement.spacedBy(toothSpacing.dp)){
                temporalTeeth.forEach { n ->
                    MiniToothItem(number = n, toothSize = temporalToothSize, numberSize)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row (horizontalArrangement = Arrangement.spacedBy(toothSpacing.dp)){
                permanentTeeth.forEach { n ->
                    MiniToothItem(number = n, toothSize = toothSize, numberSize)
                }
            }
        }
    }
}

@Composable
fun MiniToothItem(number: Int, toothSize: Int, numberSize: Int = 10) {
    val isBox = number in 11..13 || number in 21..23 ||
            number in 31..33 || number in 41..43 ||
            number in 51..53 || number in 61..63 ||
            number in 71..73 || number in 81..83
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 1.dp)
    ) {
        Canvas(modifier = Modifier.size(toothSize.dp)) {
            val s = size.width
            val stroke = s * 0.08f
            val color = Color.Black

            if (!isBox) {
                val innerScale = 0.5f
                val innerSize = s * innerScale
                val offset = (s - innerSize) / 2f
                drawRect(color = color, style = Stroke(width = stroke))
                drawRect(
                    color = color,
                    topLeft = Offset(offset, offset),
                    size = androidx.compose.ui.geometry.Size(innerSize, innerSize),
                    style = Stroke(width = stroke)
                )
                drawLine(color, Offset(0f, 0f), Offset(offset, offset), stroke)
                drawLine(color, Offset(s, 0f), Offset(offset + innerSize, offset), stroke)
                drawLine(color, Offset(0f, s), Offset(offset, offset + innerSize), stroke)
                drawLine(color, Offset(s, s), Offset(offset + innerSize, offset + innerSize), stroke)
            } else {
                val center = Offset(s / 2f, s / 2f)
                drawRect(color = color, style = Stroke(width = stroke))
                drawLine(color, Offset(0f, 0f), center, stroke)
                drawLine(color, Offset(s, 0f), center, stroke)
                drawLine(color, Offset(0f, s), center, stroke)
                drawLine(color, Offset(s, s), center, stroke)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text("$number", fontSize = numberSize.sp, color = Color.Red)
    }
}

@Composable
fun QuadrantDetailView(
    quadrantId: Int,
    onToothClick: (Int) -> Unit
) {
    val permanentTeeth = getTeethForQuadrant(quadrantId)
    val temporalTeeth = getTemporalTeethForQuadrant(quadrantId)
    val isUpper = quadrantId == 1 || quadrantId == 2

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Selecciona un diente",
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isUpper) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                permanentTeeth.forEach { n ->
                    ToothDetailItem(number = n, toothSize = 38, onToothClick = onToothClick)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                temporalTeeth.forEach { n ->
                    ToothDetailItem(number = n, toothSize = 34, onToothClick = onToothClick)
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                temporalTeeth.forEach { n ->
                    ToothDetailItem(number = n, toothSize = 34, onToothClick = onToothClick)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                permanentTeeth.forEach { n ->
                    ToothDetailItem(number = n, toothSize = 38, onToothClick = onToothClick)
                }
            }
        }
    }
}

@Composable
fun ToothDetailItem(number: Int, toothSize: Int, onToothClick: (Int) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(number) {
        kotlinx.coroutines.delay((number % 10) * 40L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(initialScale = 0.3f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )) + fadeIn()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onToothClick(number) }
                .padding(4.5.dp)
        ) {
            Text("$number", fontSize = 12.sp    , color = Color.Red, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            InteractiveTooth(
                number = number,
                isTrapezoid = !(number in 11..13 || number in 21..23 ||
                        number in 31..33 || number in 41..43 ||
                        number in 51..53 || number in 61..63 ||
                        number in 71..73 || number in 81..83),
                color = Color.Black,
                selectedSurface = ToothSurface.NONE,
                onSurfaceClick = { onToothClick(number) },
                modifier = Modifier.size(toothSize.dp)
            )
        }
    }
}