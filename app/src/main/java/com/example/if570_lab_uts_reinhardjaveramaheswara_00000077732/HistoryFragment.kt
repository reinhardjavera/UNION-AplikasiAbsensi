package com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732.R
import com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732.model.Absensi
import com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732.model.AbsensiAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var absensiAdapter: AbsensiAdapter
    private lateinit var absensiList: MutableList<Absensi>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        absensiList = mutableListOf()
        absensiAdapter = AbsensiAdapter(absensiList)
        recyclerView.adapter = absensiAdapter

        firestore = FirebaseFirestore.getInstance()
        getAbsensiRecords()
    }

    private fun getAbsensiRecords() {
        // Ambil ID pengguna yang sedang login
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Cek apakah userId tidak null
        if (userId != null) {
            firestore.collection("absensi")
                .whereEqualTo("userId", userId) // Filter berdasarkan userId
                .get()
                .addOnSuccessListener { result ->
                    absensiList.clear() // Bersihkan daftar sebelum menambahkan data baru
                    for (document in result) {
                        val absensi = document.toObject(Absensi::class.java)
                        absensiList.add(absensi)
                    }
                    absensiList.sortByDescending { it.tanggal }
                    absensiAdapter.notifyDataSetChanged() // Notify adapter about data changes
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Pengguna belum login", Toast.LENGTH_SHORT).show()
        }
    }
}