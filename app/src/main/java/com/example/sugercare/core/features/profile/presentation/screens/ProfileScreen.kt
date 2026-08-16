package com.example.sugercare.core.features.profile.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare.core.features.auth.presentation.AuthViewModel
import com.example.sugercare.core.features.profile.model.ProfileUiState
import com.example.sugercare.core.features.profile.presentation.ProfileViewModel
import com.example.sugercare.core.mainComponents.utils.vibrate
import com.google.firebase.auth.FirebaseAuth
import com.sugarcare.app.navigation.Screen
import com.sugarcare.app.ui.components.GradientButton
import com.sugarcare.app.ui.components.ProfilePicture
import com.sugarcare.app.ui.theme.*
import java.util.Calendar
import com.sugarcare.app.R

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
    val bgColor = if (isDark) BackgroundDark else BackgroundLight
    val cardColor = if (isDark) SurfaceDark else Color.White
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor = if (isDark) Color(0xFF80CBC4) else TextMedium
    val navColor = if (isDark) SurfaceDark else Color.White
    val navText = if (isDark) Color(0xFF80CBC4) else TextMedium

    // Mohamed : TO allow Delete Accoumt
    LaunchedEffect(profileState.value) {
        when (profileState.value) {
            is ProfileUiState.SaveSuccess -> onSaveSuccess()
            is ProfileUiState.AccountDeleted -> {
                authViewModel.clearRememberMeDetails()
                authViewModel.logout()
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {}
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
                        color = textColor)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor)
            )
        },
        bottomBar = { BottomNav(navController, Screen.Profile.route) },
        containerColor = bgColor
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
                    containerColor = bgColor),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
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
                                color = textColor)
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
                is ProfileUiState.AccountDeleted,
                is ProfileUiState.SaveSuccess -> {
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
                        icon          = Icons.Filled.Person,
                        color = textColor
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
                        keyboardType = KeyboardType.Phone,
                        color        = textColor
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
//                                profileViewModel.updateAge(it)
                                profileViewModel.clearFieldError("age")
                            },
                            label         = "Age",
                            /* remove edit age access ← ←
                             isError       = fieldErrors.value.containsKey("age"),
                               supportingText = {
                                   fieldErrors.value["age"]?.let {
                                       Text(it, color = Color.Red, fontSize = 12.sp)
                                   }
                               },*/
                            icon         = Icons.Filled.HealthAndSafety,
                            keyboardType = KeyboardType.Number,
                            color        = textColor
                        )
                        ProfileFieldItem(
                            modifier      = Modifier.weight(1.5f),
                            value         = editableProfile.value.weight.toString(),
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
                            keyboardType = KeyboardType.Decimal,
                            color        = textColor
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    // Gender dropdown
                    ExposedDropdownMenuBox(showGender, { showGender = it }) {
                        OutlinedTextField(
                            value         = editableProfile.value.gender,
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Gender",color =  textColor) },
                            leadingIcon   = { Icon(Icons.Filled.Wc, null, tint = TealPrimary) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(showGender) },
                            modifier      = Modifier
                                .fillMaxWidth()
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
                                            color = textColor,
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


                    // Mohamed : Replaced with -> GradientButton  <- <-
                  /*  PrimaryButton(
                        text    = if (isSaving) "Saving..." else "Save Changes",
                        onClick = { profileViewModel.saveProfile() },
                        enabled = !isSaving
                    )*/


                    // Save button
                    GradientButton(
                        text = if (isSaving) "Saving..." else "Save Changes",
                        onClick = { profileViewModel.saveProfile() },
                        enabled = true,
                        color1 = TealPrimary,
                        color2 = TealPrimary2,
                        textSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_save) ,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // ------- Delete Account Process
                    var showDeleteDialog by remember { mutableStateOf(false) }
                    var deletePassword by remember { mutableStateOf("") }
                     val auth = FirebaseAuth.getInstance()

                    val isEmailProvider = auth.currentUser?.providerData
                        ?.map { it.providerId }
                        ?.contains("password") == true

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Delete Account", fontWeight = FontWeight.Bold) },
                            text = {
                                Column {
                                    Text("Are you sure you want to delete your account? This cannot be undone.")
                                    if (isEmailProvider) {  // ✅ Only show for Email users
                                        Spacer(Modifier.height(12.dp))
                                        OutlinedTextField(
                                            value = deletePassword,
                                            onValueChange = { deletePassword = it },
                                            label = { Text("Confirm Password") },
                                            visualTransformation = PasswordVisualTransformation(),
                                            singleLine = true
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    profileViewModel.deleteAccount(
                                        if (isEmailProvider) deletePassword else null  // ✅ Pass null for Google
                                    )
                                    showDeleteDialog = false
                                }) {
                                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDeleteDialog = false
                                    deletePassword = ""
                                }) { Text("Cancel") }
                            }
                        )
                    }

                    GradientButton(
                        text = "Delete Account!",
                        onClick = { showDeleteDialog = true },  // ✅ Show dialog first
                        enabled = true,
                        color1 = FireIcon,
                        color2 = FireIcon2,
                        textSize = 18.sp,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_cross),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Log Out
                    OutlinedButton(
                        onClick  = { showLogout = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape    = RoundedCornerShape(28.dp),
                        border   = androidx.compose.foundation.BorderStroke(
                            1.5.dp, Color(0xFFE53935))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
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
private fun   ProfileFieldItem(
    modifier      : Modifier,
    value         : String,
    onValueChange : (String) -> Unit,
    label         : String,
    isError       : Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    icon          : ImageVector,
    keyboardType  : KeyboardType = KeyboardType.Text,
    color         : Color
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label,color = color) },
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
    val darkState = LocalDarkTheme.current
    val isDark    = darkState.value
    val textColor = if (isDark) Color(0xFFE0F2F1) else TextDark


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
        label          = { Text("Date of Birth",color = textColor) },
        leadingIcon    = { Icon(Icons.Filled.CalendarToday, null, tint = TealPrimary) },
        trailingIcon   = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Filled.Edit, null, tint = TealPrimary)
            }
        },
        isError        = isError,
        supportingText = supportingText,
        modifier       = Modifier
            .fillMaxWidth()
            .clickable { showPicker = true },
        shape          = RoundedCornerShape(14.dp),
        colors         = newScreenFieldColors()
    )
}

