package sv.edu.udb.desafio2android.network


import retrofit2.Call
import retrofit2.http.*
import sv.edu.udb.desafio2android.models.ApiResponse
import sv.edu.udb.desafio2android.models.Resource

interface ApiService {

    @GET("recursos") // Esta URL será relativa si ya has definido la baseUrl en ApiClient
    fun getAllResources(): Call<ApiResponse> // Cambiar a ApiResponse

    @POST("recursos")
    fun createResource(@Body resource: Resource): Call<Resource>

    @PUT("recursos/{id}")
    fun updateResource(@Path("id") id: Int, @Body resource: Resource): Call<Resource>

    @DELETE("recursos/{id}")
    fun deleteResource(@Path("id") id: Int): Call<Void>
}
