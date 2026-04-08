package com.example.dynalar_frontend_v1.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dynalar_frontend_v1.model.odontogram.ProcessType
import com.example.dynalar_frontend_v1.model.odontogram.ToothSurface


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToothPage(number: Int, onBack: () -> Unit) {
    var selectedSurface by remember { mutableStateOf(ToothSurface.NONE) }
    var selectedProcess by remember { mutableStateOf(ProcessType.ABSENCIA_NATURAL) }

    val isBox = number in 11..13 || number in 21..23 ||
            number in 31..33 || number in 41..43 ||
            number in 51..53 || number in 61..63 ||
            number in 71..73 || number in 81..83

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(36.dp))
        Text("Dent numero $number", style = MaterialTheme.typography.titleMedium)

        Button(
            onClick = {  onBack() },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Volver")
        }

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            InteractiveTooth(
                number,
                isTrapezoid = !isBox,
                color = Color.Black,
                selectedSurface = selectedSurface,
                onSurfaceClick = { surface ->
                    selectedSurface = surface
                },
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { selectedSurface = ToothSurface.COMPLET }) {
                Text("Escollir Dent Complet (COMPLET)")
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (selectedSurface != ToothSurface.NONE) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Registrar Tratament",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Superficie: $selectedSurface",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        Text("Tipus de Procesus:", fontWeight = FontWeight.SemiBold)

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            FilterChip(
                                selected = selectedProcess == ProcessType.FET,
                                onClick = { selectedProcess = ProcessType.FET },
                                label = { Text(ProcessType.FET.name, color = Color.White) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFF0000)
                                )
                            )

                            FilterChip(
                                selected = selectedProcess == ProcessType.PER_FER,
                                onClick = { selectedProcess = ProcessType.PER_FER },
                                label = { Text("PER FER", color = Color.White) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0070C0)
                                )
                            )

                            FilterChip(
                                selected = selectedProcess == ProcessType.ABSENCIA_NATURAL,
                                onClick = { selectedProcess = ProcessType.ABSENCIA_NATURAL
                                            selectedSurface = ToothSurface.COMPLET},
                                label = { Text("ABSENCIA NATURAL", color = Color.White) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2D2D2D)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Seleccioni una Enfermetat:", fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button (
                            onClick = {
                                println("Desat: Superficie=${selectedSurface}, Procés=${selectedProcess}")
                                selectedSurface = ToothSurface.NONE
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Desar Tractament")
                        }
                    }
                }
            }
        }
    }
}
    @Composable
    fun InteractiveTooth(
        number: Int,
        isTrapezoid: Boolean,
        color: Color,
        selectedSurface: ToothSurface,
        onSurfaceClick: (ToothSurface) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier.aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isTrapezoid, number) {
                        detectTapGestures { tapOffset ->
                            val x = tapOffset.x
                            val y = tapOffset.y
                            val s = size.width.toFloat()

                            if (isTrapezoid) {
                                val innerScale = 0.5f
                                val innerSize = s * innerScale
                                val innerOffset = (s - innerSize) / 2f

                                if (x in innerOffset..(innerOffset + innerSize) && y in innerOffset..(innerOffset + innerSize)) {
                                    onSurfaceClick(ToothSurface.OCLUSAL)
                                    return@detectTapGestures
                                }
                            }

                            val isAbove1 = y < x
                            val isAbove2 = y < s - x

                            val isUpper = number in 11..18 || number in 21..28 ||
                                    number in 51..58 || number in 61..68

                            val isLeftPart = number in 21..28 || number in 31..38 ||
                                    number in 61..68 || number in 71..78

                            val clickedSurface = when {
                                isAbove1 && isAbove2   -> if (isUpper) ToothSurface.VESTIBULAR else ToothSurface.LINGUAL
                                !isAbove1 && !isAbove2 -> if (isUpper) ToothSurface.LINGUAL    else ToothSurface.VESTIBULAR
                                !isAbove1 && isAbove2  -> if (isLeftPart) ToothSurface.MESIAL  else ToothSurface.DISTAL
                                else                   -> if (isLeftPart) ToothSurface.DISTAL  else ToothSurface.MESIAL
                            }
                            onSurfaceClick(clickedSurface)
                        }
                    }
            ) {
                val s = size.width
                val borderStroke = s * 0.05f

                val oTL = Offset(0f, 0f)
                val oTR = Offset(s, 0f)
                val oBL = Offset(0f, s)
                val oBR = Offset(s, s)

                val pathTop = Path()
                val pathBottom = Path()
                val pathLeft = Path()
                val pathRight = Path()
                var pathCenter: Path? = null

                if (isTrapezoid) {
                    val innerScale = 0.5f
                    val innerSize = s * innerScale
                    val offset = (s - innerSize) / 2f

                    val iTL = Offset(offset, offset)
                    val iTR = Offset(offset + innerSize, offset)
                    val iBL = Offset(offset, offset + innerSize)
                    val iBR = Offset(offset + innerSize, offset + innerSize)

                    pathTop.apply { moveTo(oTL.x, oTL.y); lineTo(oTR.x, oTR.y); lineTo(iTR.x, iTR.y); lineTo(iTL.x, iTL.y); close() }
                    pathBottom.apply { moveTo(oBL.x, oBL.y); lineTo(oBR.x, oBR.y); lineTo(iBR.x, iBR.y); lineTo(iBL.x, iBL.y); close() }
                    pathLeft.apply { moveTo(oTL.x, oTL.y); lineTo(oBL.x, oBL.y); lineTo(iBL.x, iBL.y); lineTo(iTL.x, iTL.y); close() }
                    pathRight.apply { moveTo(oTR.x, oTR.y); lineTo(oBR.x, oBR.y); lineTo(iBR.x, iBR.y); lineTo(iTR.x, iTR.y); close() }
                    pathCenter = Path().apply { moveTo(iTL.x, iTL.y); lineTo(iTR.x, iTR.y); lineTo(iBR.x, iBR.y); lineTo(iBL.x, iBL.y); close() }
                } else {
                    val center = Offset(s / 2f, s / 2f)

                    pathTop.apply { moveTo(oTL.x, oTL.y); lineTo(oTR.x, oTR.y); lineTo(center.x, center.y); close() }
                    pathBottom.apply { moveTo(oBL.x, oBL.y); lineTo(oBR.x, oBR.y); lineTo(center.x, center.y); close() }
                    pathLeft.apply { moveTo(oTL.x, oTL.y); lineTo(oBL.x, oBL.y); lineTo(center.x, center.y); close() }
                    pathRight.apply { moveTo(oTR.x, oTR.y); lineTo(oBR.x, oBR.y); lineTo(center.x, center.y); close() }
                }

                val isUpperPart = number in 11..18 || number in 21..28 ||
                        number in 51..58 || number in 61..68

                val isLeftPart = number in 21..28 || number in 31..38 ||
                        number in 61..68 || number in 71..78

                fun drawSurface(path: Path, surface: ToothSurface) {
                    if (selectedSurface == ToothSurface.COMPLET || selectedSurface == surface) {
                        drawPath(path = path, color = Color.hsl(0.0F, 1.0F, 0.71F), style = Fill)
                    }
                    drawPath(path = path, color = color, style = Stroke(width = borderStroke / 2f))
                }

                drawSurface(pathTop,    if (isUpperPart) ToothSurface.VESTIBULAR else ToothSurface.LINGUAL)
                drawSurface(pathBottom, if (isUpperPart) ToothSurface.LINGUAL    else ToothSurface.VESTIBULAR)
                drawSurface(pathLeft,   if (isLeftPart) ToothSurface.MESIAL  else ToothSurface.DISTAL)
                drawSurface(pathRight,  if (isLeftPart) ToothSurface.DISTAL  else ToothSurface.MESIAL)

                if (isTrapezoid && pathCenter != null) {
                    drawSurface(pathCenter, ToothSurface.OCLUSAL)
                }

                drawRect(color = color, style = Stroke(width = borderStroke))
            }
        } }