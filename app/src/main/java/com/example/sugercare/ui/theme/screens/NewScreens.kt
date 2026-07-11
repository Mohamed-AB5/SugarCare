
package com.example.sugercare.ui.theme.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare.navigation.Screen
import com.example.sugercare.profileRepo.ProfileUiState
import com.example.sugercare.utils.vibrate
import com.example.sugercare.viewModels.AuthViewModel
import com.example.sugercare.viewModels.ProfileViewModel
import com.example.sugercare.viewModels.ResetPassState
import com.sugarcare.app.ui.components.PrimaryButton
import com.sugarcare.app.ui.components.ProfilePicture
import com.sugarcare.app.ui.theme.*
import java.util.Calendar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import kotlinx.coroutines.delay

// ── Shared bottom nav ─────────────────────────────────────────
@Composable
private fun BottomNav(navController: NavHostController, currentRoute: String) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        listOf(
            Triple("Home",    Icons.Filled.Home,       Screen.Home.route),
            Triple("Logs",    Icons.Filled.Favorite,   Screen.Logs.route),
            Triple("Meals",   Icons.Filled.Restaurant, Screen.MealPlan.route),
            Triple("Profile", Icons.Filled.Person,     Screen.Profile.route)
        ).forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick  = {
                    if (currentRoute != route)
                        navController.navigate(route) { launchSingleTop = true }
                },
                icon   = { Icon(icon, contentDescription = label) },
                label  = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = TealPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    indicatorColor      = TealLight
                )
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  1. NOTIFICATIONS
// ══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notifications",
                        fontWeight = FontWeight.Bold,
                        color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B))
                },
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
        Box(
            Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(110.dp).clip(CircleShape).background(TealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.NotificationsNone, null,
                        tint = TealPrimary, modifier = Modifier.size(56.dp))
                }
                Spacer(Modifier.height(24.dp))
                Text("No notifications yet",
                    fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B))
                Spacer(Modifier.height(8.dp))
                Text(
                    "You're all caught up!\nWe'll notify you when something new arrives.",
                    fontSize = 14.sp,
                    color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1).copy(0.6f) else Color(0xFF4A6565),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  2. FORGOT PASSWORD — Step 1 (with real Firebase reset)
