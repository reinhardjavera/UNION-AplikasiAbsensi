package com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732.R
import java.text.SimpleDateFormat
import java.util.Locale

class AbsensiAdapter(private val daftarAbsensi: List<Absensi>) :
    RecyclerView.Adapter<AbsensiAdapter.AbsensiViewHolder>() {

    inner class AbsensiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.absensiImage)
        private val tanggalTextView: TextView = itemView.findViewById(R.id.absensiTanggal)
        private val jamMasukTextView: TextView = itemView.findViewById(R.id.absensiJamMasuk)
        private val jamKeluarTextView: TextView = itemView.findViewById(R.id.absensiJamKeluar)

        fun bind(absensi: Absensi) {
            tanggalTextView.text = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID")).
            format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(absensi.tanggal))
            jamMasukTextView.text = "Jam Masuk: ${absensi.jamMasuk ?: "-"}"
            jamKeluarTextView.text = "Jam Keluar: ${absensi.jamKeluar ?: "-"}"

            // Load gambar menggunakan Glide
            Glide.with(itemView.context)
                .load(absensi.imageUrl)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsensiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_absensi, parent, false)
        return AbsensiViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbsensiViewHolder, position: Int) {
        holder.bind(daftarAbsensi[position])
    }

    override fun getItemCount(): Int = daftarAbsensi.size
}