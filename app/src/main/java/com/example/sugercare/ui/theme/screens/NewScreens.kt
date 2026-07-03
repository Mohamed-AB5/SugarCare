package com.example.sugercare1

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.sugercare.ProfileUiState
import com.example.sugercare.utils.vibrate
import com.example.sugercare.viewModels.AuthViewModel
import com.example.sugercare.viewModels.ProfileViewModel
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.PrimaryButton
import com.sugarcare.app.ui.components.ProfilePicture
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.GreenAccent
import com.sugarcare.app.ui.theme.TealDark
import com.sugarcare.app.ui.theme.TealLight
import com.sugarcare.app.ui.theme.TealPrimary
import com.sugarcare.app.ui.theme.TealPrimary2
import com.sugarcare.app.ui.theme.TextDark
import com.sugarcare.app.ui.theme.TextMedium
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.collections.all
import kotlin.collections.forEach
import kotlin.text.isDigit
import kotlin.text.isEmpty
import kotlin.text.isNotBlank
import kotlin.text.isNotEmpty
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    NavItem("Home", Icons.Filled.Home, Screen.Home.route),
    NavItem("Logs", Icons.AutoMirrored.Filled.Assignment, Screen.Logs.route),
    NavItem("Meals", Icons.Filled.Restaurant, Screen.MealPlan.route),
    NavItem("Profile", Icons.Filled.Person, Screen.Profile.route)
)