// ══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val email          = authViewModel.email.collectAsState()
    val resetPassState = authViewModel.resetPassState.collectAsState()
    val context        = LocalContext.current

    LaunchedEffect(resetPassState.value) {
        if (resetPassState.value is ResetPassState.Success) {
            Toast.makeText(context, "Reset email sent! Check your inbox.", Toast.LENGTH_LONG).show()
            authViewModel.resetPasswordState()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
            Arrangement.Center, Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(88.dp).clip(CircleShape).background(TealLight),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Lock, null,
                    tint = TealPrimary, modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(28.dp))
            Text("Confirm it's you",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B))
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter your registered email and\nwe'll send a verification code.",
                fontSize = 14.sp,
                color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1).copy(0.6f) else Color(0xFF4A6565),
                textAlign = TextAlign.Center, lineHeight = 22.sp
            )
            Spacer(Modifier.height(36.dp))

            OutlinedTextField(
                value         = email.value,
                onValueChange = { authViewModel.updateEmail(it) },
                label         = { Text("Email address") },
                leadingIcon   = { Icon(Icons.Filled.Email, null, tint = TealPrimary) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(28.dp),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors        = newScreenFieldColors()
            )
            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { authViewModel.sendPasswordReset(email.value) },
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(28.dp),
                enabled   = email.value.isNotBlank()
                        && resetPassState.value !is ResetPassState.Loading,
                colors    = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                if (resetPassState.value is ResetPassState.Loading)
                    CircularProgressIndicator(color = Color.White,
                        modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                else
                    Text("Send Reset Email", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (resetPassState.value is ResetPassState.Error) {
                Spacer(Modifier.height(8.dp))
                Text((resetPassState.value as ResetPassState.Error).message,
                    color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(20.dp))
            Text("← Back to Sign In",
                color = TealPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { navController.popBackStack() })
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  3. FORGOT PASSWORD — Step 2: OTP
// ══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordCodeScreen(navController: NavHostController) {
    val code   = remember { mutableStateListOf("", "", "", "", "", "") }
    var timer  by remember { mutableStateOf(30) }
    var resent by remember { mutableStateOf(false) }

    LaunchedEffect(resent) {
        timer = 30
        while (timer > 0) { delay(1000); timer-- }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
            Arrangement.Center, Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(88.dp).clip(CircleShape).background(TealLight),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.MarkEmailRead, null,
                    tint = TealPrimary, modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(28.dp))
            Text("Check your email",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B))
            Spacer(Modifier.height(8.dp))
            Text("We've sent a 6-digit code to your email.",
                fontSize = 14.sp,
                color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1).copy(0.6f) else Color(0xFF4A6565),
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))

            // OTP boxes
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(6) { i ->
                    val filled = code[i].isNotEmpty()
                    Box(
                        Modifier.weight(1f).height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (filled) TealLight.copy(0.4f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                1.5.dp,
                                if (filled) TealPrimary
                                else MaterialTheme.colorScheme.onSurface.copy(0.3f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.text.BasicTextField(
                            value = code[i],
                            onValueChange = { v ->
                                if (v.length <= 1 && (v.isEmpty() || v[0].isDigit())) code[i] = v
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                textAlign  = TextAlign.Center,
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                Text("Didn't receive it? ", fontSize = 13.sp,
                    color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1).copy(0.6f) else Color(0xFF4A6565))
                Text(
                    if (timer > 0) "Resend in ${timer}s" else "Resend",
                    fontSize   = 13.sp,
                    color      = if (timer > 0) MaterialTheme.colorScheme.onBackground.copy(0.5f)
                    else TealPrimary,
                    fontWeight = if (timer == 0) FontWeight.SemiBold else FontWeight.Normal,
                    modifier   = if (timer == 0) Modifier.clickable { resent = !resent } else Modifier
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(28.dp),
                enabled   = code.all { it.isNotEmpty() },
                colors    = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Verify & Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  4. PROFILE SCREEN — full ViewModel + Dark Mode Switch
// ══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController   : NavHostController,
    authViewModel   : AuthViewModel,
    profileViewModel: ProfileViewModel,
    onSaveSuccess   : () -> Unit
) {
    val context         = LocalContext.current
    var showLogout      by remember { mutableStateOf(false) }
    var showGender      by remember { mutableStateOf(false) }
    val profileState    = profileViewModel.profileState.collectAsState()
    val editableProfile = profileViewModel.editableProfile.collectAsState()
    val fieldErrors     = profileViewModel.fieldErrors.collectAsState()
    val genderOptions   = listOf("Male", "Female")

    // ── Dark Mode Switch ──────────────────────────────────────
    val darkState = LocalDarkTheme.current
    val isDark    = darkState.value

    LaunchedEffect(profileState.value) {
        if (profileState.value is ProfileUiState.Saved) {
            onSaveSuccess()
            profileViewModel.resetState()
        }
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            icon  = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFE53935)) },
            title = { Text("Log Out", fontWeight = FontWeight.Bold) },
            text  = { Text("Are you sure you want to log out of your account?") },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.logout()
                    profileViewModel.clearData()
                    showLogout = false
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) { Text("Log Out", color = Color(0xFFE53935), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("My Profile", fontWeight = FontWeight.Bold,
                        color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B))
                },
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
        bottomBar = { BottomNav(navController, Screen.Profile.route) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture
            ProfilePicture(profileViewModel)
            Spacer(Modifier.height(20.dp))

            // ── Dark Mode Switch ──────────────────────────────
            Card(
                Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            null, tint = TealPrimary, modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Dark Mode",
                                fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                                color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B))
                            Text(if (isDark) "On" else "Off",
                                fontSize = 12.sp,
                                color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1).copy(0.6f) else Color(0xFF1A2B2B).copy(0.5f))
                        }
                    }
                    Switch(
                        checked         = isDark,
                        onCheckedChange = { darkState.value = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor  = Color.White,
                            checkedTrackColor  = TealPrimary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(0.2f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Profile fields ────────────────────────────────
            when (profileState.value) {
                is ProfileUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealPrimary)
                    }
                }
                is ProfileUiState.Error -> {
                    Text((profileState.value as ProfileUiState.Error).message,
                        color = Color.Red, textAlign = TextAlign.Center)
                }
                is ProfileUiState.Success,
                is ProfileUiState.Saving,
                is ProfileUiState.Saved -> {
                    LaunchedEffect(fieldErrors.value) {
                        if (fieldErrors.value.isNotEmpty()) vibrate(context)
                    }
                    val isSaving = profileState.value is ProfileUiState.Saving

                    Text("Personal Details",
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                        color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else TealDark, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))

                    ProfileFieldItem(
                        modifier      = Modifier.fillMaxWidth(),
                        value         = editableProfile.value.fullName,
                        onValueChange = {
                            profileViewModel.updateFullName(it)
                            profileViewModel.clearFieldError("fullName")
                        },
                        label         = "Full Name",
                        isError       = fieldErrors.value.containsKey("fullName"),
                        supportingText = {
                            fieldErrors.value["fullName"]?.let {
                                Text(it, color = Color.Red, fontSize = 12.sp)
                            }
                        },
                        icon          = Icons.Filled.Person
                    )
                    Spacer(Modifier.height(12.dp))

                    ProfileFieldItem(
                        modifier      = Modifier.fillMaxWidth(),
                        value         = editableProfile.value.phone,
                        onValueChange = {
                            profileViewModel.updatePhoneNumber(it)
                            profileViewModel.clearFieldError("phone")
                        },
                        label         = "Phone Number",
                        isError       = fieldErrors.value.containsKey("phone"),
                        supportingText = {
                            fieldErrors.value["phone"]?.let {
                                Text(it, color = Color.Red, fontSize = 12.sp)
                            }
                        },
                        icon         = Icons.Filled.Phone,
                        keyboardType = KeyboardType.Phone
                    )
                    Spacer(Modifier.height(12.dp))

                    DatePickerField(
                        selectedDate   = editableProfile.value.dob,
                        onDateSelected = {
                            profileViewModel.updateDateOfBirth(it)
                            profileViewModel.clearFieldError("dateOfBirth")
                        },
                        isError        = fieldErrors.value.containsKey("dateOfBirth"),
                        supportingText = {
                            fieldErrors.value["dateOfBirth"]?.let {
                                Text(it, color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProfileFieldItem(
                            modifier      = Modifier.weight(1f),
                            value         = editableProfile.value.age.toString(),
                            onValueChange = {
                                profileViewModel.updateAge(it)
                                profileViewModel.clearFieldError("age")
                            },
                            label         = "Age",
                            isError       = fieldErrors.value.containsKey("age"),
                            supportingText = {
                                fieldErrors.value["age"]?.let {
                                    Text(it, color = Color.Red, fontSize = 12.sp)
                                }
                            },
                            icon         = Icons.Filled.HealthAndSafety,
                            keyboardType = KeyboardType.Number
                        )
                        ProfileFieldItem(
                            modifier      = Modifier.weight(1.5f),
                            value         = editableProfile.value.weight,
                            onValueChange = {
                                profileViewModel.updateWeight(it)
                                profileViewModel.clearFieldError("weight")
                            },
                            label         = "Weight (kg)",
                            isError       = fieldErrors.value.containsKey("weight"),
                            supportingText = {
                                fieldErrors.value["weight"]?.let {
                                    Text(it, color = Color.Red, fontSize = 12.sp)
                                }
                            },
                            icon         = Icons.Filled.MonitorWeight,
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    // Gender dropdown
                    ExposedDropdownMenuBox(showGender, { showGender = it }) {
                        OutlinedTextField(
                            value         = editableProfile.value.gender,
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Gender") },
                            leadingIcon   = { Icon(Icons.Filled.Wc, null, tint = TealPrimary) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(showGender) },
                            modifier      = Modifier.fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape         = RoundedCornerShape(14.dp),
                            colors        = newScreenFieldColors()
                        )
                        ExposedDropdownMenu(
                            showGender, { showGender = false },
                            Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            genderOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = {
                                        Text(opt,
                                            color = if (LocalDarkTheme.current.value) Color(0xFFE0F2F1) else Color(0xFF1A2B2B),
                                            fontSize = 15.sp,
                                            fontWeight = if (opt == editableProfile.value.gender)
                                                FontWeight.SemiBold else FontWeight.Normal)
                                    },
                                    onClick  = {
                                        profileViewModel.updateGender(opt)
                                        showGender = false
                                    },
                                    modifier = Modifier.background(
                                        if (opt == editableProfile.value.gender)
                                            TealLight.copy(0.35f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    // Save button
                    PrimaryButton(
                        text    = if (isSaving) "Saving..." else "Save Changes",
                        onClick = { profileViewModel.saveProfile() },
                        enabled = !isSaving
                    )
                    Spacer(Modifier.height(12.dp))

                    // Switch Account
                    Button(
                        onClick = {
                            navController.navigate(Screen.SignIn.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier  = Modifier.fillMaxWidth().height(56.dp),
                        shape     = RoundedCornerShape(28.dp),
                        colors    = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Filled.SwitchAccount, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Switch Account", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(12.dp))

                    // Log Out
                    OutlinedButton(
                        onClick  = { showLogout = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape    = RoundedCornerShape(28.dp),
                        border   = androidx.compose.foundation.BorderStroke(
                            1.5.dp, Color(0xFFE53935))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null,
                            tint = Color(0xFFE53935))
                        Spacer(Modifier.width(8.dp))
                        Text("Log Out",
                            color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────
@Composable
private fun ProfileFieldItem(
    modifier      : Modifier,
    value         : String,
    onValueChange : (String) -> Unit,
    label         : String,
    isError       : Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    icon          : ImageVector,
    keyboardType  : KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label) },
        leadingIcon    = { Icon(icon, null, tint = TealPrimary) },
        isError        = isError,
        supportingText = supportingText,
        modifier       = modifier,
        shape          = RoundedCornerShape(14.dp),
        singleLine     = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors         = newScreenFieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate  : String,
    onDateSelected: (String) -> Unit,
    isError       : Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton    = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        val d = cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                        val m = (cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
                        val y = cal.get(Calendar.YEAR).toString()
                        onDateSelected("$d/$m/$y")
                    }
                    showPicker = false
                }) { Text("Confirm", color = TealPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel", color = TealPrimary)
                }
            }
        ) {
            DatePicker(
                state          = datePickerState,
                title          = {
                    Text("Select Date of Birth",
                        modifier   = Modifier.padding(16.dp),
                        color      = TealDark,
                        fontWeight = FontWeight.Bold)
                },
                showModeToggle = true
            )
        }
    }

    OutlinedTextField(
        value          = selectedDate,
        onValueChange  = {},
        readOnly       = true,
        label          = { Text("Date of Birth") },
        leadingIcon    = { Icon(Icons.Filled.CalendarToday, null, tint = TealPrimary) },
        trailingIcon   = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Filled.Edit, null, tint = TealPrimary)
            }
        },
        isError        = isError,
        supportingText = supportingText,
        modifier       = Modifier.fillMaxWidth().clickable { showPicker = true },
        shape          = RoundedCornerShape(14.dp),
        colors         = newScreenFieldColors()
    )
}

@Composable
private fun newScreenFieldColors(): TextFieldColors {
    val isDark         = LocalDarkTheme.current.value
    // Always high contrast: light text on dark bg, dark text on light bg
    val textColor      = if (isDark) Color(0xFFE0F2F1) else Color(0xFF1A2B2B)
    val containerColor = if (isDark) Color(0xFF1A3333)  else Color.White
    val borderColor    = if (isDark) TealPrimary        else TealLight

    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor        = TealPrimary,
        unfocusedBorderColor      = borderColor,
        focusedLabelColor         = TealPrimary,
        unfocusedLabelColor       = if (isDark) TealLight.copy(0.8f) else TextMedium,
        focusedTextColor          = textColor,
        unfocusedTextColor        = textColor,
        disabledTextColor         = textColor.copy(0.7f),
        cursorColor               = TealPrimary,
        focusedContainerColor     = containerColor,
        unfocusedContainerColor   = containerColor,
        focusedPlaceholderColor   = textColor.copy(0.6f),
        unfocusedPlaceholderColor = textColor.copy(0.6f),
        focusedLeadingIconColor   = TealPrimary,
        unfocusedLeadingIconColor = TealPrimary,
        focusedTrailingIconColor  = TealPrimary,
        unfocusedTrailingIconColor = TealPrimary
    )
}