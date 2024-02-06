package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import api.FakeGithubService
import api.GitRepo
import api.GitUser
import com.seiko.imageloader.rememberImagePainter
import com.seiko.uistate.UiState
import com.seiko.uistate.getOrElse
import com.seiko.uistate.onFailure
import com.seiko.uistate.onLoading
import com.seiko.uistate.onSuccess
import com.seiko.uistate.toUiState
import moe.tlaster.precompose.molecule.producePresenter
import util.emptyList
import util.onEmptyList

@Composable
fun HomeScene() {
    val state by producePresenter { CounterPresenter() }
    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            state.onSuccess { data ->
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        UserInfoCard(data.user)
                    }
                    item {
                        FollowersContent(data.followers)
                    }
                    UserReposContent(data.repos)
                }
            }.onLoading {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }.onFailure {
                Text(it.message.orEmpty())
            }
        }
    }
}

@Composable
private fun UserInfoCard(
    user: GitUser,
    modifier: Modifier = Modifier,
) {
    Surface(modifier, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.fillMaxWidth()) {
            ListItem(
                leadingContent = {
                    Image(
                        rememberImagePainter(user.avatarUrl),
                        contentDescription = "avatar",
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                    )
                },
                headlineContent = {
                    Text(user.name)
                },
                supportingContent = {
                    Text("@" + user.login)
                },
            )
        }
    }
}

@Composable
private fun FollowersContent(state: UiState<List<GitUser>>) {
    Surface(shape = MaterialTheme.shapes.medium) {
        Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) {
            state.onSuccess { list ->
                LazyRow(
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(list) { user ->
                        Column(Modifier.width(70.dp)) {
                            Image(
                                rememberImagePainter(user.avatarUrl),
                                null,
                                Modifier.size(56.dp).clip(CircleShape),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                user.login,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }.onLoading {
                CircularProgressIndicator()
            }.onFailure {
                Text(it.message.orEmpty())
            }.onEmptyList {
                Text("no followers")
            }
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.UserReposContent(state: UiState<List<GitRepo>>) {
    state.onSuccess { list ->
        items(list) { item ->
            ListItem(
                headlineContent = {
                    Text(item.name)
                },
                supportingContent = {
                    Text(item.fullName)
                },
                modifier = Modifier.clip(shape = MaterialTheme.shapes.medium),
            )
        }
    }.onLoading {
        item {
            Box(
                Modifier.fillMaxWidth().height(300.dp),
                Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun CounterPresenter(): UiState<HomeState> {
    val gitUser = produceState(UiState.loading()) {
        value = runCatching {
            FakeGithubService.getUser("qdsfdhvh")
        }.toUiState()
    }.getOrElse {
        return it.swap()
    }

    val followers by produceState(UiState.loading()) {
        value = runCatching {
            FakeGithubService.getFollowers(gitUser.followersUrl)
        }.toUiState {
            if (it.isEmpty()) UiState.emptyList()
            else UiState.success(it)
        }
    }

    val repos by produceState(UiState.loading()) {
        value = runCatching {
            FakeGithubService.getRepos(gitUser.reposUrl)
        }.toUiState()
    }

    return UiState.success(
        HomeState(
            user = gitUser,
            followers = followers,
            repos = repos,
            event = {

            }
        )
    )
}

private data class HomeState(
    val user: GitUser,
    val followers: UiState<List<GitUser>>,
    val repos: UiState<List<GitRepo>>,
    val event: (HomeEvent) -> Unit,
)

private sealed interface HomeEvent {
}
