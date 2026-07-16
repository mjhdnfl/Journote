package com.naufal.cheddar.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.naufal.cheddar.ui.screens.entries.EntriesScreen
import com.naufal.cheddar.ui.screens.editor.EditorScreen
import com.naufal.cheddar.ui.screens.collections.CollectionsScreen
import com.naufal.cheddar.ui.screens.insights.InsightsScreen
import com.naufal.cheddar.ui.screens.settings.SettingsScreen
import com.naufal.cheddar.ui.screens.settings.AppearanceScreen
import com.naufal.cheddar.ui.screens.settings.DataScreen
import com.naufal.cheddar.ui.screens.settings.ThemeOption
import com.naufal.cheddar.ui.screens.trash.TrashScreen
import com.naufal.cheddar.ui.screens.archive.ArchiveScreen
import com.naufal.cheddar.ui.screens.about.AboutScreen
import com.naufal.cheddar.ui.screens.search.SearchScreen
import com.naufal.cheddar.ui.viewmodel.JournoteViewModel
import com.naufal.cheddar.ui.viewmodel.JournoteViewModelFactory
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Entries : Screen("entries", "Entries",
        Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Outlined.List
    )
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    object Collections : Screen("collections", "Collections", Icons.Filled.SpaceDashboard, Icons.Outlined.SpaceDashboard)
    object Insights : Screen("insights", "Insights", Icons.Filled.Insights, Icons.Outlined.Insights)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    object Appearance : Screen("appearance", "Appearance", Icons.Filled.Palette, Icons.Outlined.Palette)
    object Data : Screen("data", "Data", Icons.Filled.Tune, Icons.Outlined.Tune)
    object Archive : Screen("archive", "Archive", Icons.Filled.Archive, Icons.Outlined.Archive)
    object Trash : Screen("trash", "Trash", Icons.Filled.Delete, Icons.Outlined.Delete)
    object About : Screen("about", "About", Icons.Filled.Info, Icons.Outlined.Info)

    object Editor : Screen("editor?noteId={noteId}", "Editor", Icons.Filled.Edit, Icons.Outlined.Edit) {
        fun passId(id: Int) = "editor?noteId=$id"
    }
}

