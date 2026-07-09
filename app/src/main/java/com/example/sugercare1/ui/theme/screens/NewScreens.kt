package com.sugarcare.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare1.navigation.Screen
import com.sugarcare.app.ui.components.* // استدعاء مكوناتنا
import com.sugarcare.app.ui.theme.*
import kotlinx.coroutines.delay

//  NOTIFICATIONS SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(110.dp).clip(CircleShape).background(TealLight),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.NotificationsNone, null, tint = TealPrimary, modifier = Modifier.size(56.dp))
                }
                Spacer(Modifier.height(24.dp))
                Text("No notifications yet", fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(8.dp))
                Text("You're all caught up!\nWe'll notify you when something new arrives.",
                    fontSize = 14.sp, color = TextMedium, textAlign = TextAlign.Center, lineHeight = 22.sp)
            }
        }
    }
}

//   FORGOT PASSWORD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    var email     by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(88.dp).clip(CircleShape).background(TealLight),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Lock, null, tint = TealPrimary, modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(28.dp))
            Text("Confirm it's you", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Text("Enter your registered email and\nwe'll send a verification code.",
                fontSize = 14.sp, color = TextMedium, textAlign = TextAlign.Center, lineHeight = 22.sp)
            Spacer(Modifier.height(36.dp))
            OutlinedTextField(value = email, onValueChange = { email = it },
                label = { Text("Email address") },
                leadingIcon = { Icon(Icons.Filled.Email, null, tint = TealPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary, focusedLabelColor = TealPrimary, cursorColor = TealPrimary,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface)
            )
            Spacer(Modifier.height(28.dp))
            
         
            SugarCareGradientButton(
                text = if (isLoading) "Sending..." else "Send Code",
                gradientColors = listOf(Color(0xFF3B9E9E), Color(0xFF7FE3E1)),
                onClick = { 
                    if (email.isNotBlank() && !isLoading) {
                        isLoading = true
                        navController.navigate(Screen.ForgotPasswordCode.route) 
                    }
                }
            )
            
            Spacer(Modifier.height(20.dp))
            Text("← Back to Sign In", color = TealPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { navController.popBackStack() })
        }
    }
}


