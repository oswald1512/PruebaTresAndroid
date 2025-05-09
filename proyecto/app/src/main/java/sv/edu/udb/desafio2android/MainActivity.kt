package sv.edu.udb.desafio2android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sv.edu.udb.desafio2android.models.ApiResponse
import sv.edu.udb.desafio2android.models.Resource
import sv.edu.udb.desafio2android.network.ApiClient
import sv.edu.udb.desafio2android.adapter.ResourceAdapter
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var resourceAdapter: ResourceAdapter
    private lateinit var editTextTitulo: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextTipo: EditText
    private lateinit var editTextEnlace: EditText
    private lateinit var editTextImagen: EditText
    private lateinit var searchEditText: EditText

    private var selectedResourceId: Int? = null
    private var lastResourceId: Int = 0
    private var fullResourceList: List<Resource> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnCerrarSesion: Button = findViewById(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        resourceAdapter = ResourceAdapter { resource ->
            selectedResourceId = resource.id
            fillEditTextWithResourceData(resource)
        }
        recyclerView.adapter = resourceAdapter

        editTextTitulo = findViewById(R.id.editTextTitulo)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextTipo = findViewById(R.id.editTextTipo)
        editTextEnlace = findViewById(R.id.editTextEnlace)
        editTextImagen = findViewById(R.id.editTextImagen)
        searchEditText = findViewById(R.id.searchEditText)

        val btnCrear: Button = findViewById(R.id.btnCrear)
        val btnActualizar: Button = findViewById(R.id.btnActualizar)
        val btnEliminar: Button = findViewById(R.id.btnEliminar)
        val btnLimpiar: Button = findViewById(R.id.btnLimpiar)

        btnCrear.setOnClickListener {
            createResource()
        }

        btnActualizar.setOnClickListener {
            selectedResourceId?.let { id -> updateResource(id) }
        }

        btnEliminar.setOnClickListener {
            selectedResourceId?.let { id -> deleteResource(id) }
        }

        btnLimpiar.setOnClickListener {
            clearEditTexts()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterResources(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        getAllResources()
    }

    private fun getAllResources() {
        ApiClient.apiService.getAllResources().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val resources = response.body()?.recursos.orEmpty()
                    fullResourceList = resources
                    resourceAdapter.submitList(resources)
                    lastResourceId = resources.maxOfOrNull { it.id } ?: 0
                } else {
                    showError("Error al obtener los recursos")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showError("Error: ${t.message}")
            }
        })
    }

    private fun filterResources(query: String) {
        val filtered = fullResourceList.filter {
            it.titulo.contains(query, true) ||
                    it.descripcion.contains(query, true) ||
                    it.tipo.contains(query, true) ||
                    it.enlace.contains(query, true) ||
                    it.imagen.contains(query, true)
        }
        resourceAdapter.submitList(filtered)
    }

    private fun fillEditTextWithResourceData(resource: Resource) {
        editTextTitulo.setText(resource.titulo)
        editTextDescripcion.setText(resource.descripcion)
        editTextTipo.setText(resource.tipo)
        editTextEnlace.setText(resource.enlace)
        editTextImagen.setText(resource.imagen)
    }

    private fun clearEditTexts() {
        editTextTitulo.text.clear()
        editTextDescripcion.text.clear()
        editTextTipo.text.clear()
        editTextEnlace.text.clear()
        editTextImagen.text.clear()
        selectedResourceId = null
    }

    private fun createResource() {
        val titulo = editTextTitulo.text.toString().trim()
        val descripcion = editTextDescripcion.text.toString().trim()
        val tipo = editTextTipo.text.toString().trim()
        val enlace = editTextEnlace.text.toString().trim()
        val imagen = editTextImagen.text.toString().trim()

        if (titulo.isEmpty() || descripcion.isEmpty() || tipo.isEmpty() || enlace.isEmpty() || imagen.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val newId = lastResourceId + 1
        val newResource = Resource(newId, titulo, descripcion, tipo, enlace, imagen)

        ApiClient.apiService.createResource(newResource).enqueue(object : Callback<Resource> {
            override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                if (response.isSuccessful) {
                    showError("Recurso creado con éxito")
                    clearEditTexts()
                    getAllResources()
                } else {
                    showError("Error al crear el recurso")
                }
            }

            override fun onFailure(call: Call<Resource>, t: Throwable) {
                showError("Error: ${t.message}")
            }
        })
    }

    private fun updateResource(id: Int) {
        val titulo = editTextTitulo.text.toString().trim()
        val descripcion = editTextDescripcion.text.toString().trim()
        val tipo = editTextTipo.text.toString().trim()
        val enlace = editTextEnlace.text.toString().trim()
        val imagen = editTextImagen.text.toString().trim()

        if (titulo.isEmpty() || descripcion.isEmpty() || tipo.isEmpty() || enlace.isEmpty() || imagen.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedResource = Resource(id, titulo, descripcion, tipo, enlace, imagen)

        ApiClient.apiService.updateResource(id, updatedResource).enqueue(object : Callback<Resource> {
            override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                if (response.isSuccessful) {
                    showError("Recurso actualizado con éxito")
                    clearEditTexts()
                    getAllResources()
                } else {
                    showError("Error al actualizar el recurso")
                }
            }

            override fun onFailure(call: Call<Resource>, t: Throwable) {
                showError("Error: ${t.message}")
            }
        })
    }

    private fun deleteResource(id: Int) {
        ApiClient.apiService.deleteResource(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showError("Recurso eliminado con éxito")
                    clearEditTexts()
                    searchEditText.text.clear()
                    getAllResources()
                } else {
                    showError("Error al eliminar el recurso")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showError("Error: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
