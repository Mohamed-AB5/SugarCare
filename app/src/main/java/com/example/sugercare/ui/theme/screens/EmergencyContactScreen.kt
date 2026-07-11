package com.example.sugercare.ui.theme.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sugercare.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.sugarcare.app.ui.theme.*
import com.sugarcare.app.ui.theme.LocalDarkTheme
import com.sugarcare.app.ui.theme.BackgroundDark
import com.sugarcare.app.ui.theme.BackgroundLight
import com.sugarcare.app.ui.theme.SurfaceDark
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

// ── Data model ────────────────────────────────────────────────
data class EmergencyContact(
    val id       : String  = "",
    val name     : String  = "",
    val phone    : String  = "",
    val relation : String  = "",
    val isPrimary: Boolean = false
) {
    fun initials() = name.split(" ")
        .filter { it.isNotEmpty() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

    fun toMap() = mapOf(
        "id"        to id,
        "name"      to name,
        "phone"     to phone,
        "relation"  to relation,
        "isPrimary" to isPrimary
    )
}

// ══════════════════════════════════════════════════════════════
//  SCREEN
// ══════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactScreen(navController: NavHostController) {
    val context      = LocalContext.current
    val scope        = rememberCoroutineScope()
    val snackbar     = remember { SnackbarHostState() }

    // ── Dark mode colors ──────────────────────────────────────
    val isDark        = LocalDarkTheme.current.value
    val bgColor       = if (isDark) BackgroundDark else BackgroundLight
    val cardColor     = if (isDark) SurfaceDark    else Color.White
    val textColor     = if (isDark) Color(0xFFE0F2F1) else TextDark
    val subColor      = if (isDark) Color(0xFF80CBC4)  else TextMedium
    val emergencyRed  = if (isDark) Color(0xFFEF5350) else Color(0xFFE53935)
    val badgeBg       = emergencyRed.copy(alpha = if (isDark) 0.22f else 0.12f)
    val iconCircleBg  = emergencyRed.copy(alpha = if (isDark) 0.20f else 0.1f)
    val deleteTint    = if (isDark) Color(0xFF9E9E9E) else Color.Gray.copy(alpha = 0.6f)
    val dialogMenuBg  = if (isDark) SurfaceDark else Color.White

    // ── State ─────────────────────────────────────────────────
    var contacts     by remember { mutableStateOf<List<EmergencyContact>>(emptyList()) }
    var isLoading    by remember { mutableStateOf(true) }
    var showDialog   by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<EmergencyContact?>(null) }
    var isSaving     by remember { mutableStateOf(false) }

    // ── Firebase refs ─────────────────────────────────────────
    val uid = Firebase.auth.currentUser?.uid
    val col = uid?.let {
        Firebase.firestore.collection("users").document(it).collection("emergencyContacts")
    }

    // ── Load contacts ─────────────────────────────────────────
    LaunchedEffect(Unit) {
        if (col == null) { isLoading = false; return@LaunchedEffect }
        try {
            col.addSnapshotListener { snap, _ ->
                contacts = snap?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(EmergencyContact::class.java)?.copy(id = doc.id)
                    }
                    ?.sortedByDescending { it.isPrimary }
                    ?: emptyList()
                isLoading = false
            }
        } catch (e: Exception) {
            isLoading = false
        }
    }

    // ── Add contact ───────────────────────────────────────────
    fun saveContact(contact: EmergencyContact) {
        if (col == null) return
        isSaving = true
        scope.launch {
            try {
                val docRef = if (contact.id.isBlank()) col.document()
                else col.document(contact.id)
                docRef.set(contact.copy(id = docRef.id).toMap()).await()
                snackbar.showSnackbar("Contact saved ✓")
            } catch (e: Exception) {
                snackbar.showSnackbar("Error: ${e.message}")
            } finally {
                isSaving = false
            }
        }
    }

    // ── Delete contact ────────────────────────────────────────
    fun deleteContact(id: String) {
        if (col == null) return
        scope.launch {
            try {
                col.document(id).delete().await()
                snackbar.showSnackbar("Contact removed")
            } catch (e: Exception) {
                snackbar.showSnackbar("Error: ${e.message}")
            }
        }
    }

    // ── Add dialog ────────────────────────────────────────────
    if (showDialog) {
        AddContactDialog(
            isSaving   = isSaving,
            isDark     = isDark,
            cardColor  = cardColor,
            textColor  = textColor,
            subColor   = subColor,
            accentRed  = emergencyRed,
            dialogMenuBg = dialogMenuBg,
            onDismiss  = { showDialog = false },
            onSave     = { contact -> saveContact(contact); showDialog = false }
        )
    }

    // ── Delete confirmation ───────────────────────────────────
    deleteTarget?.let { c ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = cardColor,
            icon    = { Icon(Icons.Filled.DeleteForever, null, tint = emergencyRed) },
            title   = { Text("Remove Contact?", fontWeight = FontWeight.Bold, color = textColor) },
            text    = { Text("Remove ${c.name} from emergency contacts?", color = subColor) },
            confirmButton = {
                TextButton(onClick = { deleteContact(c.id); deleteTarget = null }) {
                    Text("Remove", color = emergencyRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel", color = subColor) }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Emergency Contacts", fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE0F2F1) else TealDark)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                            tint = if (isDark) Color(0xFFE0F2F1) else TealDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { showDialog = true },
                containerColor = emergencyRed,
                contentColor   = Color.White,
                shape          = CircleShape
            ) { Icon(Icons.Filled.PersonAdd, "Add Contact") }
        },
        containerColor = bgColor
    ) { padding ->

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = emergencyRed)
            }
            return@Scaffold
        }

        if (contacts.isEmpty()) {
            // Empty state
            Column(
                Modifier.fillMaxSize().padding(padding).padding(32.dp),
                Arrangement.Center, Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(100.dp).clip(CircleShape)
                        .background(iconCircleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ContactPhone, null,
                        tint = emergencyRed, modifier = Modifier.size(52.dp))
                }
                Spacer(Modifier.height(20.dp))
                Text("No Emergency Contacts", fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = textColor)
                Spacer(Modifier.height(8.dp))
                Text("Add people who should be\ncontacted in an emergency",
                    fontSize = 14.sp, color = subColor,
                    textAlign = TextAlign.Center, lineHeight = 22.sp)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick   = { showDialog = true },
                    shape     = RoundedCornerShape(28.dp),
                    colors    = ButtonDefaults.buttonColors(containerColor = emergencyRed),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                    modifier  = Modifier.height(52.dp).fillMaxWidth(0.6f)
                ) { Text("Add Contact", fontWeight = FontWeight.Bold) }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding  = PaddingValues(vertical = 16.dp)
        ) {
            // SOS banner
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = emergencyRed),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Emergency, null, tint = Color.White,
                            modifier = Modifier.size(36.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Emergency SOS", fontWeight = FontWeight.Bold,
                                color = Color.White, fontSize = 16.sp)
                            Text("Tap a contact below to call immediately",
                                color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                        }
                    }
                }
            }

            items(contacts, key = { it.id }) { contact ->
                ContactCard(
                    contact     = contact,
                    cardColor   = cardColor,
                    textColor   = textColor,
                    subColor    = subColor,
                    accentRed   = emergencyRed,
                    badgeBg     = badgeBg,
                    deleteTint  = deleteTint,
                    onCall      = {
                        val intent = Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:${contact.phone}"))
                        context.startActivity(intent)
                    },
                    onDelete    = { deleteTarget = contact }
                )
            }

            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}

