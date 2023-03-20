package `in`.creativelizard.composedemo.domain.repo

import `in`.creativelizard.composedemo.domain.model.LoginResponse

interface LoginRepo {
    suspend fun getLoggedIn(): LoginResponse
}