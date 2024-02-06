package api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface GithubApi {
    suspend fun getUser(userName: String): GitUser
    suspend fun getFollowers(url: String): List<GitUser>
    suspend fun getRepos(url: String): List<GitRepo>
}


@Serializable
data class GitUser(
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("followers_url")
    val followersUrl: String,
    @SerialName("repos_url")
    val reposUrl: String,
    val name: String = "",
)

@Serializable
data class GitRepo(
    val name: String,
    @SerialName("full_name")
    val fullName: String,
)
