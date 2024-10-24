package com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var editTextNama: EditText
    private lateinit var editTextNIM: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonKeluar: ImageButton
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inisialisasi UI
        editTextNama = view.findViewById(R.id.editTextNama)
        editTextNIM = view.findViewById(R.id.editTextNIM)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonKeluar = view.findViewById(R.id.buttonKeluar)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Memuat profil ketika fragment dimulai
        muatProfil()

        // Event listener untuk tombol simpan dan keluar
        buttonSave.setOnClickListener {
            simpanProfil()
        }

        buttonKeluar.setOnClickListener {
            tampilkanDialogKeluar()
        }

        return view
    }

    private fun muatProfil() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    editTextNama.setText(document.getString("nama") ?: "")
                    editTextNIM.setText(document.getString("nim") ?: "")
                } else {
                    Toast.makeText(requireContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Gagal memuat profil: ${e.message}")
                Toast.makeText(requireContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun simpanProfil() {
        val nama = editTextNama.text.toString().trim()
        val nim = editTextNIM.text.toString().trim()
        val userId = auth.currentUser?.uid ?: return

        if (nama.isEmpty() || nim.isEmpty()) {
            Toast.makeText(requireContext(), "Harap isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val profilPengguna = mapOf(
            "nama" to nama,
            "nim" to nim
        )

        firestore.collection("users").document(userId)
            .set(profilPengguna)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Gagal menyimpan profil: ${e.message}")
                Toast.makeText(requireContext(), "Gagal menyimpan profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun tampilkanDialogKeluar() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi Logout")
        builder.setMessage("Apakah Anda yakin ingin keluar?")
        builder.setPositiveButton("Ya") { _, _ ->
            keluarAkun()
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun keluarAkun() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        auth.signOut()
        startActivity(intent)
    }
}
