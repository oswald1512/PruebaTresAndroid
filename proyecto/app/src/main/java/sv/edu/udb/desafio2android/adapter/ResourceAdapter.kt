package sv.edu.udb.desafio2android.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sv.edu.udb.desafio2android.R
import sv.edu.udb.desafio2android.models.Resource
import java.util.Locale


class ResourceAdapter(
    private val onItemClick: (Resource) -> Unit
) : RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder>() {

    private var resourceList: List<Resource> = listOf()

    fun submitList(list: List<Resource>) {
        resourceList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resourceList[position]
        holder.bind(resource)
        holder.itemView.setOnClickListener { onItemClick(resource) }
    }

    override fun getItemCount(): Int = resourceList.size

    class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titulo: TextView = itemView.findViewById(R.id.textTitulo)
        private val descripcion: TextView = itemView.findViewById(R.id.textDescripcion)
        private val tipo: TextView = itemView.findViewById(R.id.textTipo)
        private val enlace: TextView = itemView.findViewById(R.id.textEnlace)
        private val imagen: TextView = itemView.findViewById(R.id.textImagen)

        fun bind(resource: Resource) {
            titulo.text = "Título: ${resource.titulo}"
            descripcion.text = "Descripción: ${resource.descripcion}"
            tipo.text = "Tipo: ${resource.tipo}"
            enlace.text = "Enlace: ${resource.enlace}"
            imagen.text = "Imagen: ${resource.imagen}"
        }
    }
}