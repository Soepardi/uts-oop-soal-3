# Aplikasi ATM - Bank UNSIA

Proyek ini dibuat untuk memenuhi tugas **Ujian Tengah Semester (UTS)**.

---

## Identitas Mahasiswa

*   **Nama:** Supardi Akhiyat
*   **NIM:** 230101010026

---

## Prasyarat Sistem

Sebelum menjalankan proyek ini, pastikan sistem Anda telah terinstal:
1. **Java Development Kit (JDK)**: Versi 11 atau yang lebih baru (sangat direkomendasikan JDK 17+).
2. **Sistem Operasi**: Windows, macOS, atau Linux.

Untuk memverifikasi instalasi Java, jalankan perintah berikut di terminal/command prompt Anda:
```bash
java -version
javac -version
```

---

## Struktur Proyek

* `ATMApplication.java` 
* `flatlaf-3.7.1.jar` 
* `logo.png` 
* `.vscode/` 

---

## Cara Menginstal & Menjalankan

### Metode 1: Menggunakan Terminal / Command Prompt (Rekomendasi)

Buka terminal atau command prompt, lalu masuk ke direktori tempat proyek ini berada.

#### **A. Untuk Pengguna Windows (PowerShell / CMD)**
1. **Kompilasi Program:**
   ```powershell
   javac -cp ".;flatlaf-3.7.1.jar" ATMApplication.java
   ```
2. **Jalankan Program:**
   ```powershell
   java -cp ".;flatlaf-3.7.1.jar" ATMApplication
   ```

#### **B. Untuk Pengguna macOS / Linux**
1. **Kompilasi Program:**
   ```bash
   javac -cp ".:flatlaf-3.7.1.jar" ATMApplication.java
   ```
2. **Jalankan Program:**
   ```bash
   java -cp ".:flatlaf-3.7.1.jar" ATMApplication
   ```

---

### Metode 2: Menggunakan Visual Studio Code (VS Code)

Jika Anda menggunakan VS Code, proyek ini sudah terkonfigurasi untuk memuat library FlatLaf secara otomatis melalui `.vscode/settings.json`.

1. Install extension **Extension Pack for Java** dari Microsoft di VS Code.
2. Buka folder proyek ini (`soal 3`) di VS Code melalui menu `File > Open Folder...`.
3. Buka file `ATMApplication.java`.
4. Klik tombol **Run** atau **Debug** yang muncul di atas baris deklarasi kelas utama `public class ATMApplication`, atau tekan tombol **F5** pada keyboard Anda.

---

## Informasi Akun Demo ATM

Gunakan kredensial default berikut untuk masuk dan mencoba seluruh fitur pada aplikasi:

* **Nama Nasabah:** Supardi Akhiyat
* **Nomor Rekening:** `1234-5678-9012`
* **PIN Akses:** `123456`
* **Saldo Awal:** Rp 5.000.000
