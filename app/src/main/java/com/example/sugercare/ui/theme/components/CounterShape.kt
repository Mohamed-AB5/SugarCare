package com.example.sugercare.ui.theme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CounterShape(
    currentDays: Int = 0,
    totalDays: Int = 90
) {
    // Calculate the fill ratio (0.0f to 1.0f)
    val fillRatio = (currentDays.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)

    // Define colors matching your UI
    val backgroundColor = Color(0xFFD6F3F5) // Soft light cyan background
    val fillInColor = Color(0xFF00C3D7)     // Deep cyan progress color

    Canvas(modifier = Modifier.size(200.dp, 240.dp)) {
        val width = size.width
        val height = size.height

        // 1. Create or parse your custom "90" path.
        // Replace this string with your actual SVG path string for the "90" graphic.
        // FIX: Do not use .toString() on an R.drawable ID. Paste the actual SVG 'd' string here.
        val pathString = "M213.21,132.61C216.27,135.28 219.14,138.11 222,141C222.81,141.8 223.63,142.61 224.46,143.43C230.67,149.89 234.91,157.12 239,165C239.69,166.31 239.69,166.31 240.4,167.64C248.79,184.65 251.55,203.39 254,222C254.11,221.46 254.23,220.93 254.35,220.37C259.96,194.55 268.65,170.35 286,150C287.19,148.57 287.19,148.57 288.39,147.11C305.81,126.94 329.47,115.77 355.96,113.78C381.23,113.03 406.54,120.21 425.38,137.44C427.29,139.26 429.14,141.12 431,143C431.81,143.81 432.62,144.63 433.46,145.46C462.94,176.5 468.87,224.41 468.25,265.25C467.13,304.35 455.51,343.99 426.67,371.66C404.33,391.19 378.01,397.07 349,396C324.53,394.26 301.09,383.19 284.14,365.48C264.42,342.63 255.43,313.53 252,284C251.81,285.05 251.81,285.05 251.62,286.12C245.23,321.43 235.31,357.79 204.49,379.79C194.76,386.23 184.61,391.47 173.44,394.81C172.47,395.1 172.47,395.1 171.48,395.4C160.36,398.51 149.19,399.37 137.69,399.31C136.41,399.31 135.14,399.3 133.83,399.3C110.34,398.99 87.83,392.26 70.54,375.72C61.1,365.62 55.98,354.28 55.56,340.38C56.13,325.35 61.58,313.5 72.46,303.17C80.85,296 80.85,296 85,296C82.16,293 79.23,290.69 75.81,288.38C58.74,276.05 48.81,256.87 45.13,236.44C40.56,207.37 45.1,176.76 61.89,152.23C64.98,148.18 68.3,144.51 72,141C72.49,140.48 72.98,139.97 73.48,139.43C77.72,134.99 82.12,131.6 87.38,128.44C88.16,127.97 88.94,127.49 89.74,127.01C124.82,106.51 181.02,105.73 213.21,132.61ZM75.57,152.62C58.31,173.74 52.75,200.57 55.22,227.37C57.78,248.18 66.58,266.67 83,280C102.18,293.77 125.73,298.76 149,297C157.05,295.51 164.1,292.35 171,288C170.84,288.53 170.68,289.06 170.52,289.61C169.89,292.53 169.81,295.33 169.69,298.31C168.8,311.24 165.92,322.89 156.69,332.38C149.68,336.61 142.75,338.08 134.69,336.25C132,335 132,335 130.56,333.25C129.8,330.21 130.21,328.01 131,325C134.01,321.99 136.82,322.4 141,322C137.95,313.25 130.75,308.24 122.71,304.19C112.58,299.74 102.06,300.15 91.75,304.13C80.35,309.39 73.4,316.74 68.63,328.31C65.28,338.4 66.43,347.52 71,357C78.15,370.57 90.88,378.85 105.09,383.72C132.64,392.21 162.93,389.69 188.44,376.28C213.28,362.51 226.88,339.32 234.69,312.64C247.51,266.33 250.04,206.87 225.77,163.89C213.98,144.66 196.19,131.5 174.25,126.23C140.18,118.79 99.55,125.44 75.57,152.62ZM294.68,156.54C266.51,190.45 258.98,236.54 262.46,279.36C265.09,305.79 272.25,333.52 290,354C290.65,354.81 291.31,355.62 291.98,356.45C305.96,373.08 326.89,382.2 348.19,384.75C349.79,384.86 351.39,384.94 353,385C353.99,385.04 354.98,385.08 356,385.12C380.97,385.63 403.22,377.65 421.52,360.58C428.65,353.6 434.29,345.81 439,337C439.34,336.38 439.67,335.75 440.02,335.11C461.8,293.83 462.09,240.73 448.69,196.75C442.87,178.7 434.51,162.53 421,149C420.32,148.3 419.63,147.61 418.93,146.89C403.07,131.77 381.39,125.31 359.88,124.63C333.68,125.54 312.17,137.4 294.68,156.54ZM150.31,307C148.89,307.33 147.47,307.66 146,308C148.31,311.96 150.62,315.92 153,320C156.59,315.21 158,311.98 158,306C155.2,306 153.02,306.37 150.31,307Z"
        val customPath = try {
            PathParser().parsePathString(pathString).toPath()
        } catch (e: Exception) {
            Path() // Return empty path on failure to avoid crash in preview
        }

        // 2. Draw the background (unfilled) state of the entire shape
        drawPath(
            path = customPath,
            color = backgroundColor
        )

        // 3. Clip to the shape and draw the fill level from the bottom up
        clipPath(path = customPath) {
            val fillHeight = height * fillRatio
            val topOffset = height - fillHeight

            drawRect(
                color = fillInColor,
                topLeft = androidx.compose.ui.geometry.Offset(0f, topOffset),
                size = androidx.compose.ui.geometry.Size(width, fillHeight)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterShapePreview() {
    CounterShape(currentDays = 30, totalDays = 90)
}