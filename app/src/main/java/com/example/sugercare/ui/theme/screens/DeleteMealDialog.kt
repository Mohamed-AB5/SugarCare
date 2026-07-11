package com.example.sugercare.ui.screens


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sugercare.ui.theme.*
/**
 * Confirmation dialog shown before deleting a meal.
 *
 * @param mealName  Shown in the message so the user knows what will be deleted.
 * @param onDismiss Called when the user taps Cancel.
 * @param onConfirm Called when the user taps Delete.
 */
@Composable
fun DeleteMealDialog(
    mealName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceWhite,
        shape            = RoundedCornerShape(20.dp),
        title = {
            Text(
                text       = "Delete Meal",
                fontWeight = FontWeight.Bold,
                color      = TextDark
            )
        },
        text = {
            Text(
                text  = "Are you sure you want to delete \"$mealName\"?",
                color = TextMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape   = RoundedCornerShape(24.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape   = RoundedCornerShape(24.dp),
                border  = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary)
            ) {
                Text("Cancel", color = TealPrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
