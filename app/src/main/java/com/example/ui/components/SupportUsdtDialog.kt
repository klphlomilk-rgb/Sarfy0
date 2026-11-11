package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.util.AppStrings
import com.example.ui.viewmodel.ExpenseViewModel

@Composable
fun SupportUsdtDialog(
    isProActivated: Boolean,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onActivateHonor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedNetwork by remember { mutableStateOf("TON") }
    var copiedRecently by remember { mutableStateOf(false) }

    val activeAddress = if (selectedNetwork == "TON") {
        ExpenseViewModel.TON_WALLET_ADDRESS
    } else {
        ExpenseViewModel.TRC20_WALLET_ADDRESS
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("support_dialog"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE91E63).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = strings.supportDialogTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Friendly message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .padding(14.dp)
                ) {
                    Text(
                        text = strings.supportDialogDesc,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Network Selector Tabs (TON as primary, TRC20 as alternative)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("TON" to "TON Network (USDT)", "TRC20" to "TRON (TRC20)").forEach { (networkId, label) ->
                        val isSelected = selectedNetwork == networkId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable {
                                    selectedNetwork = networkId
                                    copiedRecently = false
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // QR Code View
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CryptoQrCanvas(
                        dataString = activeAddress,
                        modifier = Modifier.size(136.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Address Card with One-Tap Copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable {
                            copyToClipboard(context, activeAddress, strings.copiedToast)
                            copiedRecently = true
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.walletAddressLabel(selectedNetwork),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeAddress,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (copiedRecently) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (copiedRecently) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = strings.copy,
                                tint = if (copiedRecently) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (copiedRecently) strings.copied else strings.copy,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                color = if (copiedRecently) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Honor Activation Button
                if (isProActivated) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.proActiveTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onActivateHonor,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("honor_activation_button")
                    ) {
                        Text(
                            text = strings.honorButton,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String, toastMessage: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("USDT Wallet", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}

@Composable
private fun CryptoQrCanvas(
    dataString: String,
    modifier: Modifier = Modifier
) {
    val matrixSize = 25
    val hash = dataString.hashCode()

    Canvas(modifier = modifier) {
        val cellSize = size.width / matrixSize
        val qrColor = Color(0xFF1E1E1E)

        // Draw Corner Finder Pattern (Top-Left)
        drawFinderPattern(0f, 0f, cellSize, qrColor)
        // Top-Right
        drawFinderPattern((matrixSize - 7) * cellSize, 0f, cellSize, qrColor)
        // Bottom-Left
        drawFinderPattern(0f, (matrixSize - 7) * cellSize, cellSize, qrColor)

        // Draw Matrix modules based on dataString
        for (row in 0 until matrixSize) {
            for (col in 0 until matrixSize) {
                val inTopLeft = row < 7 && col < 7
                val inTopRight = row < 7 && col >= matrixSize - 7
                val inBottomLeft = row >= matrixSize - 7 && col < 7
                if (!inTopLeft && !inTopRight && !inBottomLeft) {
                    val pseudoRandomBit = ((row * 31 + col * 17 + hash) % 3) == 0 ||
                            ((row + col) % 2 == 0 && (row * col + hash) % 5 == 0)
                    if (pseudoRandomBit) {
                        drawRoundRect(
                            color = qrColor,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize * 0.95f, cellSize * 0.95f),
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                    }
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFinderPattern(
    x: Float,
    y: Float,
    cellSize: Float,
    color: Color
) {
    drawRoundRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(7 * cellSize, 7 * cellSize),
        cornerRadius = CornerRadius(cellSize, cellSize),
        style = Stroke(width = cellSize)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(x + 2 * cellSize, y + 2 * cellSize),
        size = Size(3 * cellSize, 3 * cellSize),
        cornerRadius = CornerRadius(cellSize * 0.5f, cellSize * 0.5f)
    )
}
