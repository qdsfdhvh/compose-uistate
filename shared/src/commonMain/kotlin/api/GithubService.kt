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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object GithubService : GithubApi {

    private val client by lazy {
        HttpClient {
            install(Logging) {
                level = LogLevel.BODY
                logger = object : Logger {
                    override fun log(message: String) {
                        if (message.length < 2000) {
                            println(message)
                        } else {
                            message.windowed(2000, 2000, true).forEach {
                                println(it)
                            }
                        }
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

    override suspend fun getUser(userName: String): GitUser {
        return client.get("https://api.github.com/users/${userName}").body()
    }

    override suspend fun getFollowers(url: String): List<GitUser> {
        return client.get(url).body()
    }

    override suspend fun getRepos(url: String): List<GitRepo> {
        return client.get(url).body()
    }
}

@Serializable
data class GithubNetworkException(
    override val message: String,
) : IllegalStateException()