@Composable
private fun SugarCareBottomNav(navController: NavHostController, currentRoute: String) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route)
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    unselectedIconColor = TextMedium,
                    indicatorColor = TealLight
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
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
                        tint = TealPrimary,
                        modifier = Modifier.size(56.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    "No notifications yet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextDark
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "You're all caught up!\nWe'll let you know when something\nnew arrives.",
                    fontSize = 14.sp,
                    color = TextMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
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

            Text(
                "Confirm it's you",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter your registered email address and\nwe'll send you a verification code.",
                fontSize = 14.sp,
                color = TextMedium,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(36.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email address") },
                leadingIcon = { Icon(Icons.Filled.Email, null, tint = TealPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    focusedLabelColor = TealPrimary,
                    cursorColor = TealPrimary
                )
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    isLoading = true
                    navController.navigate(Screen.ForgotPasswordCode.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                enabled = email.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Send Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "← Back to Sign In",
                color = TealPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordCodeScreen(navController: NavHostController) {
    val codeLength = 6
    val code = remember { mutableStateListOf(*Array(codeLength) { "" }) }
    var timer by remember { mutableStateOf(60) }
    var resent by remember { mutableStateOf(false) }

    LaunchedEffect(resent) {
        timer = 60
        while (timer > 0) {
            delay(1000); timer--
        }
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
                Icon(
                    Icons.Filled.MarkEmailRead,
                    null,
                    tint = TealPrimary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "Check your email",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "We've sent a 6-digit verification code\nto your email address.",
                fontSize = 14.sp,
                color = TextMedium,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(36.dp))

            // OTP boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        androidx.compose.foundation.text.BasicTextField(
                            value = code[i],
                            onValueChange = { v ->
                                if (v.length <= 1 && (v.isEmpty() || v[0].isDigit()))
                                    code[i] = v
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
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
                    text = if (timer > 0) "Resend in ${timer}s" else "Resend",
                    fontSize = 13.sp,
                    color = if (timer > 0) TextMedium else TealPrimary,
                    fontWeight = if (timer == 0) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = if (timer == 0) Modifier.clickable { resent = !resent } else Modifier
                )
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                enabled = allFilled,
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Verify & Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    var showLogout by remember { mutableStateOf(false) }
    var showGender by remember { mutableStateOf(false) }

    // ── TO Access ProfileViewModel ↓ ↓ ↓  ────────────────────
    val profileState = profileViewModel.profileState.collectAsState()
    val editableProfile = profileViewModel.editableProfile.collectAsState()

    val genderOptions = listOf("Male", "Female")
    val fieldErrors = profileViewModel.fieldErrors.collectAsState()


    LaunchedEffect(profileState.value,Unit) {
        if (profileState.value is ProfileUiState.Saved) {
            onSaveSuccess()
            profileViewModel.resetState()
        }
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            containerColor = BackgroundLight,
            icon = { Icon(Icons.Filled.Logout, null, tint = Color.Red) },
            title = { Text("Log Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of your account?") },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.logout()
                    profileViewModel.clearData()
                    showLogout = false
                    navController.navigate(Screen.SignIn.route) { popUpTo(0) { inclusive = true } }
                }) { Text("Log Out", color = Color.Red, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) { Text("Cancel", color = TextMedium) }
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
            ProfilePicture(profileViewModel)
            Spacer(Modifier.height(28.dp))

            when (profileState.value) {
                is ProfileUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ProfileUiState.Error -> {
                    Box((Modifier.fillMaxSize()), contentAlignment = Alignment.Center) {
                        Text(
                            text = (profileState.value as ProfileUiState.Error).message,
                            color = Color.Red
                        )
                    }
                }

                is ProfileUiState.Success,
                is ProfileUiState.Saving,
                is ProfileUiState.Saved -> {
                    LaunchedEffect(fieldErrors.value) {
                        if (fieldErrors.value.isNotEmpty()) {
                            vibrate(context)
                        }
                    }
                    val isSaving = profileState.value is ProfileUiState.Saving
                    Text(
                        "Personal Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealDark,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    ProfileField(
                        value = editableProfile.value.fullName,
                        onValueChange = {
                            profileViewModel.updateFullName(it)
                            profileViewModel.clearFieldError("fullName")
                        },
                        label = "Full Name",
                        isError = fieldErrors.value.containsKey("fullName"),
                        supportingText = {
                            fieldErrors.value["fullName"]?.let {
                                Text(text = it, color = Color.Red, fontSize = 12.sp)
                            }
                        },
                        icon = Icons.Filled.Person
                    )
                    Spacer(Modifier.height(12.dp))

                    ProfileField(
                        value = editableProfile.value.phone,
                        onValueChange = {
                            profileViewModel.updatePhoneNumber(it)
                            profileViewModel.clearFieldError("phone")
                        },
                        label = "Phone Number",
                        isError = fieldErrors.value.containsKey("phone"),
                        supportingText = {
                            fieldErrors.value["phone"]?.let {
                                Text(text = it, color = Color.Red, fontSize = 12.sp)
                            }
                        },
                        icon = Icons.Filled.Phone,
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                    )
                    Spacer(Modifier.height(12.dp))

//                    ProfileField(
//                        value = editableProfile.value.dob,
//                        onValueChange = {
//                            profileViewModel.updateDateOfBirth(it)
//                            profileViewModel.clearFieldError("dob")
//                        },
//                        label = "Date of Birth (DD/MM/YYYY)",
//                        isError       = fieldErrors.value.containsKey("dob"),
//                        supportingText = {
//                            fieldErrors.value["dob"]?.let {
//                                Text(text = it, color = Color.Red, fontSize = 12.sp)
//                            }
//                        },
//                        icon = Icons.Filled.CalendarToday
//                    )

                    DatePickerField(
                        selectedDate = editableProfile.value.dob,
                        onDateSelected = {
                            profileViewModel.updateDateOfBirth(it)
                            profileViewModel.clearFieldError("dateOfBirth")
                        },
                        isError = fieldErrors.value.containsKey("dateOfBirth"),
                        supportingText = {
                            fieldErrors.value["dateOfBirth"]?.let {
                                Text(text = it, color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))


                    ExposedDropdownMenuBox(
                        expanded = showGender,
                        onExpandedChange = { showGender = it }
                    ) {
                        OutlinedTextField(
                            value = editableProfile.value.gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            leadingIcon = { Icon(Icons.Filled.Wc, null, tint = TealPrimary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGender) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(14.dp),
                            colors = profileFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = showGender,
                            onDismissRequest = { showGender = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            genderOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            opt,
                                            color = TextDark,
                                            fontSize = 15.sp,
                                            fontWeight = if (opt == editableProfile.value.gender) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        profileViewModel.updateGender(opt); showGender = false
                                    }, // maybe error here
                                    modifier = Modifier.background(
                                        if (opt == editableProfile.value.gender) TealLight.copy(
                                            alpha = 0.35f
                                        ) else Color.White
                                    ),
                                    colors = MenuDefaults.itemColors(
                                        textColor = TextDark
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    /*     Box(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .height(54.dp)
                                 .clip(RoundedCornerShape(27.dp))
                                 .background(
                                     Brush.horizontalGradient(
                                         listOf(TealPrimary, TealPrimary2)
                                     )
                                 )
                                 .clickable { profileViewModel.saveProfile() }
                                 .padding(vertical = 4.dp),
                             contentAlignment = Alignment.Center
                         ) {
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 horizontalArrangement = Arrangement.spacedBy(6.dp)
                             ) {
                                 Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                                 Spacer(Modifier.width(1.dp))
                                 Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                             }
                         }*/

                    PrimaryButton(
                        text = if (isSaving) "Saving..." else "Save",
                        onClick = { profileViewModel.saveProfile() },
                        enabled = !isSaving
                    )

                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { showLogout = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE53935))
                    ) {
                        Icon(Icons.Filled.Logout, null, tint = Color(0xFFE53935))
                        Spacer(Modifier.width(8.dp))
                        Text("Log Out", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(16.dp))
                }

            }

        }
    }
}

@Composable
private fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = TealPrimary) },
        isError = isError,
        supportingText = supportingText,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = profileFieldColors()
    )
}

@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TealPrimary,
    focusedLabelColor = TealPrimary,
    cursorColor = TealPrimary
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
) {
    var showPicker by remember { mutableStateOf(false) }

    // ✅ Show calendar dialog
    if (showPicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
                        val year = calendar.get(Calendar.YEAR).toString()
                        onDateSelected("$day/$month/$year")
                    }
                    showPicker = false
                }) {
                    Text("Confirm", color = TealPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel", color = TealPrimary)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Select Date of Birth",
                        modifier = Modifier.padding(16.dp),
                        color = TealDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                showModeToggle = true
            )
        }
    }


    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text("Date of Birth") },
        leadingIcon = {
            Icon(Icons.Filled.CalendarToday, null, tint = TealPrimary)
        },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Filled.Edit, null, tint = TealPrimary)
            }
        },
        isError = isError,
        supportingText = supportingText,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showPicker = true },
        shape = RoundedCornerShape(14.dp),
        colors = profileFieldColors()
    )
}