// ── Contact card ──────────────────────────────────────────────
@Composable
private fun ContactCard(
    contact   : EmergencyContact,
    cardColor : Color,
    textColor : Color,
    subColor  : Color,
    accentRed : Color,
    badgeBg   : Color,
    deleteTint: Color,
    onCall    : () -> Unit,
    onDelete  : () -> Unit
) {
    val avatarColor = if (contact.isPrimary) accentRed else TealPrimary

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with initials
            Box(
                Modifier.size(52.dp).clip(CircleShape)
                    .background(avatarColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(contact.initials(), fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = avatarColor)
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(contact.name, fontWeight = FontWeight.Bold,
                        fontSize = 15.sp, color = textColor)
                    if (contact.isPrimary) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier.background(badgeBg, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Primary", fontSize = 10.sp, color = accentRed,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Text(contact.relation, fontSize = 13.sp, color = subColor)
                Text(contact.phone, fontSize = 13.sp, color = TealPrimary,
                    fontWeight = FontWeight.SemiBold)
            }

            // Call button
            IconButton(
                onClick  = onCall,
                modifier = Modifier.size(44.dp).clip(CircleShape)
                    .background(accentRed)
            ) { Icon(Icons.Filled.Phone, "Call", tint = Color.White) }

            Spacer(Modifier.width(4.dp))

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, "Delete", tint = deleteTint)
            }
        }
    }
}

