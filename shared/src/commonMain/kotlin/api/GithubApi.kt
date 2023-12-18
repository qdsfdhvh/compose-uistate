package api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object GithubApi {

    private val client by lazy {
        HttpClient {
            install(Logging) {
                level = LogLevel.BODY
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
            HttpResponseValidator {
                validateResponse { response ->
                    if (response.status.value !in 200..299) {
                        throw response.body<GithubNetworkException>()
                    }
                }
            }
        }
    }

    suspend fun getUser(userName: String): GitUser {
        return client.get("https://api.github.com/users/${userName}").body()
    }

    suspend fun getFollowers(url: String): List<GitUser> {
        return client.get(url).body()
    }

    suspend fun getRepos(url: String): List<GitRepo> {
        return client.get(url).body()
    }
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

@Serializable
data class GithubNetworkException(
    override val message: String,
) : IllegalStateException()