// OTP

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordCodeScreen(navController: NavHostController) {
    val codeLength = 6
    val code   = remember { mutableStateListOf(*Array(codeLength) { "" }) }
    var timer  by remember { mutableStateOf(60) }
    var resent by remember { mutableStateOf(false) }
    LaunchedEffect(resent) { timer = 60; while (timer > 0) { delay(1000); timer-- } }
    val allFilled = code.all { it.isNotEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(88.dp).clip(CircleShape).background(TealLight), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.MarkEmailRead, null, tint = TealPrimary, modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(28.dp))
            Text("Check your email", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Text("We've sent a 6-digit verification code\nto your email address.",
                fontSize = 14.sp, color = TextMedium, textAlign = TextAlign.Center, lineHeight = 22.sp)
            Spacer(Modifier.height(36.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(codeLength) { i ->
                    val filled = code[i].isNotEmpty()
                    Box(Modifier.weight(1f).height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (filled) TealLight.copy(0.4f) else MaterialTheme.colorScheme.surface)
                        .border(1.5.dp, if (filled) TealPrimary else Color(0xFFCCCCCC), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.text.BasicTextField(
                            value = code[i],
                            onValueChange = { v -> if (v.length <= 1 && (v.isEmpty() || v[0].isDigit())) code[i] = v },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center,
                                fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                Text("Didn't receive the code? ", fontSize = 13.sp, color = TextMedium)
                Text(if (timer > 0) "Resend in ${timer}s" else "Resend",
                    fontSize = 13.sp,
                    color = if (timer > 0) TextMedium else TealPrimary,
                    fontWeight = if (timer == 0) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = if (timer == 0) Modifier.clickable { resent = !resent } else Modifier)
            }
            Spacer(Modifier.height(28.dp))
            
            // تم تعديل الزرار
            SugarCareGradientButton(
                text = "Verify & Sign In",
                gradientColors = listOf(Color(0xFF3B9E9E), Color(0xFF7FE3E1)),
                onClick = { 
                    if (allFilled) {
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Welcome.route) { inclusive = true } } 
                    }
                }
            )
        }
    }
}


//   COMPLETE PROFILE SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(navController: NavHostController) {
    var fullName   by remember { mutableStateOf("") }
    var phone      by remember { mutableStateOf("") }
    var dob        by remember { mutableStateOf("") }
    var gender     by remember { mutableStateOf("") }
    var showLogout by remember { mutableStateOf(false) }
    var showGender by remember { mutableStateOf(false) }
    var isDark     by remember { mutableStateOf(false) }

    if (showLogout) {
        AlertDialog(onDismissRequest = { showLogout = false },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red) },
            title = { Text("Log Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogout = false
                    navController.navigate(Screen.SignIn.route) { popUpTo(Screen.Welcome.route) { inclusive = true } }
                }) { Text("Log Out", color = Color.Red, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { showLogout = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = { BottomNavBar(navController, Screen.Profile.route) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(Modifier.size(108.dp).clip(CircleShape).background(TealLight).border(3.dp, TealPrimary, CircleShape),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, null, tint = TealPrimary, modifier = Modifier.size(60.dp))
                }
                Box(Modifier.size(34.dp).clip(CircleShape).background(GreenAccent).clickable { },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Tap camera to change photo", fontSize = 12.sp, color = TextMedium)
            Spacer(Modifier.height(20.dp))

            // Dark Mode toggle
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                    Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode, null,
                            tint = TealPrimary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Dark Mode", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Switch(checked = isDark, onCheckedChange = { isDark = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = TealPrimary))
                }
            }
            Spacer(Modifier.height(20.dp))

            Text("Personal Details", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                color = TealDark, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            ProfileField(fullName, { fullName = it }, "Full Name", Icons.Filled.Person)
            Spacer(Modifier.height(12.dp))
            ProfileField(phone, { phone = it }, "Phone Number", Icons.Filled.Phone,
                androidx.compose.ui.text.input.KeyboardType.Phone)
            Spacer(Modifier.height(12.dp))
            ProfileField(dob, { dob = it }, "Date of Birth (DD/MM/YYYY)", Icons.Filled.CalendarToday)
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = showGender, onExpandedChange = { showGender = it }) {
                OutlinedTextField(value = gender, onValueChange = {}, readOnly = true,
                    label = { Text("Gender") },
                    leadingIcon = { Icon(Icons.Filled.Wc, null, tint = TealPrimary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGender) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(28.dp), colors = profileFieldColors()
                )
                ExposedDropdownMenu(expanded = showGender, onDismissRequest = { showGender = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    listOf("Male", "Female", "Prefer not to say").forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp,
                                fontWeight = if (opt == gender) FontWeight.SemiBold else FontWeight.Normal) },
                            onClick = { gender = opt; showGender = false },
                            modifier = Modifier.background(
                                if (opt == gender) TealLight.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface)
                        )
                    }
                }
            }
            Spacer(Modifier.height(28.dp))

            
            SugarCareGradientButton(
                text = "Save Changes",
                gradientColors = listOf(Color(0xFF3B9E9E), Color(0xFF7FE3E1)),
                onClick = { navController.popBackStack() }
            )
            
            Spacer(Modifier.height(12.dp))
            
            
            SugarCareGradientButton(
                text = "Switch Account",
                gradientColors = listOf(Color(0xFF65B96E), Color(0xFF9DF0A5)),
                onClick = { navController.navigate(Screen.SignIn.route) { popUpTo(Screen.Welcome.route) { inclusive = true } } }
            )
            
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { showLogout = true },
                modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE53935))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFE53935))
                Spacer(Modifier.width(8.dp))
                Text("Log Out", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileField(
    value: String, onValueChange: (String) -> Unit, label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text
) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = TealPrimary) },
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType), colors = profileFieldColors())
}

@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TealPrimary, focusedLabelColor = TealPrimary, cursorColor = TealPrimary,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface
)