// ── Add contact dialog ────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddContactDialog(
    isSaving    : Boolean,
    isDark      : Boolean,
    cardColor   : Color,
    textColor   : Color,
    subColor    : Color,
    accentRed   : Color,
    dialogMenuBg: Color,
    onDismiss   : () -> Unit,
    onSave      : (EmergencyContact) -> Unit
) {
    var name        by remember { mutableStateOf("") }
    var phone       by remember { mutableStateOf("") }
    var relation    by remember { mutableStateOf("") }
    var isPrimary   by remember { mutableStateOf(false) }
    var showRelMenu by remember { mutableStateOf(false) }
    var nameError   by remember { mutableStateOf(false) }
    var phoneError  by remember { mutableStateOf(false) }

    val relations = listOf("Spouse", "Parent", "Child", "Sibling", "Friend", "Doctor", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardColor,
        title = { Text("Add Emergency Contact", fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFFE0F2F1) else TealDark) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it; nameError = false },
                    label         = { Text("Full Name") },
                    leadingIcon   = { Icon(Icons.Filled.Person, null, tint = TealPrimary) },
                    isError       = nameError,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(14.dp), singleLine = true,
                    colors        = dialogFieldColors(textColor, subColor)
                )

                OutlinedTextField(
                    value           = phone,
                    onValueChange   = { phone = it; phoneError = false },
                    label           = { Text("Phone Number") },
                    leadingIcon     = { Icon(Icons.Filled.Phone, null, tint = TealPrimary) },
                    isError         = phoneError,
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(14.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors          = dialogFieldColors(textColor, subColor)
                )

                ExposedDropdownMenuBox(showRelMenu, { showRelMenu = it }) {
                    OutlinedTextField(
                        value         = relation, onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Relation") },
                        leadingIcon   = { Icon(Icons.Filled.FamilyRestroom, null, tint = TealPrimary) },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(showRelMenu) },
                        modifier      = Modifier.fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape         = RoundedCornerShape(14.dp),
                        colors        = dialogFieldColors(textColor, subColor)
                    )
                    ExposedDropdownMenu(showRelMenu, { showRelMenu = false },
                        Modifier.background(dialogMenuBg)) {
                        relations.forEach { rel ->
                            DropdownMenuItem(
                                text    = { Text(rel, color = textColor) },
                                onClick = { relation = rel; showRelMenu = false }
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked         = isPrimary,
                        onCheckedChange = { isPrimary = it },
                        colors          = CheckboxDefaults.colors(checkedColor = accentRed)
                    )
                    Text("Set as primary contact", fontSize = 14.sp, color = textColor)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    nameError  = name.isBlank()
                    phoneError = phone.isBlank()
                    if (!nameError && !phoneError) {
                        onSave(EmergencyContact(
                            name      = name.trim(),
                            phone     = phone.trim(),
                            relation  = relation.ifBlank { "Other" },
                            isPrimary = isPrimary
                        ))
                    }
                },
                enabled   = !isSaving,
                shape     = RoundedCornerShape(28.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = accentRed),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                if (isSaving)
                    CircularProgressIndicator(color = Color.White,
                        modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else
                    Text("Save Contact", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = subColor) }
        }
    )
}

@Composable
private fun dialogFieldColors(textColor: Color, subColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = TealPrimary,
    focusedLabelColor    = TealPrimary,
    cursorColor          = TealPrimary,
    focusedTextColor     = textColor,
    unfocusedTextColor   = textColor,
    unfocusedLabelColor  = subColor,
    focusedLeadingIconColor   = TealPrimary,
    unfocusedLeadingIconColor = subColor
)