package com.example.sugercare1

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare1.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.collections.all
import kotlin.collections.forEach
import kotlin.text.isDigit
import kotlin.text.isEmpty
import kotlin.text.isNotBlank
import kotlin.text.isNotEmpty

private val TealPrimary     = Color(0xFF2E9B9B)
private val TealLight       = Color(0xFFB2DFDB)
private val TealDark        = Color(0xFF00695C)
private val GreenAccent     = Color(0xFF5BAD6F)
private val BackgroundLight = Color(0xFFF0F9F9)
private val TextDark        = Color(0xFF1A2E2E)
private val TextMedium      = Color(0xFF607070)


private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    NavItem("Home", Icons.Filled.Home, Screen.Home.route),
    NavItem("Logs", Icons.Filled.FavoriteBorder, Screen.Logs.route),
    NavItem("Meals", Icons.Filled.Restaurant, Screen.MealPlan.route),
    NavItem("Profile", Icons.Filled.Person, Screen.Profile.route)
)

@Composable
private fun SugarCareBottomNav(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick  = {
                    if (currentRoute != item.route)
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState    = true
                        }
                },
                icon  = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = TealPrimary,
                    unselectedIconColor = TextMedium,
                    indicatorColor      = TealLight
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, color = TealDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TealDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Box(
            modifier         = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(TealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.NotificationsNone,
                        contentDescription = null,
                        tint     = TealPrimary,
                        modifier = Modifier.size(56.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    "No notifications yet",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                    color      = TextDark
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "You're all caught up!\nWe'll let you know when something\nnew arrives.",
                    fontSize   = 14.sp,
                    color      = TextMedium,
                    textAlign  = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    var email     by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TealDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(TealLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Lock, null, tint = TealPrimary, modifier = Modifier.size(44.dp))
            }

            Spacer(Modifier.height(28.dp))

            Text("Confirm it's you", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter your registered email address and\nwe'll send you a verification code.",
                fontSize   = 14.sp,
                color      = TextMedium,
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(36.dp))

            OutlinedTextField(
                value           = email,
                onValueChange   = { email = it },
                label           = { Text("Email address") },
                leadingIcon     = { Icon(Icons.Filled.Email, null, tint = TealPrimary) },
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(16.dp),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    focusedLabelColor  = TealPrimary,
                    cursorColor        = TealPrimary
                )
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick  = {
                    isLoading = true
                    navController.navigate(Screen.ForgotPasswordCode.route)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(27.dp),
                enabled  = email.isNotBlank() && !isLoading,
                colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color       = Color.White,
                        modifier    = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Send Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "← Back to Sign In",
                color      = TealPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.clickable { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordCodeScreen(navController: NavHostController) {
    val codeLength = 6
    val code   = remember { mutableStateListOf(*Array(codeLength) { "" }) }
    var timer  by remember { mutableStateOf(60) }
    var resent by remember { mutableStateOf(false) }

    LaunchedEffect(resent) {
        timer = 60
        while (timer > 0) {
            delay(1000); timer-- }
    }

    val allFilled = code.all { it.isNotEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TealDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(TealLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.MarkEmailRead, null, tint = TealPrimary, modifier = Modifier.size(44.dp))
            }

            Spacer(Modifier.height(28.dp))

            Text("Check your email", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(8.dp))
            Text(
                "We've sent a 6-digit verification code\nto your email address.",
                fontSize   = 14.sp,
                color      = TextMedium,
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(36.dp))

            // OTP boxes
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(codeLength) { i ->
                    val isFilled = code[i].isNotEmpty()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isFilled) TealLight.copy(alpha = 0.4f) else Color.White)
                            .border(
                                width = 1.5.dp,
                                color = if (isFilled) TealPrimary else Color(0xFFCCCCCC),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = code[i],
                            onValueChange = { v ->
                                if (v.length <= 1 && (v.isEmpty() || v[0].isDigit()))
                                    code[i] = v
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealDark
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text("Didn't receive the code? ", fontSize = 13.sp, color = TextMedium)
                Text(
                    text       = if (timer > 0) "Resend in ${timer}s" else "Resend",
                    fontSize   = 13.sp,
                    color      = if (timer > 0) TextMedium else TealPrimary,
                    fontWeight = if (timer == 0) FontWeight.SemiBold else FontWeight.Normal,
                    modifier   = if (timer == 0) Modifier.clickable { resent = !resent } else Modifier
                )
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick  = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(27.dp),
                enabled  = allFilled,
                colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Verify & Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(navController: NavHostController) {
    var fullName   by remember { mutableStateOf("") }
    var phone      by remember { mutableStateOf("") }
    var dob        by remember { mutableStateOf("") }
    var gender     by remember { mutableStateOf("") }
    var showLogout by remember { mutableStateOf(false) }
    var showGender by remember { mutableStateOf(false) }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            icon  = { Icon(Icons.Filled.Logout, null, tint = Color.Red) },
            title = { Text("Log Out", fontWeight = FontWeight.Bold) },
            text  = { Text("Are you sure you want to log out of your account?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogout = false
                    navController.navigate(Screen.SignIn.route) { popUpTo(0) { inclusive = true } }
                }) { Text("Log Out", color = Color.Red, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold, color = TealDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TealDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        bottomBar = { SugarCareBottomNav(navController, Screen.Profile.route) },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(TealLight)
                        .border(3.dp, TealPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, tint = TealPrimary, modifier = Modifier.size(60.dp))
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GreenAccent)
                        .clickable { /* open image picker */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(6.dp))
            Text("Tap camera to change photo", fontSize = 12.sp, color = TextMedium)
            Spacer(Modifier.height(28.dp))


            Text(
                "Personal Details",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TealDark,
                modifier   = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            ProfileField(value = fullName, onValueChange = { fullName = it },
                label = "Full Name",                icon = Icons.Filled.Person)
            Spacer(Modifier.height(12.dp))

            ProfileField(value = phone, onValueChange = { phone = it },
                label = "Phone Number",             icon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(12.dp))

            ProfileField(value = dob, onValueChange = { dob = it },
                label = "Date of Birth (DD/MM/YYYY)", icon = Icons.Filled.CalendarToday)
            Spacer(Modifier.height(12.dp))


            ExposedDropdownMenuBox(
                expanded         = showGender,
                onExpandedChange = { showGender = it }
            ) {
                OutlinedTextField(
                    value        = gender,
                    onValueChange = {},
                    readOnly     = true,
                    label        = { Text("Gender") },
                    leadingIcon  = { Icon(Icons.Filled.Wc, null, tint = TealPrimary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGender) },
                    modifier     = Modifier.fillMaxWidth().menuAnchor(),
                    shape        = RoundedCornerShape(14.dp),
                    colors       = profileFieldColors()
                )
                ExposedDropdownMenu(expanded = showGender, onDismissRequest = { showGender = false }) {
                    listOf("Male", "Female", "Prefer not to say").forEach { opt ->
                        DropdownMenuItem(
                            text    = { Text(opt) },
                            onClick = { gender = opt; showGender = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick  = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(27.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick  = {
                    navController.navigate(Screen.SignIn.route) { popUpTo(0) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(27.dp),
                border   = BorderStroke(1.5.dp, TealPrimary)
            ) {
                Icon(Icons.Filled.SwitchAccount, null, tint = TealPrimary)
                Spacer(Modifier.width(8.dp))
                Text("Switch Account", color = TealPrimary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick  = { showLogout = true },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(27.dp),
                border   = BorderStroke(1.5.dp, Color(0xFFE53935))
            ) {
                Icon(Icons.Filled.Logout, null, tint = Color(0xFFE53935))
                Spacer(Modifier.width(8.dp))
                Text("Log Out", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
@Composable
private fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType =
        KeyboardType.Text
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(label) },
        leadingIcon     = { Icon(icon, null, tint = TealPrimary) },
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(14.dp),
        singleLine      = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors          = profileFieldColors()
    )
}

@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TealPrimary,
    focusedLabelColor  = TealPrimary,
    cursorColor        = TealPrimary
)