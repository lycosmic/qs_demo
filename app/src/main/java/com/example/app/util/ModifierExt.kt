package com.example.app.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * 无水波纹点击
 */
@Composable
fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClick: () -> Unit
) = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    enabled = enabled,
    onClick = onClick
)