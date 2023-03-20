package `in`.creativelizard.composedemo.data.repo
import `in`.creativelizard.composedemo.data.model.DataLoginResponse
import `in`.creativelizard.composedemo.data.model.mapToDomain
import `in`.creativelizard.composedemo.domain.model.LoginResponse
import `in`.creativelizard.composedemo.domain.repo.LoginRepo

class LoginRepoImpl: LoginRepo {
    override suspend fun getLoggedIn(): LoginResponse {
        return DataLoginResponse(0,true).mapToDomain()
    }

}