package com.ekin.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ekin.core.ui.theme.EkinTheme
import com.ekin.feature.onboarding.OnboardingScreen
import com.ekin.feature.auth.LoginScreen
import com.ekin.feature.auth.RegisterScreen
import com.ekin.feature.map.MapScreen
import com.ekin.feature.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

sealed class Screen(val title: String, val icon: ImageVector, val route: String) {
    object Home : Screen("Ana Sayfa", Icons.Default.Home, "home")
    object Map : Screen("Harita", Icons.Default.Map, "map")
    object Explore : Screen("Keşfet", Icons.Default.Explore, "explore")
    object Profile : Screen("Profil", Icons.Default.Person, "profile")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EkinTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) "home" else "onboarding"

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    navController.navigate("home") { popUpTo("onboarding") { inclusive = true } }
                }
        } catch (_: Exception) { /* no-op */ }
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = { navController.navigate("auth/login") }
            )
        }
        composable("auth/login") {
            val context = androidx.compose.ui.platform.LocalContext.current
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(com.ekin.app.R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } },
                onNavigateRegister = { navController.navigate("auth/register") },
                onGoogleSignInClick = { launcher.launch(googleSignInClient.signInIntent) }
            )
        }
        composable("auth/register") {
            RegisterScreen(
                onRegistered = { navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable("home") {
            MainScreen(
                onLogout = {
                    navController.navigate("auth/login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var cameraLauncher by remember { mutableStateOf<(() -> Unit)?>(null) }

    val screens = listOf(Screen.Home, Screen.Map, Screen.Explore, Screen.Profile)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            text = "e-kin",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tarım verilerini keşfet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }

                // Navigation Items
                screens.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selectedScreen == screen,
                        onClick = {
                            selectedScreen = screen
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 2.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menü",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = selectedScreen.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        )
                    }
                }
        },
        floatingActionButton = {
                if (selectedScreen == Screen.Home && cameraLauncher != null) {
            FloatingActionButton(
                        onClick = { cameraLauncher?.invoke() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Bitki fotoğrafı çek",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                    }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
            when (selectedScreen) {
                is Screen.Home -> HomeContent(
                    innerPadding = innerPadding,
                    onCameraLauncherReady = { launcher -> cameraLauncher = launcher }
                )
                is Screen.Map -> MapScreen()
                is Screen.Explore -> ExploreContent(innerPadding)
                is Screen.Profile -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

@Composable
fun HomeContent(
    innerPadding: PaddingValues,
    onCameraLauncherReady: ((() -> Unit) -> Unit)? = null
) {
    val search = remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var plantImages by remember { mutableStateOf<List<PlantImage>>(emptyList()) }

    LaunchedEffect(Unit) {
        plantImages = ImageStorage.getUserPlantImages()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
        scope.launch {
                val result = ImageStorage.uploadPlantImage(it)
                result.onSuccess {
                    snackbarHostState.showSnackbar("Fotoğraf başarıyla yüklendi!")
                    plantImages = ImageStorage.getUserPlantImages()
                }.onFailure { error ->
                    snackbarHostState.showSnackbar("Hata: ${error.message}")
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Kamera izni olmadan fotoğraf çekemezsin.") }
        }
    }

    val launchCamera: () -> Unit = remember {
        {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
                cameraLauncher.launch(null)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Expose launchCamera to parent
    LaunchedEffect(Unit) {
        onCameraLauncherReady?.invoke(launchCamera)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        OutlinedTextField(
            value = search.value,
            onValueChange = { search.value = it },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(24.dp),
            label = { Text("Bitki ara…") }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { 
                    search.value = TextFieldValue("Elma")
                    scope.launch {
                        snackbarHostState.showSnackbar("Elma aranıyor...")
                    }
                },
                label = { Text("Elma") }
            )
            AssistChip(
                onClick = { 
                    search.value = TextFieldValue("Buğday")
                    scope.launch {
                        snackbarHostState.showSnackbar("Buğday aranıyor...")
                    }
                },
                label = { Text("Buğday") }
            )
            AssistChip(
                onClick = { 
                    search.value = TextFieldValue("Domates")
                    scope.launch {
                        snackbarHostState.showSnackbar("Domates aranıyor...")
                    }
                },
                label = { Text("Domates") }
            )
        }
        ShimmerBanner()
        
        // Hava Durumu Widget
        WeatherWidget()
        
        // Yüklenen Fotoğraflar
        if (plantImages.isNotEmpty()) {
            Text(
                text = "Yüklenen Bitki Fotoğrafları",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(plantImages) { image ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        AsyncImage(
                            model = image.imageUrl,
                            contentDescription = "Bitki fotoğrafı",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Haberler", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Text("Şimdilik taslak haber akışı. Yakında bitki ve tarım haberleri burada.", color = MaterialTheme.colorScheme.onSurface)
            }
        }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ExploreContent(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Keşfet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Yeni bitki önerileri ve bilgi kartları bu bölümde yer alacak.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun ShimmerBanner() {
    val transition = rememberInfiniteTransition()
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing))
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
        ),
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(x = offset, y = offset)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(brush, RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
    )
}
