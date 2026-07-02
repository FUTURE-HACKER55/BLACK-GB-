@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- REUSABLE BLUE VERIFIED BADGE ---
@Composable
fun BlueVerifiedBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Color(0xFF00A884)), // Beautiful WhatsApp Green or Twitter Blue Check
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Vérifié",
            tint = Color.Black,
            modifier = Modifier.size(10.dp)
        )
    }
}

// --- APP BAR EMBLEME LOGO "GB" NOIR ---
@Composable
fun BlackGBLogoBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "GB",
            color = WhatsAppAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

// --- MAIN SCREEN ACCUEIL ---
@Composable
fun MainScreen(viewModel: ChatViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showUpdateDialog by remember { mutableStateOf(true) }

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Mise à jour", tint = WhatsAppAccent)
                    Text(
                        text = "📢 Nouvelle mise à jour !",
                        color = WhatsAppTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "La version 1.1 de votre application Black GB est disponible ! Voici les nouvelles fonctionnalités installées :",
                        color = WhatsAppTextSecondary,
                        fontSize = 13.sp
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WhatsAppBg, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🛡️", fontSize = 14.sp)
                            Column {
                                Text("Récupération de Compte", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Si vous perdez l'accès, récupérez instantanément votre compte avec 'Mot de passe oublié'.", color = WhatsAppTextSecondary, fontSize = 11.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(WhatsAppSurface))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("👑", fontSize = 14.sp)
                            Column {
                                Text("Tableau de Bord Administrateur", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Statistiques d'inscription Gmail et surveillance de la base de données réservées aux administrateurs.", color = WhatsAppTextSecondary, fontSize = 11.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(WhatsAppSurface))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔒", fontSize = 14.sp)
                            Column {
                                Text("Sécurisation & Hashage", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Les mots de passe sont hashés et protégés. Conforme à la politique Google Play Store.", color = WhatsAppTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                    
                    Text(
                        text = "Toutes vos données et conversations existantes ont été conservées en toute sécurité.",
                        color = WhatsAppTextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showUpdateDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppAccent)
                ) {
                    Text("Super, j'ai compris ! 👍", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = WhatsAppSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BlackGBLogoBadge()
                        Column {
                            Text(
                                text = "Black GB",
                                color = WhatsAppTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Ultra-Sombre Connecté",
                                color = WhatsAppAccent,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    if (currentUser?.isVerified == false) {
                        Button(
                            onClick = { viewModel.verifyEmailSimulated() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Vérifier Email ⚠️", fontSize = 11.sp, color = Color.White)
                        }
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Déconnexion",
                            tint = WhatsAppTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhatsAppSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = WhatsAppSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == MainTab.CHATS,
                    onClick = { viewModel.selectTab(MainTab.CHATS) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
                    label = { Text("Discussions", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppAccent,
                        selectedTextColor = WhatsAppAccent,
                        unselectedIconColor = WhatsAppTextSecondary,
                        unselectedTextColor = WhatsAppTextSecondary,
                        indicatorColor = Color.Black
                    )
                )
                NavigationBarItem(
                    selected = currentTab == MainTab.STATUS,
                    onClick = { viewModel.selectTab(MainTab.STATUS) },
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Statut") },
                    label = { Text("Statut", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppAccent,
                        selectedTextColor = WhatsAppAccent,
                        unselectedIconColor = WhatsAppTextSecondary,
                        unselectedTextColor = WhatsAppTextSecondary,
                        indicatorColor = Color.Black
                    )
                )
                NavigationBarItem(
                    selected = currentTab == MainTab.CONTACTS,
                    onClick = { viewModel.selectTab(MainTab.CONTACTS) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Contacts") },
                    label = { Text("Contacts", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppAccent,
                        selectedTextColor = WhatsAppAccent,
                        unselectedIconColor = WhatsAppTextSecondary,
                        unselectedTextColor = WhatsAppTextSecondary,
                        indicatorColor = Color.Black
                    )
                )
                NavigationBarItem(
                    selected = currentTab == MainTab.PROFILE,
                    onClick = { viewModel.selectTab(MainTab.PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppAccent,
                        selectedTextColor = WhatsAppAccent,
                        unselectedIconColor = WhatsAppTextSecondary,
                        unselectedTextColor = WhatsAppTextSecondary,
                        indicatorColor = Color.Black
                    )
                )
                NavigationBarItem(
                    selected = currentTab == MainTab.SETTINGS,
                    onClick = { viewModel.selectTab(MainTab.SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Paramètres") },
                    label = { Text("Paramètres", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhatsAppAccent,
                        selectedTextColor = WhatsAppAccent,
                        unselectedIconColor = WhatsAppTextSecondary,
                        unselectedTextColor = WhatsAppTextSecondary,
                        indicatorColor = Color.Black
                    )
                )
            }
        },
        containerColor = WhatsAppBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.CHATS -> ChatsTab(viewModel)
                MainTab.STATUS -> StatusTab(viewModel)
                MainTab.CONTACTS -> ContactsTab(viewModel)
                MainTab.PROFILE -> ProfileTab(viewModel)
                MainTab.SETTINGS -> SettingsTab(viewModel)
            }
        }
    }
}

// --- TABS: 1. CHATS LIST ---
@Composable
fun ChatsTab(viewModel: ChatViewModel) {
    val conversations by viewModel.conversations.collectAsState()
    val context = LocalContext.current

    if (conversations.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Aucune discussion",
                    tint = WhatsAppAccent,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Aucune discussion active",
                    color = WhatsAppTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Allez dans l'onglet 'Contacts' pour démarrer un chat direct ou créer un groupe ! 🟢",
                    color = WhatsAppTextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(conversations) { convo ->
                val lastMsgState = remember { mutableStateOf<MessageEntity?>(null) }
                val membersState = remember { mutableStateOf<List<ProfileEntity>>(emptyList()) }

                // Load latest message and members reactively
                LaunchedEffect(convo.id) {
                    val db = com.example.data.db.AppDatabase.getDatabase(context)
                    db.messageDao().getLatestMessageForConversation(convo.id).collect { msg ->
                        lastMsgState.value = msg
                    }
                }
                LaunchedEffect(convo.id) {
                    val db = com.example.data.db.AppDatabase.getDatabase(context)
                    val list = db.conversationDao().getMembersForConversation(convo.id)
                    val resolved = list.mapNotNull { db.profileDao().getProfileById(it.userId) }
                    membersState.value = resolved
                }

                val currentUserId = viewModel.currentUser.value?.id ?: ""
                val otherMember = membersState.value.firstOrNull { it.id != currentUserId }
                val title = if (!convo.isGroup) {
                    otherMember?.displayName ?: convo.name
                } else {
                    convo.name
                }
                val avatar = if (!convo.isGroup) {
                    otherMember?.avatarUrl ?: convo.avatarUrl
                } else {
                    convo.avatarUrl
                }
                val isVerified = !convo.isGroup && otherMember?.isBlueVerified == true

                ListItem(
                    headlineContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = title,
                                color = WhatsAppTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isVerified) {
                                BlueVerifiedBadge()
                            }
                        }
                    },
                    supportingContent = {
                        val lastMsg = lastMsgState.value
                        val supportingText = when {
                            lastMsg == null -> "Aucun message"
                            lastMsg.deletedAt != null -> "🚫 Ce message a été supprimé."
                            lastMsg.mediaUrl != null -> "📸 Photo"
                            else -> lastMsg.content
                        }
                        Text(
                            text = supportingText,
                            color = WhatsAppTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingContent = {
                        AsyncImage(
                            model = avatar.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80" },
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentScale = ContentScale.Crop
                        )
                    },
                    trailingContent = {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val lastMsg = lastMsgState.value
                            if (lastMsg != null) {
                                val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                Text(
                                    text = sdf.format(java.util.Date(lastMsg.createdAt)),
                                    color = WhatsAppTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            if (convo.isGroup) {
                                Badge(
                                    containerColor = Color.Black,
                                    contentColor = WhatsAppAccent
                                ) {
                                    Text("Groupe", fontSize = 9.sp)
                                }
                            }
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        viewModel.openDirectConversationById(convo.id)
                    }
                )
                HorizontalDivider(color = WhatsAppBubbleReceived.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}

// --- TABS: 2. STATUS LIST ---
@Composable
fun StatusTab(viewModel: ChatViewModel) {
    val activeStatuses by viewModel.activeStatuses.collectAsState()
    val myStatuses by viewModel.myStatuses.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // "Mon Statut" (Create status shortcut)
        item {
            Text(
                text = "Mon statut",
                color = WhatsAppAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.NewStatus) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80",
                        contentDescription = "Mon Avatar",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(WhatsAppAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = "Mon statut",
                        color = WhatsAppTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (myStatuses.isNotEmpty()) "${myStatuses.size} statut(s) actif(s)" else "Ajouter un statut texte ou photo (expire en 24h)",
                        color = WhatsAppTextSecondary,
                        fontSize = 13.sp
                    )
                }
                if (myStatuses.isNotEmpty()) {
                    IconButton(onClick = {
                        viewModel.deleteStatusById(myStatuses.first().id)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red)
                    }
                }
            }
        }

        // Recent Status Updates from other contacts
        item {
            Text(
                text = "Mises à jour récentes",
                color = WhatsAppTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (activeStatuses.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WhatsAppSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PhotoAlbum, contentDescription = "Vide", tint = WhatsAppTextSecondary)
                        Text(
                            text = "Aucun statut récent disponible",
                            color = WhatsAppTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Les statuts de vos contacts apparaîtront ici pendant 24h.",
                            color = WhatsAppTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(activeStatuses) { status ->
                val senderProfile = remember { mutableStateOf<ProfileEntity?>(null) }
                LaunchedEffect(status.userId) {
                    val db = com.example.data.db.AppDatabase.getDatabase(context)
                    senderProfile.value = db.profileDao().getProfileById(status.userId)
                }

                senderProfile.value?.let { sender ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.navigateTo(Screen.StatusViewer(status.id))
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Colored ring for unviewed status indicator
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .border(2.5.dp, WhatsAppAccent, CircleShape)
                                .padding(3.dp)
                        ) {
                            AsyncImage(
                                model = sender.avatarUrl,
                                contentDescription = "Sender Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color.Black),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = sender.displayName,
                                    color = WhatsAppTextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                if (sender.isBlueVerified) {
                                    BlueVerifiedBadge()
                                }
                            }
                            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                            Text(
                                text = "Posté à ${sdf.format(java.util.Date(status.createdAt))}",
                                color = WhatsAppTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                    HorizontalDivider(color = WhatsAppBubbleReceived.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }
        }
    }
}

// --- TABS: 3. CONTACTS LIST & GROUPS ACTION ---
@Composable
fun ContactsTab(viewModel: ChatViewModel) {
    val contacts by viewModel.contacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Group Creator Button
        Button(
            onClick = { viewModel.navigateTo(Screen.NewGroup) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, WhatsAppAccent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.GroupAdd, contentDescription = "Créer groupe", tint = WhatsAppAccent)
                Text("Créer un groupe de discussion", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold)
            }
        }

        // Contact Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Chercher par @pseudo ou email...", color = WhatsAppTextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Chercher", tint = WhatsAppTextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WhatsAppSurface,
                unfocusedContainerColor = WhatsAppSurface,
                focusedBorderColor = WhatsAppAccent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = WhatsAppTextPrimary,
                unfocusedTextColor = WhatsAppTextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Utilisateurs disponibles",
            color = WhatsAppTextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucun contact trouvé. Tapez un pseudo valide.",
                    color = WhatsAppTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contacts) { contact ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = WhatsAppSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.openConversation(contact.id)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = contact.avatarUrl,
                                contentDescription = "Contact avatar",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1.0f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = contact.displayName,
                                        color = WhatsAppTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    if (contact.isBlueVerified) {
                                        BlueVerifiedBadge()
                                    }
                                }
                                Text(
                                    text = contact.username,
                                    color = WhatsAppAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = contact.bio,
                                    color = WhatsAppTextSecondary,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Ouvrir chat",
                                tint = WhatsAppAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- TABS: 4. EDIT PROFILE WITH VIP BLUE CHECK OPTIONS ---
@Composable
fun ProfileTab(viewModel: ChatViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()

    currentUser?.let { user ->
        var displayName by remember { mutableStateOf(user.displayName) }
        var bio by remember { mutableStateOf(user.bio) }
        var avatarUrl by remember { mutableStateOf(user.avatarUrl) }
        var savedFeedback by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = avatarUrl.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80" },
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, WhatsAppAccent, CircleShape)
                        .background(Color.Black),
                    contentScale = ContentScale.Crop
                )
                if (user.isBlueVerified) {
                    Box(
                        modifier = Modifier
                            .offset(x = 6.dp, y = 6.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(1.5.dp, WhatsAppAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        BlueVerifiedBadge(modifier = Modifier.size(20.dp))
                    }
                }
            }

            Text(
                text = user.email,
                color = WhatsAppTextSecondary,
                fontSize = 14.sp
            )

            // Dynamic save confirmation banner
            AnimatedVisibility(visible = savedFeedback) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WhatsAppBubbleSent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Profil mis à jour avec succès ! 🟢🖤",
                        color = Color.White,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Input Fields
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Nom affiché") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WhatsAppAccent,
                    unfocusedBorderColor = WhatsAppBubbleReceived,
                    focusedTextColor = WhatsAppTextPrimary,
                    unfocusedTextColor = WhatsAppTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Actu / Bio") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WhatsAppAccent,
                    unfocusedBorderColor = WhatsAppBubbleReceived,
                    focusedTextColor = WhatsAppTextPrimary,
                    unfocusedTextColor = WhatsAppTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = avatarUrl,
                onValueChange = { avatarUrl = it },
                label = { Text("URL Photo de profil") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WhatsAppAccent,
                    unfocusedBorderColor = WhatsAppBubbleReceived,
                    focusedTextColor = WhatsAppTextPrimary,
                    unfocusedTextColor = WhatsAppTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // VIP BLUE BADGE CONFIGURATOR (Requested feature)
            Card(
                colors = CardDefaults.cardColors(containerColor = WhatsAppSurface),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, WhatsAppAccent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1.0f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Option Badge Bleu VIP", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            BlueVerifiedBadge()
                        }
                        Text("Activer la vérification VIP de votre compte sur tout Black GB.", color = WhatsAppTextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = user.isBlueVerified,
                        onCheckedChange = { viewModel.toggleBlueVerified() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = WhatsAppAccent,
                            uncheckedThumbColor = WhatsAppTextSecondary,
                            uncheckedTrackColor = WhatsAppBubbleReceived
                        )
                    )
                }
            }

            // Simulated Email verification link trigger
            if (!user.isVerified) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FF3D00)),
                    border = BorderStroke(1.dp, Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📧 Compte non vérifié par email !",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Pour lever les restrictions simulées de votre client, activez votre email directement ici :",
                            color = WhatsAppTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.verifyEmailSimulated() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Simuler l'activation par email", color = Color.White)
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WhatsAppBubbleSent.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = "Vérifié", tint = WhatsAppAccent)
                        Column {
                            Text("Compte vérifié par Email", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Votre adresse email est officiellement certifiée.", color = WhatsAppTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.updateProfileInfo(displayName, bio, avatarUrl)
                    scope.launch {
                        savedFeedback = true
                        delay(2500)
                        savedFeedback = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Sauvegarder", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// --- TABS: 5. SETTINGS TAB ---
@Composable
fun SettingsTab(viewModel: ChatViewModel) {
    val registeredProfiles by viewModel.registeredProfiles.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRegisteredProfiles()
    }

    val totalUsers = registeredProfiles.size
    val gmailUsers = registeredProfiles.count { it.email.lowercase().endsWith("@gmail.com") }
    val isAdmin = currentUser?.email?.lowercase() == "jd6398926@gmail.com"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        BlackGBLogoBadge(modifier = Modifier.size(72.dp))
        Text(
            text = "Black GB - Paramètres",
            color = WhatsAppTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Text(
            text = if (isAdmin) "👑 Session Administrateur" else "Édition Ultra-Dark Pro v1.0",
            color = WhatsAppAccent,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )

        if (isAdmin) {
            // --- ADMIN ONLY SECTION ---
            Text(
                text = "👑 TABLEAU DE BORD ADMIN",
                color = WhatsAppAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            // Statistics Card
            Card(
                colors = CardDefaults.cardColors(containerColor = WhatsAppSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "📊 Statistiques d'Inscription",
                        color = WhatsAppAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Utilisateurs :", color = WhatsAppTextPrimary)
                        Text("$totalUsers", color = WhatsAppAccent, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Utilisateurs Gmail :", color = WhatsAppTextPrimary)
                        Text("$gmailUsers", color = WhatsAppAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Security Notice Card
            Card(
                colors = CardDefaults.cardColors(containerColor = WhatsAppSurface),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, WhatsAppAccent.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔒 Sécurité & Confidentialité (Google Play)",
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Les mots de passe sont hashés et stockés de manière sécurisée et irréversible. Pour être conforme aux règles du Google Play Store et protéger la vie privée des utilisateurs, l'affichage en clair des mots de passe est strictement bloqué par défaut. Cela évite le bannissement de votre application pour espionnage.",
                        color = WhatsAppTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // User Accounts list for Admin view
            Card(
                colors = CardDefaults.cardColors(containerColor = WhatsAppSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "👥 Comptes Enregistrés (Simulation)",
                        color = WhatsAppTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    if (registeredProfiles.isEmpty()) {
                        Text("Aucun utilisateur inscrit.", color = WhatsAppTextSecondary, fontSize = 12.sp)
                    } else {
                        registeredProfiles.forEach { profile ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(WhatsAppBg, shape = RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = profile.displayName,
                                    color = WhatsAppTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = profile.username,
                                    color = WhatsAppAccent,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Email: ${profile.email}",
                                    color = WhatsAppTextSecondary,
                                    fontSize = 11.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Mot de passe: [SÉCURISÉ / HASHÉ]",
                                        color = Color.LightGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (profile.isVerified) "Vérifié" else "Non vérifié",
                                        color = if (profile.isVerified) WhatsAppAccent else Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        } else {
            // --- STANDARD USER SECTION ---
            Card(
                colors = CardDefaults.cardColors(containerColor = WhatsAppSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "👤 Mon Compte",
                        color = WhatsAppAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Nom :", color = WhatsAppTextSecondary)
                        Text(currentUser?.displayName ?: "Utilisateur", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Email :", color = WhatsAppTextSecondary)
                        Text(currentUser?.email ?: "Aucun", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Statut :", color = WhatsAppTextSecondary)
                        Text(
                            text = if (currentUser?.isVerified == true) "Compte Vérifié ✓" else "Non vérifié ⚠️",
                            color = if (currentUser?.isVerified == true) WhatsAppAccent else Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Recovery Helper Card for standard users
            Card(
                colors = CardDefaults.cardColors(containerColor = WhatsAppSurface),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, WhatsAppAccent.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔑 Récupération de compte",
                        color = WhatsAppAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Si vous perdez l'accès à votre compte, vous pouvez cliquer sur 'Mot de passe oublié' lors de la connexion. Un jeton de récupération sécurisé sera simulé pour vous permettre de définir un nouveau mot de passe instantanément.",
                        color = WhatsAppTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Se déconnecter", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

// --- CHAT DETAILS THREAD (TEXT + PHOTOS, REALTIME) ---
@Composable
fun ChatDetailsScreen(viewModel: ChatViewModel) {
    val convo by viewModel.activeConversation.collectAsState()
    val messages by viewModel.activeConversationMessages.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    convo?.let { c ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = c.avatarUrl.ifEmpty { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80" },
                                contentDescription = "Avatar Recipient",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = c.name,
                                        color = WhatsAppTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    if (c.name == "Black-Carl GB" || c.name.contains("Carl")) {
                                        BlueVerifiedBadge()
                                    }
                                }
                                Text(
                                    text = if (c.isGroup) "Groupe actif" else if (c.name.contains("Carl")) "Assistant Gemini en ligne 🟢" else "En ligne",
                                    color = WhatsAppAccent,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.navigateTo(Screen.Main) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = WhatsAppAccent)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppSurface)
                )
            },
            containerColor = WhatsAppBg
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF070C10)) // Specific WhatsApp Wallpaper Dark
            ) {
                // Messages thread
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(messages) { msg ->
                        val isMe = msg.senderId == currentUser?.id
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (isMe) 12.dp else 0.dp,
                                            bottomEnd = if (isMe) 0.dp else 12.dp
                                        )
                                    )
                                    .background(if (isMe) WhatsAppBubbleSent else WhatsAppBubbleReceived)
                                    .padding(10.dp)
                            ) {
                                if (c.isGroup && !isMe) {
                                    Text(
                                        text = if (msg.senderId == "black_carl_ai") "Black-Carl GB 🖤" else "Membre",
                                        color = WhatsAppAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }

                                if (msg.mediaUrl != null) {
                                    AsyncImage(
                                        model = msg.mediaUrl,
                                        contentDescription = "Image message",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Text(
                                    text = msg.content,
                                    color = WhatsAppTextPrimary,
                                    fontSize = 15.sp
                                )

                                val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                Text(
                                    text = sdf.format(java.util.Date(msg.createdAt)),
                                    color = WhatsAppTextSecondary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }

                // Input Bar with preset media sender helper
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WhatsAppSurface)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Send media simulated shortcut
                    IconButton(onClick = {
                        // Simulates picking/uploading a curated photo to chat-media
                        val curatedPhotos = listOf(
                            "https://images.unsplash.com/photo-1579202673506-ca3ce28943ef?auto=format&fit=crop&w=400&q=80",
                            "https://images.unsplash.com/photo-1511556532299-8f662fc26c06?auto=format&fit=crop&w=400&q=80",
                            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=400&q=80"
                        )
                        val randomPhoto = curatedPhotos.random()
                        viewModel.postMessage("Je vous partage cette photo ! 📸", randomPhoto, "image")
                    }) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Envoyer Photo", tint = WhatsAppAccent)
                    }

                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Message...", color = WhatsAppTextSecondary) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = WhatsAppTextPrimary,
                            unfocusedTextColor = WhatsAppTextPrimary
                        ),
                        modifier = Modifier.weight(1.0f)
                    )

                    IconButton(
                        onClick = {
                            if (textInput.trim().isNotEmpty()) {
                                viewModel.postMessage(textInput.trim())
                                textInput = ""
                            }
                        },
                        enabled = textInput.trim().isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Envoyer",
                            tint = if (textInput.trim().isNotEmpty()) WhatsAppAccent else WhatsAppTextSecondary
                        )
                    }
                }
            }
        }
    }
}

// --- CREATE STATUS SCREEN (TEXT STATUT WITH CUSTOM BACKGROUND COLS) ---
@Composable
fun NewStatusScreen(viewModel: ChatViewModel) {
    var textInput by remember { mutableStateOf("") }
    val colors = listOf("#111B21", "#005C4B", "#1A237E", "#311B92", "#880E4F", "#000000")
    var selectedColor by remember { mutableStateOf(colors.first()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Créer un statut", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Main) }) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = WhatsAppAccent)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (textInput.trim().isNotEmpty()) {
                                viewModel.postTextStatus(textInput.trim(), selectedColor)
                            }
                        },
                        enabled = textInput.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppAccent)
                    ) {
                        Text("Publier", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppSurface)
            )
        },
        containerColor = Color(android.graphics.Color.parseColor(selectedColor))
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Statut Text Input
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text(
                        "Tapez votre statut ici...",
                        color = WhatsAppTextPrimary.copy(alpha = 0.6f),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    color = WhatsAppTextPrimary,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = WhatsAppTextPrimary,
                    unfocusedTextColor = WhatsAppTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Dynamic color picker helper and camera simulation choice
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(col)))
                                .border(
                                    width = if (selectedColor == col) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = col }
                        )
                    }
                }

                Button(
                    onClick = {
                        // Post custom curated photo as status instantly
                        val randomWallpaper = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=500&q=80"
                        viewModel.postMediaStatus("Nouvelle photo de statut ! 📱🖤", randomWallpaper)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(1.dp, WhatsAppAccent)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Camera, contentDescription = "Photo Status", tint = WhatsAppAccent)
                        Text("Poster plutôt un Statut Photo 📸", color = WhatsAppTextPrimary)
                    }
                }
            }
        }
    }
}

// --- FULLSCREEN STATUS STORY VIEWER STYLE WHATSAPP ---
@Composable
fun StatusViewerScreen(viewModel: ChatViewModel, statusId: String) {
    val activeStatuses by viewModel.activeStatuses.collectAsState()
    val status = activeStatuses.find { it.id == statusId }
    val views by viewModel.activeStatusViews.collectAsState()

    val context = LocalContext.current
    val sender = remember { mutableStateOf<ProfileEntity?>(null) }

    LaunchedEffect(status) {
        status?.let {
            val db = com.example.data.db.AppDatabase.getDatabase(context)
            sender.value = db.profileDao().getProfileById(it.userId)
            viewModel.viewStatusItem(it.id)
        }
    }

    if (status == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Statut expiré ou introuvable", color = Color.White)
        }
    } else {
        var progress by remember { mutableStateOf(0f) }

        // Progress timer of 5 seconds
        LaunchedEffect(Unit) {
            val duration = 5000f
            val steps = 100
            val stepTime = (duration / steps).toLong()
            for (i in 1..steps) {
                delay(stepTime)
                progress = i / steps.toFloat()
            }
            viewModel.navigateTo(Screen.Main)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (status.mediaUrl == null) Color(android.graphics.Color.parseColor(status.backgroundColor ?: "#111B21"))
                    else Color.Black
                )
        ) {
            // Fullscreen Photo status background if available
            if (status.mediaUrl != null) {
                AsyncImage(
                    model = status.mediaUrl,
                    contentDescription = "Photo de Statut",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark shade overlay
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
            }

            // Top Status Control Deck
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Progress Bar indicator
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = WhatsAppAccent,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = sender.value?.avatarUrl,
                            contentDescription = "Sender Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = sender.value?.displayName ?: "Utilisateur",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (sender.value?.isBlueVerified == true) {
                                    BlueVerifiedBadge()
                                }
                            }
                            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                            Text(
                                text = sdf.format(java.util.Date(status.createdAt)),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.navigateTo(Screen.Main) }) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                    }
                }
            }

            // Status message centered content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = status.content,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = LocalTextStyle.current.copy(
                        lineHeight = 32.sp
                    )
                )
            }

            // Status views details deck at the bottom
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "Vues", tint = WhatsAppAccent)
                    Text(
                        text = "Vu par ${views.size} personne(s)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Uniquement visible par vous et vos contacts.",
                        color = WhatsAppTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// --- NEW GROUP CREATOR SCREEN ---
@Composable
fun NewGroupScreen(viewModel: ChatViewModel) {
    var groupName by remember { mutableStateOf("") }
    val contacts by viewModel.contacts.collectAsState()
    val selectedMembers = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Créer un groupe", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Main) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = WhatsAppAccent)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (groupName.trim().isNotEmpty() && selectedMembers.isNotEmpty()) {
                                viewModel.createGroup(groupName.trim(), selectedMembers.toList())
                            }
                        },
                        enabled = groupName.trim().isNotEmpty() && selectedMembers.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppAccent)
                    ) {
                        Text("Créer", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppSurface)
            )
        },
        containerColor = WhatsAppBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                placeholder = { Text("Nom du groupe...", color = WhatsAppTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WhatsAppAccent,
                    unfocusedBorderColor = WhatsAppBubbleReceived,
                    focusedTextColor = WhatsAppTextPrimary,
                    unfocusedTextColor = WhatsAppTextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Sélectionner les participants (${selectedMembers.size})",
                color = WhatsAppTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contacts) { contact ->
                    // Avoid grouping with oneself or if it's Carl system bot
                    val isChecked = selectedMembers.contains(contact.id)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) WhatsAppBubbleSent.copy(alpha = 0.3f) else WhatsAppSurface
                        ),
                        border = BorderStroke(1.dp, if (isChecked) WhatsAppAccent else Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedMembers.remove(contact.id)
                                else selectedMembers.add(contact.id)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = contact.avatarUrl,
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(contact.displayName, color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold)
                                        if (contact.isBlueVerified) {
                                            BlueVerifiedBadge()
                                        }
                                    }
                                    Text(contact.username, color = WhatsAppTextSecondary, fontSize = 12.sp)
                                }
                            }
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    if (isChecked) selectedMembers.remove(contact.id)
                                    else selectedMembers.add(contact.id)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = WhatsAppAccent,
                                    uncheckedColor = WhatsAppTextSecondary,
                                    checkmarkColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- COMPREHENSIVE REGISTRATION + LOGIN + FORGOT PASSWORD SCREEN ---
@Composable
fun AuthScreen(viewModel: ChatViewModel) {
    var isLoginTab by remember { mutableStateOf(true) }
    var isForgotPasswordView by remember { mutableStateOf(false) }

    // Forms states
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var resetToken by remember { mutableStateOf("") }
    var showTokenResetInput by remember { mutableStateOf(false) }

    val authError by viewModel.authError.collectAsState()
    val authSuccessMessage by viewModel.authSuccessMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WhatsAppBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Logo Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BlackGBLogoBadge(modifier = Modifier.size(54.dp))
                Column {
                    Text("Black GB", color = WhatsAppTextPrimary, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    Text("Dark WhatsApp Clone", color = WhatsAppAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Error display block
            authError?.let { err ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FF3D00)),
                    border = BorderStroke(1.dp, Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ $err",
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Success display block
            authSuccessMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = WhatsAppBubbleSent.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, WhatsAppAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        color = WhatsAppAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (isForgotPasswordView) {
                // Forgot Password Layout
                Text(
                    text = "Réinitialiser mot de passe",
                    color = WhatsAppTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Votre Email Gmail") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhatsAppAccent,
                        unfocusedBorderColor = WhatsAppBubbleReceived,
                        focusedTextColor = WhatsAppTextPrimary,
                        unfocusedTextColor = WhatsAppTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showTokenResetInput) {
                    OutlinedTextField(
                        value = resetToken,
                        onValueChange = { resetToken = it },
                        label = { Text("Token reçu (simulé)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhatsAppAccent,
                            unfocusedBorderColor = WhatsAppBubbleReceived,
                            focusedTextColor = WhatsAppTextPrimary,
                            unfocusedTextColor = WhatsAppTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Nouveau mot de passe (min. 8)") },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhatsAppAccent,
                            unfocusedBorderColor = WhatsAppBubbleReceived,
                            focusedTextColor = WhatsAppTextPrimary,
                            unfocusedTextColor = WhatsAppTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (email.trim().isNotEmpty() && resetToken.trim().isNotEmpty() && password.length >= 8) {
                                viewModel.verifyForgotPasswordToken(email.trim(), resetToken.trim(), password)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Confirmer nouveau mot de passe", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (email.trim().isNotEmpty()) {
                                viewModel.requestPasswordReset(email.trim())
                                showTokenResetInput = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Obtenir token de réinitialisation", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = {
                    isForgotPasswordView = false
                    showTokenResetInput = false
                }) {
                    Text("Retour à la connexion", color = WhatsAppAccent)
                }

            } else {
                // Connection Tabs Header
                TabRow(
                    selectedTabIndex = if (isLoginTab) 0 else 1,
                    containerColor = WhatsAppSurface,
                    contentColor = WhatsAppAccent,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            color = WhatsAppAccent,
                            modifier = Modifier.tabIndicatorOffset(tabPositions[if (isLoginTab) 0 else 1])
                        )
                    }
                ) {
                    Tab(
                        selected = isLoginTab,
                        onClick = { isLoginTab = true },
                        text = { Text("Se connecter", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                    )
                    Tab(
                        selected = !isLoginTab,
                        onClick = { isLoginTab = false },
                        text = { Text("S'inscrire", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Common Inputs
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse email Gmail") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhatsAppAccent,
                        unfocusedBorderColor = WhatsAppBubbleReceived,
                        focusedTextColor = WhatsAppTextPrimary,
                        unfocusedTextColor = WhatsAppTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (!isLoginTab) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nom d'utilisateur (@pseudo)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhatsAppAccent,
                            unfocusedBorderColor = WhatsAppBubbleReceived,
                            focusedTextColor = WhatsAppTextPrimary,
                            unfocusedTextColor = WhatsAppTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Nom complet") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhatsAppAccent,
                            unfocusedBorderColor = WhatsAppBubbleReceived,
                            focusedTextColor = WhatsAppTextPrimary,
                            unfocusedTextColor = WhatsAppTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe (min. 8)") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhatsAppAccent,
                        unfocusedBorderColor = WhatsAppBubbleReceived,
                        focusedTextColor = WhatsAppTextPrimary,
                        unfocusedTextColor = WhatsAppTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Submittals
                if (isLoginTab) {
                    Button(
                        onClick = {
                            if (email.trim().isNotEmpty() && password.isNotEmpty()) {
                                viewModel.loginUser(email.trim(), password)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Connexion", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { isForgotPasswordView = true }) {
                            Text("Mot de passe oublié ?", color = WhatsAppAccent, fontSize = 13.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            if (email.trim().isNotEmpty() && username.trim().isNotEmpty() && displayName.trim().isNotEmpty() && password.length >= 8) {
                                viewModel.registerUser(email.trim(), username.trim(), displayName.trim(), password)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("S'inscrire", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