@Composable
fun JournoteApp(
    viewModelFactory: JournoteViewModelFactory,
    onThemeChange: (ThemeOption) -> Unit,
    onAmoledToggle: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val viewModel: JournoteViewModel = viewModel(factory = viewModelFactory)

    var hasSeenSplash by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomNavItems = listOf(Screen.Entries, Screen.Collections, Screen.Insights)

    val isBottomBarVisible = bottomNavItems.any { currentRoute?.contains(it.route) == true }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerShape = MaterialTheme.shapes.large
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Journote",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))

                DrawerItem("Settings", Icons.Outlined.Settings) {
                    navController.navigate(Screen.Settings.route)
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Manage Tags", Icons.Outlined.Style) { }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Data",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                DrawerItem("Backup & Sync", Icons.Outlined.CloudSync) { }
                DrawerItem("Export", Icons.Outlined.FileDownload) { }
                DrawerItem("Import", Icons.Outlined.FileUpload) { }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))

                DrawerItem("Archive", Icons.Outlined.Archive) {
                    navController.navigate(Screen.Archive.route)
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Trash", Icons.Outlined.Delete) {
                    navController.navigate(Screen.Trash.route)
                    scope.launch { drawerState.close() }
                }
                DrawerItem("About", Icons.Outlined.Info) {
                    navController.navigate(Screen.About.route)
                    scope.launch { drawerState.close() }
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (isBottomBarVisible) {
                    JournoteBottomBar(navController, bottomNavItems, currentRoute)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Entries.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Entries.route) {
                    val allNotes by viewModel.allNotes.collectAsState()
                    val activeNotes = allNotes.filter { !it.isTrashed && !it.isArchived }

                    EntriesScreen(
                        notes = activeNotes,
                        hasSeenSplash = hasSeenSplash,
                        onSplashFinished = { hasSeenSplash = true },
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSearchClick = { navController.navigate(Screen.Search.route) },
                        onFabClick = { navController.navigate("editor") },
                        onEntryClick = { noteId -> navController.navigate(Screen.Editor.passId(noteId)) }
                    )
                }

                composable(
                    route = Screen.Search.route,
                    enterTransition = { scaleIn(initialScale = 0.9f, animationSpec = tween(250)) + fadeIn(tween(250)) },
                    exitTransition = { scaleOut(targetScale = 0.9f, animationSpec = tween(250)) + fadeOut(tween(250)) }
                ) {
                    val allNotes by viewModel.allNotes.collectAsState()
                    SearchScreen(
                        notes = allNotes.filter { !it.isTrashed && !it.isArchived },
                        onBack = { navController.popBackStack() },
                        onEntryClick = { noteId -> navController.navigate(Screen.Editor.passId(noteId)) }
                    )
                }

                composable(
                    route = Screen.Editor.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType; defaultValue = -1 })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                    val note by (if (noteId != -1) viewModel.getNoteById(noteId) else flowOf(null)).collectAsState(initial = null)

                    EditorScreen(
                        note = note,
                        onBack = { navController.popBackStack() },
                        onSave = { title, content ->
                            viewModel.saveNote(id = note?.id ?: 0, title = title, content = content)
                            navController.popBackStack()
                        },
                        onDelete = { note?.let { viewModel.trashNote(it) }; navController.popBackStack() },
                        onArchive = { note?.let { viewModel.archiveNote(it) }; navController.popBackStack() }
                    )
                }

                composable(Screen.Collections.route) {
                    val allNotes by viewModel.allNotes.collectAsState()
                    CollectionsScreen(
                        archiveCount = allNotes.count { it.isArchived && !it.isTrashed },
                        onNavigateToArchive = { navController.navigate(Screen.Archive.route) }
                    )
                }

                composable(Screen.Archive.route) {
                    val allNotes by viewModel.allNotes.collectAsState()
                    ArchiveScreen(
                        archivedNotes = allNotes.filter { it.isArchived && !it.isTrashed },
                        onBack = { navController.popBackStack() },
                        onUnarchive = { viewModel.unarchiveNote(it) },
                        onEntryClick = { navController.navigate(Screen.Editor.passId(it)) }
                    )
                }

                composable(Screen.Insights.route) {
                    val allNotes by viewModel.allNotes.collectAsState()
                    InsightsScreen(notes = allNotes.filter { !it.isTrashed && !it.isArchived })
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToAppearance = { navController.navigate(Screen.Appearance.route) },
                        onNavigateToData = { navController.navigate(Screen.Data.route) }
                    )
                }

                composable(Screen.Appearance.route) {
                    AppearanceScreen(onBack = { navController.popBackStack() }, onThemeChange = onThemeChange, onAmoledToggle = onAmoledToggle)
                }

                composable(Screen.Data.route) { DataScreen(onBack = { navController.popBackStack() }) }

                composable(Screen.Trash.route) {
                    val allNotes by viewModel.allNotes.collectAsState()
                    TrashScreen(
                        trashedNotes = allNotes.filter { it.isTrashed },
                        onBack = { navController.popBackStack() },
                        onRestore = { viewModel.restoreNote(it) },
                        onDeleteForever = { viewModel.deleteNoteForever(it) }
                    )
                }

                composable(Screen.About.route) { AboutScreen(onBack = { navController.popBackStack() }) }
            }
        }
    }
}

@Composable
private fun DrawerItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        icon = { Icon(icon, contentDescription = label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Composable
private fun JournoteBottomBar(navController: NavHostController, items: List<Screen>, currentRoute: String?) {
    NavigationBar {
        items.forEach { screen ->
            val isSelected = currentRoute?.contains(screen.route) == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)) },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}