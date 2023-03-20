package `in`.creativelizard.composedemo.data.model
import `in`.creativelizard.composedemo.domain.model.LoginResponse

data class DataLoginResponse (val id:Int,val isLoggedIn:Boolean)
fun DataLoginResponse.mapToDomain(): LoginResponse {
    return LoginResponse(isLoggedIn)
}