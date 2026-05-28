# UserHub Application 📱

UserHub adalah aplikasi manajemen data pengguna (User Management) berbasis Android yang dirancang dengan arsitektur modern berorientasi performa *offline* (**Offline-First Architecture**). Aplikasi ini mengintegrasikan penyimpanan lokal (Room) sebagai sumber data utama di layar (*Single Source of Truth*) yang disinkronkan secara reaktif dengan REST API (Retrofit).

---

## 🛠️ 1. Teknologi yang Digunakan

Proyek ini dibangun menggunakan pustaka standar industri (Android Jetpack) yang direkomendasikan oleh Google untuk memastikan performa yang optimal dan manajemen memori yang aman:

* **Bahasa Pemrograman:** Kotlin (100%) dengan pemrosesan asinkron tak sinkron di latar belakang menggunakan **Coroutines (Dispatchers.IO)**.
* **Arsitektur Utama:** MVVM Pattern (Model-View-ViewModel) yang modular dengan penerapan pemisahan tanggung jawab (*Separation of Concerns*) dan pembagian layer yang bersih.
* **Penyimpanan Lokal (Database):** **Room Database** untuk menyimpan cache data user (`UserEntity`), menghapus duplikasi data, serta melakukan manipulasi query dinamis.
* **Komponen Reaktif UI:** **LiveData (switchMap & map)** untuk mengamati dan memperbarui data secara langsung (*real-time*) ke UI ketika terjadi perubahan status pencarian, pengurutan, atau filter.
* **Koneksi Jaringan (Network Layer):** **Retrofit2 & Gson Converter** yang dikonfigurasikan bersama *OkHttpClient* dan *HttpLoggingInterceptor* untuk melacak keluar-masuknya data JSON dari API.
* **Penyuplai Dependensi (DI):** Struktur **Manual Injection** memanfaatkan pola *Singleton* pada berkas `Injection` dan `ViewModelFactory`.
* **Desain & Tata Letak UI:** * **View Binding:** Menghilangkan `findViewById` untuk menjamin keamanan tipe (*type safety*) dan mencegah *NullPointerException*.
  * **ConstraintLayout & LinearLayout:** Menyusun elemen UI secara responsif dan presisi di berbagai ukuran layar smartphone.
  * **Material Design 3 (M3):** Menggunakan komponen *Extended Floating Action Button*, *TextInputLayout OutlinedBox*, *MaterialButton*, dan *RadioButton*.
* **Pemantau Sinyal (Network Monitoring):** `ConnectivityManager` bersama `NetworkCallback` untuk mendeteksi perubahan jaringan internet secara instan.

---

## 🚀 2. Cara Penggunaan Aplikasi

### A. Prasyarat Sistem
* Android Studio (Versi Jellyfish atau yang lebih baru).
* Perangkat Android Fisik atau Emulator dengan minimum Android SDK 21 (Android 5.0 Lollipop).
* Koneksi internet aktif untuk sinkronisasi data API di awal.

### B. Langkah Menjalankan Proyek
1. *Clone* atau unduh repositori proyek UserHub ini.
2. Buka Android Studio, pilih **File > Open**, lalu arahkan ke folder proyek.
3. Tunggu proses **Gradle Sync** selesai mengunduh semua dependensi (`Room`, `Retrofit`, `Glide`, dsb).
4. Hubungkan perangkat Android lewat kabel USB (pastikan *USB Debugging* aktif).
5. Klik ikon **Run 'app'** (tombol segitiga hijau) di bagian toolbar atas Android Studio.

### C. Alur Fitur dan Navigasi
1. **Sinkronisasi Cache Otomatis:** Saat dibuka pertama kali, aplikasi memicu fungsi `refreshUsers()`. Data ditarik dari API, database lama dibersihkan (`deleteAll()`), dan digantikan dengan data baru (`insertUsers()`). Jika perangkat offline, aplikasi tidak akan *crash* melainkan langsung memuat data lokal terakhir.
2. **Pencarian Kilat (Real-time Search):** Ketik nama pada kolom *SearchView*. Sistem akan mengeksekusi query `LIKE` di database lokal secara reaktif tanpa menekan tombol cari.
3. **Menu Urutkan & Filter Kota:** Tekan tombol **Urutkan** atau **Filter Kota** untuk memunculkan panel *Bottom Sheet* dari bawah. Pilihan Anda akan langsung memicu fungsi database `searchSortAndFilterUsers()` sehingga susunan daftar di layar berubah instan tanpa *loading* internet (*zero network latency*).
4. **Membuka Detail User:** Klik pada salah satu kartu (*CardView*) user di halaman utama untuk memperluas (*expand*) detail informasi seperti nomor telepon, alamat, dan jenis kelamin dengan animasi transisi yang mulus.
5. **Form Tambah User Baru:** Tekan tombol melayang **Tambah User** di bawah. Lengkapi data (Username, Email, Phone, City, Address, & Gender) lalu tekan tombol **Save**. Jika internet terputus, aplikasi otomatis memblokir pengiriman data untuk mencegah error *timeout*.

---

## 🎨 3. Filosofi Desain (Kenapa Tampilan & Interaksi Seperti Itu?)

Arsitektur visual dan komponen antarmuka di dalam aplikasi ini dirancang berdasarkan prinsip **Material Design 3 Guidelines** dan **Usability Heuristics** untuk kenyamanan pengguna:

