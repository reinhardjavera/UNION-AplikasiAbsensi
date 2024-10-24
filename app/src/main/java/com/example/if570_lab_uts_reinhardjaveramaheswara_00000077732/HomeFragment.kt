package com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    // Deklarasi variabel untuk UI dan Firebase
    private lateinit var tanggalSekarang: TextView
    private lateinit var tombolAbsen: ImageButton
    private lateinit var jamSekarang: TextView


    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private val CAMERA_REQUEST_CODE = 100

    private var capturedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi UI
        tanggalSekarang = view.findViewById(R.id.tvDateTime)
        jamSekarang = view.findViewById(R.id.jamSekarang)
        tombolAbsen = view.findViewById(R.id.btnAbsen)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Set tanggal sekarang
        val formatTanggal = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        tanggalSekarang.text = formatTanggal.format(Date())

        // Set waktu sekarang
        updateJamSekarang()

        tombolAbsen.setOnClickListener {
            periksaStatusAbsen()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startUpdatingTime() // Memulai proses pembaruan waktu
    }


    private fun updateJamSekarang() {
        val formatJam = SimpleDateFormat("HH:mm", Locale.getDefault())
        jamSekarang.text = formatJam.format(Date())
    }

    private fun updateTanggalSekarang() {
        val formatTanggal = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        tanggalSekarang.text = formatTanggal.format(Date())
    }

    private fun startUpdatingTime() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                updateJamSekarang()  // Update waktu setiap kali ini dijalankan
                handler.postDelayed(this, 1000) // Perbarui setiap detik
            }
        }
        handler.post(runnable) // Memulai eksekusi awal
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable) // Menghentikan pembaruan waktu saat view dihancurkan
    }

    private fun periksaStatusAbsen() {
        val tanggalSaatIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Log.e("FirestoreError", "User ID is null")
            Toast.makeText(requireContext(), "User ID is null", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("absensi")
            .whereEqualTo("userId", userId)
            .whereEqualTo("tanggal", tanggalSaatIni)
            .get()
            .addOnSuccessListener { documents ->
                val sudahAbsenMasuk = documents.any { it.contains("jamMasuk") && it.getString("jamMasuk") != null }
                val sudahAbsenKeluar = documents.any { it.contains("jamKeluar") && it.getString("jamKeluar") != null }

                when {
                    sudahAbsenMasuk && !sudahAbsenKeluar -> {
                        tampilkanDialogKeluar()
                    }
                    sudahAbsenMasuk && sudahAbsenKeluar -> {
                        Toast.makeText(requireContext(), "Anda sudah absen keluar hari ini. Silahkan menunggu sampai hari berikutnya.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                        } else {
                            bukaKamera()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Gagal memeriksa status absensi: ${e.message}")
                Toast.makeText(requireContext(), "Gagal memeriksa status absensi", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == CAMERA_REQUEST_CODE) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            bukaKamera()
        } else {
            Toast.makeText(requireContext(), "Permission denied to use camera", Toast.LENGTH_SHORT).show()
        }
    }
}

    private fun bukaKamera() {
        val intentKamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intentKamera.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intentKamera, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            capturedImageBitmap = data?.extras?.get("data") as? Bitmap
            capturedImageBitmap?.let { tampilkanDialogKonfirmasi() }
        } else {
            Toast.makeText(requireContext(), "Aksi dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tampilkanDialogKonfirmasi() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Konfirmasi Absensi")
        dialogBuilder.setMessage("Apakah Anda ingin mengunggah foto ini?")

        // Tampilkan gambar yang diambil dalam dialog
        val imageView = ImageView(requireContext())
        imageView.setImageBitmap(capturedImageBitmap)
        dialogBuilder.setView(imageView)

        dialogBuilder.setPositiveButton("Unggah") { dialog, _ ->
            capturedImageBitmap?.let { unggahGambarKeStorage(it) }
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Ulangi") { dialog, _ ->
            capturedImageBitmap = null
            bukaKamera()
            dialog.dismiss()
        }

        dialogBuilder.show()
    }

    private fun unggahGambarKeStorage(gambar: Bitmap) {
        val baos = ByteArrayOutputStream()
        gambar.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val imageRef = storage.reference.child("absensi/${System.currentTimeMillis()}.jpg")

        imageRef.putBytes(data)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    simpanDataAbsensi(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("StorageError", "Gagal mengunggah gambar: ${e.message}")
                Toast.makeText(requireContext(), "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun simpanDataAbsensi(imageUrl: String) {
        val dataAbsensi = hashMapOf(
            "userId" to auth.currentUser?.uid,
            "tanggal" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            "jamMasuk" to SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            "imageUrl" to imageUrl
        )

        firestore.collection("absensi").add(dataAbsensi)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Absensi berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Gagal menyimpan data absensi: ${e.message}")
                Toast.makeText(requireContext(), "Gagal menyimpan data absensi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun tampilkanDialogKeluar() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Konfirmasi Absen Keluar")
        dialogBuilder.setMessage("Apakah Anda ingin absen keluar hari ini?")

        dialogBuilder.setPositiveButton("Ya") { dialog, _ ->
            absenKeluar()
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.show()
    }

    private fun absenKeluar() {
        val tanggalSaatIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        firestore.collection("absensi")
            .whereEqualTo("userId", auth.currentUser?.uid)
            .whereEqualTo("tanggal", tanggalSaatIni)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val documentRef = firestore.collection("absensi").document(document.id)
                    val jamKeluar = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    documentRef.update("jamKeluar", jamKeluar)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Absen keluar berhasil", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Gagal memperbarui data absen keluar: ${e.message}")
                            Toast.makeText(requireContext(), "Gagal absen keluar", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Gagal mengambil data absensi: ${e.message}")
                Toast.makeText(requireContext(), "Gagal absen keluar", Toast.LENGTH_SHORT).show()
            }
    }
}
