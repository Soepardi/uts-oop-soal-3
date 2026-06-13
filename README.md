# 🏦 Dokumentasi Sistem ATM - Bank UNSIA

## Deskripsi Program
`ATMApplication.java` adalah aplikasi berbasis Java yang mensimulasikan sistem Anjungan Tunai Mandiri (ATM). Program ini mengimplementasikan konsep *Object-Oriented Programming* (OOP) dengan menggunakan antarmuka grafis (GUI) modern berbasis *library* **FlatLaf**.

---

## 🏗️ Analisis Komponen Utama (Arsitektur OOP)

Program ini terdiri dari 3 kelas utama dalam satu file yang dipisahkan berdasarkan tanggung jawabnya:

### 1. `Transaction` (Model Data)
Kelas ini berfungsi untuk merekam dan menyimpan setiap aktivitas transaksi yang dilakukan oleh nasabah.

```java
class Transaction {
    private String jenis;
    private long nominal;
    private long saldoAkhir;
    private String waktu;

    public Transaction(String jenis, long nominal, long saldoAkhir) {
        this.jenis = jenis;
        this.nominal = nominal;
        this.saldoAkhir = saldoAkhir;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.waktu = LocalDateTime.now().format(formatter);
    }
    // Getter methods...
}
```

### 2. `ATM` (Logika Bisnis)
Menangani seluruh operasi perbankan dan validasi, memisahkan logika dari antarmuka pengguna.
Data default nasabah juga diinisialisasi di sini:
- **Nama Nasabah:** Supardi Akhiyat
- **Rekening:** 1234-5678-9012
- **PIN Default:** 123456
- **Saldo Awal:** Rp 5.000.000

```java
class ATM {
    private String pin;
    private long saldo;
    private String namaNasabah;
    private String nomorRekening;
    private List<Transaction> riwayat;

    // Konstruktor
    public ATM() {
        this.pin           = "123456";
        this.saldo         = 5_000_000L;
        this.namaNasabah   = "Supardi Akhiyat";
        this.nomorRekening = "1234-5678-9012";
        this.riwayat       = new ArrayList<>();
    }
    // Business logic methods...
}
```

### 3. `ATMApplication` (GUI / Antarmuka Pengguna)
Kelas utama turunan dari `JFrame` yang menangani tampilan visual dan interaksi pengguna. Menggunakan `CardLayout` untuk navigasi halaman tanpa membuka jendela baru.

```java
public class ATMApplication extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private ATM atm;

    public ATMApplication() {
        atm = new ATM();
        // Setup frame dan CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(buatLayarPin(), "PIN");
        mainPanel.add(buatLayarDashboard(), "DASHBOARD");
        // dan layer lainnya...
    }
}
```

---

## 🔧 Analisis Logika Pemrograman

Berikut adalah cuplikan kode bagaimana struktur kontrol dasar (`IF`, `SWITCH`, dan `FOR`) digunakan di dalam program:

### 1. Struktur Kendali `IF` (Validasi)
Program banyak menggunakan struktur `if` untuk validasi keamanan dan memastikan aturan perbankan terpenuhi. Misalnya, membatasi percobaan PIN atau memvalidasi nominal tarik tunai:

```java
// Contoh Validasi Tarik Tunai
public String tarikTunai(long nominal) {
    if (nominal <= 0) {
        return "ERROR:Nominal harus lebih dari 0.";
    }
    if (nominal % 50_000 != 0) {
        return "ERROR:Nominal harus kelipatan Rp 50.000.";
    }
    if (nominal > saldo) {
        return "ERROR:Saldo tidak mencukupi.\nSaldo Anda: " + formatRupiah(saldo);
    }
    // Jika lolos validasi
    saldo -= nominal;
    riwayat.add(new Transaction("Tarik Tunai", nominal, saldo));
    return "OK:Penarikan berhasil!";
}
```

### 2. Struktur Kendali `SWITCH` (Navigasi Menu)
`switch` case digunakan pada aplikasi ini untuk navigasi menu dari Dashboard dan pengkondisian tombol pada layar PIN.

```java
// Navigasi Menu Dashboard
btn.addActionListener(e -> {
    switch (nomorMenu) {
        case 1: cardLayout.show(mainPanel, "CEK_SALDO"); break;
        case 2: cardLayout.show(mainPanel, "TARIK_TUNAI"); break;
        case 3: cardLayout.show(mainPanel, "SETOR_TUNAI"); break;
        case 4: cardLayout.show(mainPanel, "TRANSFER"); break;
        case 5: cardLayout.show(mainPanel, "UBAH_PIN"); break;
        case 6:
            refreshMutasi();
            cardLayout.show(mainPanel, "MUTASI");
            break;
        case 7: keluarAplikasi(); break;
    }
});
```

### 3. Struktur Kendali `FOR` (Perulangan Komponen UI)
`for` loop secara efisien digunakan untuk me-render komponen secara berulang, seperti menyusun 6 dot indikator PIN dan grid keypad tanpa harus mendeklarasikannya satu per satu secara manual.

```java
// Membuat tombol keypad PIN secara dinamis
String[] tombol = {"1","2","3","4","5","6","7","8","9","Hapus","0","OK"};
for (int i = 0; i < tombol.length; i++) {
    String label = tombol[i];
    boolean isAction = label.equals("Hapus") || label.equals("OK");
    JButton btn = buatTombolKeypad(label, isAction);
    
    // Aksi ditambahkan di dalam loop
    keypad.add(btn);
}
```

---

## 🎮 Fitur Utama
1. **Autentikasi PIN**: Input 6 digit angka, memblokir akses jika salah lebih dari 3 kali.
2. **Dashboard Dinamis**: Menampilkan saldo, nama, dan no rekening dengan antarmuka yang ramah pengguna.
3. **Cek Saldo**: Melihat sisa saldo secara terperinci.
4. **Tarik & Setor Tunai**: Transaksi dengan batasan validasi (kelipatan, batas minimum, dll).
5. **Transfer**: Fitur mengirim dana antar rekening.
6. **Ubah PIN**: Meningkatkan keamanan dengan konfirmasi PIN lama dan konfirmasi ganda PIN baru.
7. **Mutasi Rekening**: Daftar riwayat seluruh aktivitas transaksi (timestamp, jenis, dan sisa saldo).

---

## 🚀 Cara Menjalankan

### 1. Kompilasi
Kompilasi source code dengan menyertakan *library* FlatLaf ke dalam classpath.
```powershell
javac -cp ".;flatlaf-3.7.1.jar" ATMApplication.java
```

### 2. Eksekusi
```powershell
java -cp ".;flatlaf-3.7.1.jar" ATMApplication
```
