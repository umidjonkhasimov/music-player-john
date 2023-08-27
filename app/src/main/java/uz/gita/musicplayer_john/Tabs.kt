//package uz.gita.musicplayer_john
//
//import androidx.compose.material3.LeadingIconTab
//import androidx.compose.material3.TabRow
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.tooling.preview.Preview
//import com.google.accompanist.pager.ExperimentalPagerApi
//import com.google.accompanist.pager.HorizontalPager
//import com.google.accompanist.pager.PagerState
//import com.google.accompanist.pager.rememberPagerState
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun Tabs(tabs: List<TabItem>, pagerState: PagerState) {
//    val scope = rememberCoroutineScope()
//
//    TabRow(
//        selectedTabIndex = pagerState.currentPage,
//
//        ) {
//        tabs.forEachIndexed() { index, tabItem ->
//            LeadingIconTab(
//                icon = {},
//                selected = pagerState.currentPage == index,
//                onClick = {
//                    scope.launch {
//                        pagerState.animateScrollToPage(index)
//                    }
//                },
//                text = {
//                    Text(text = tabItem.title)
//                }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalPagerApi::class)
//@Preview
//@Composable
//fun TabsPreview() {
//    Tabs(
//        tabs = listOf(
//            TabItem.Music,
//            TabItem.Playlist
//        ), pagerState = rememberPagerState()
//    )
//}
//
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun TabsContent(tabs: List<TabItem>, pagerState: PagerState) {
//    HorizontalPager(state = pagerState, count = tabs.size) { page ->
//        tabs[page].screen
//    }
//}
//
//@OptIn(ExperimentalPagerApi::class)
//@Preview(showBackground = true)
//@Composable
//fun TabsContentPreview() {
//    val tabs = listOf(
//        TabItem.Music,
//        TabItem.Playlist
//    )
//    val pagerState = rememberPagerState()
//    TabsContent(tabs = tabs, pagerState = pagerState)
//}