### A. Struktur Pencarian Vertikal & Desain Action Row (Chips Button)
* **Alasan Desain (`activity_main.xml`):** `SearchView` dibuat melebar penuh (*Full Width*) agar pengguna leluasa melihat teks ketikan panjang tanpa terpotong. Tombol aksi Sort dan Filter diposisikan tepat di bawahnya menggunakan komponen `MaterialButton` berbentuk pil (*Chips*). Struktur vertikal ini memberikan ruang pandang yang luas dan menghilangkan kesan padat (*visual clutter*) di bagian atas layar.

### B. Interaksi Status Tombol & Pola Warna Monokromatik (Teal Theme)
* **Alasan Desain (`colors.xml`):** Aplikasi menyelaraskan seluruh elemen visual menggunakan tema warna tunggal **Teal** (`#00BFA5` pada FAB, `#00796B` pada Primary Dark). Penyelarasan ini dilakukan untuk menghindari *Color Clashing* (tabrakan visual antara warna biru kaku dengan warna hijau teal).
* **Umpan Balik Visual (Visual Feedback):** Saat fitur urutan atau filter kota sedang aktif, tombol bersangkutan secara programmatif berubah warna menjadi *Teal Pastel* (`#E0F2F1`) dengan teks *Teal Tua* tegas, serta mengubah teksnya secara dinamis (Contoh: `"Kota: Tangerang"`). Pengguna dapat langsung mengetahui mengapa jumlah atau susunan data di layar berubah tanpa perlu menebak-nebak.

### C. Desain Kartu Expandable List dengan Animasi Indikator
* **Alasan Desain (`item_user.xml`):** Komponen daftar menggunakan `CardView` dengan sudut melengkung `10dp` dan elevasi halus `2dp` untuk memberikan kesan bersih dan modern di atas latar belakang abu-abu pudar (`@color/search_bg`). 
* **Efisiensi Informasi:** Menggunakan teknik *Expandable Item*. Informasi mendasar (Nama, Email, Kota) ditampilkan langsung di awal, sementara informasi pelengkap (Telepon, Alamat, Gender) disembunyikan di dalam `layoutDetailExpand` (`visibility="gone"`). Informasi tambahan ini baru akan terbuka secara kontekstual saat kartu diklik, didampingi ikon panah (`show`) yang berubah arah ke atas. Pola ini menghemat ruang vertikal layar secara signifikan.

### D. Penempatan Komponen Ergonomis (Extended FAB Tengah Bawah)
* **Alasan Desain (`activity_main.xml`):** Berdasarkan **Thumb Zone Theory** (Teori Zona Ibu Jari), area tengah bawah layar smartphone adalah posisi yang paling mudah dan nyaman dijangkau oleh jempol pengguna saat memegang ponsel dengan satu tangan. 
* **Optimasi Gulir:** Agar tombol melayang ini tidak menutupi informasi teks pada data user baris terakhir, komponen `RecyclerView` diberikan atribut `android:clipToPadding="false"` dan `android:paddingBottom="100dp"`. Dengan begitu, pengguna tetap bisa menggulir daftar user paling bawah hingga melewati batas belakang tombol FAB.

### E. Scrollable Bottom Sheet Dialog untuk Data Skala Besar
* **Alasan Desain (`layout_bottom_sheet_menu.xml`):** Filter kota mengambil data unik langsung dari database (`getUniqueCities()`). Karena jumlah kota bisa bertambah banyak di kemudian hari, komponen `itemContainer` dibungkus di dalam `NestedScrollView` dengan batas tinggi maksimal `android:maxHeight="350dp"`. Desain ini memastikan menu filter tidak akan meluap keluar dari layar smartphone dan tetap dapat digulir dengan nyaman, serta dilengkapi *drag handle* (garis dekoratif di bagian atas) sebagai penanda visual Material 3 bahwa panel tersebut dapat digeser ke bawah untuk ditutup.

### F. Pencegahan Galat & Efisiensi Pesan Mikro (Error Prevention)
* **Alasan Desain (`AddUserActivity.kt`):** Di halaman formulir, jika sistem mendeteksi perangkat sedang terputus dari internet via fungsi `isOnline()`, aplikasi langsung memotong proses pengiriman dan memunculkan notifikasi singkat: `"Koneksi terputus, gagal menyimpan data."`
* **Prinsip Microcopy:** Kalimat 5 kata ini langsung menembak akar masalah (Koneksi terputus) dan dampaknya (Gagal menyimpan) secara kilat (*fast scanning*) tanpa kata-kata instruksi yang bertele-tele. Strategi ini melatih aplikasi untuk mencegah *thread request* sia-sia ke server yang dapat menguras baterai dan memicu error *timeout* yang lama bagi pengguna.

---

## 📱 Tampilan Aplikasi

| Main Screen (Daftar User) | Mode Offline (Koneksi Terputus) | Sort dan Filter | Tambah User Baru  |
| :---: | :---: | :---: | :---: |
| <img width="390" height="836" alt="Image" src="https://github.com/user-attachments/assets/6c4b88f2-75a0-466d-8538-178e545396fc" /> | <img width="393" height="833" alt="Image" src="https://github.com/user-attachments/assets/33501afd-3fad-4c85-80cc-1193135f6f94" /> | <img width="392" height="829" alt="Image" src="https://github.com/user-attachments/assets/923bac88-c37d-4589-8a2e-c73a1cced2a6" /> | <img width="391" height="830" alt="Image" src="https://github.com/user-attachments/assets/5541d69d-0889-48f8-bc19-4686e9d72d38" /> | 
