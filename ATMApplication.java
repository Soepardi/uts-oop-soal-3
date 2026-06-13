import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// ============================================================
// Kelas Transaction - Model untuk menyimpan riwayat transaksi
// ============================================================
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

    public String getJenis()       { return jenis; }
    public long getNominal()       { return nominal; }
    public long getSaldoAkhir()    { return saldoAkhir; }
    public String getWaktu()       { return waktu; }
}

// ============================================================
// Kelas ATM - Logika bisnis OOP
// ============================================================
class ATM {
    private String pin;
    private long saldo;
    private String namaNasabah;
    private String nomorRekening;
    private List<Transaction> riwayat;

    // Konstruktor - data awal nasabah
    public ATM() {
        this.pin           = "123456";
        this.saldo         = 5_000_000L;
        this.namaNasabah   = "Supardi Akhiyat";
        this.nomorRekening = "1234-5678-9012";
        this.riwayat       = new ArrayList<>();
    }

    // ---- Validasi PIN dengan logika IF ----
    public boolean validasiPin(String inputPin) {
        if (inputPin == null || inputPin.isEmpty()) {
            return false;
        }
        return this.pin.equals(inputPin);
    }

    // ---- Tarik Tunai ----
    public String tarikTunai(long nominal) {
        // Logika IF untuk validasi
        if (nominal <= 0) {
            return "ERROR:Nominal harus lebih dari 0.";
        }
        if (nominal % 50_000 != 0) {
            return "ERROR:Nominal harus kelipatan Rp 50.000.";
        }
        if (nominal > saldo) {
            return "ERROR:Saldo tidak mencukupi.\nSaldo Anda: " + formatRupiah(saldo);
        }
        if (nominal > 5_000_000) {
            return "ERROR:Maksimal penarikan Rp 5.000.000 per transaksi.";
        }
        saldo -= nominal;
        riwayat.add(new Transaction("Tarik Tunai", nominal, saldo));
        return "OK:Penarikan berhasil!\nNominal: " + formatRupiah(nominal) + "\nSaldo tersisa: " + formatRupiah(saldo);
    }

    // ---- Setor Tunai ----
    public String setorTunai(long nominal) {
        if (nominal <= 0) {
            return "ERROR:Nominal harus lebih dari 0.";
        }
        if (nominal < 50_000) {
            return "ERROR:Minimal setoran Rp 50.000.";
        }
        saldo += nominal;
        riwayat.add(new Transaction("Setor Tunai", nominal, saldo));
        return "OK:Setoran berhasil!\nNominal: " + formatRupiah(nominal) + "\nSaldo sekarang: " + formatRupiah(saldo);
    }

    // ---- Transfer ----
    public String transfer(String noRekTujuan, long nominal) {
        if (noRekTujuan == null || noRekTujuan.trim().isEmpty()) {
            return "ERROR:Nomor rekening tujuan tidak boleh kosong.";
        }
        if (nominal <= 0) {
            return "ERROR:Nominal harus lebih dari 0.";
        }
        if (nominal > saldo) {
            return "ERROR:Saldo tidak mencukupi.\nSaldo Anda: " + formatRupiah(saldo);
        }
        saldo -= nominal;
        riwayat.add(new Transaction("Transfer ke " + noRekTujuan, nominal, saldo));
        return "OK:Transfer berhasil!\nKe rekening: " + noRekTujuan + "\nNominal: " + formatRupiah(nominal) + "\nSaldo tersisa: " + formatRupiah(saldo);
    }

    // ---- Ubah PIN ----
    public String ubahPin(String pinLama, String pinBaru, String konfirmasiPin) {
        if (!this.pin.equals(pinLama)) {
            return "ERROR:PIN lama tidak sesuai.";
        }
        if (pinBaru == null || pinBaru.length() < 6) {
            return "ERROR:PIN baru minimal 6 digit angka.";
        }
        if (!pinBaru.equals(konfirmasiPin)) {
            return "ERROR:Konfirmasi PIN tidak cocok.";
        }
        if (pinBaru.equals(pinLama)) {
            return "ERROR:PIN baru tidak boleh sama dengan PIN lama.";
        }
        this.pin = pinBaru;
        return "OK:PIN berhasil diubah!";
    }

    // ---- Getters ----
    public long getSaldo()           { return saldo; }
    public String getNamaNasabah()   { return namaNasabah; }
    public String getNomorRekening() { return nomorRekening; }
    public List<Transaction> getRiwayat() { return riwayat; }

    // ---- Format Rupiah ----
    public static String formatRupiah(long nominal) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
        return nf.format(nominal);
    }
}

// ============================================================
// Kelas ATMApplication - GUI Utama
// ============================================================
public class ATMApplication extends JFrame {

    // ---- Konstanta Warna & Font ----
    private static final Color COLOR_BG        = new Color(13, 17, 30);
    private static final Color COLOR_CARD      = new Color(22, 28, 48);
    private static final Color COLOR_CARD2     = new Color(30, 38, 65);
    private static final Color COLOR_ACCENT    = new Color(59, 130, 246);
    private static final Color COLOR_SUCCESS   = new Color(34, 197, 94);
    private static final Color COLOR_WARNING   = new Color(234, 179, 8);
    private static final Color COLOR_DANGER    = new Color(239, 68, 68);
    private static final Color COLOR_TEXT      = new Color(226, 232, 240);
    private static final Color COLOR_SUBTEXT   = new Color(100, 116, 139);
    private static final Color COLOR_BORDER    = new Color(51, 65, 85);

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);

    private static final Font FONT_PIN_DOT = new Font("Segoe UI", Font.BOLD, 36);

    // ---- State Aplikasi ----
    private ATM atm;
    private StringBuilder pinInput = new StringBuilder();
    private int percobaan = 0;
    private static final int MAX_PERCOBAAN = 3;

    // ---- Panel & CardLayout ----
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // ---- Komponen layar PIN ----
    private JLabel[] pinDots;
    private JLabel lblPinError;
    private JLabel lblPercobaanSisa;

    // ---- Komponen layar Dashboard ----
    private JLabel lblSaldoDashboard;
    private JLabel lblNamaHeader;

    // Helper untuk memuat dan menyesuaikan ukuran logo.png
    private ImageIcon dapatkanLogo(int targetHeight) {
        try {
            ImageIcon originalIcon = new ImageIcon("logo.png");
            if (originalIcon.getImageLoadStatus() == MediaTracker.ERRORED || originalIcon.getImageLoadStatus() == MediaTracker.ABORTED) {
                return null;
            }
            Image img = originalIcon.getImage();
            int originalWidth = img.getWidth(null);
            int originalHeight = img.getHeight(null);
            
            double ratio = (originalWidth > 0 && originalHeight > 0) 
                ? (double) originalWidth / originalHeight 
                : 566.0 / 208.0;
                
            int targetWidth = (int) (targetHeight * ratio);
            Image scaledImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
            return null;
        }
    }

    // ============================================================
    // Konstruktor - Inisialisasi Frame
    // ============================================================
    public ATMApplication() {
        atm = new ATM();
        setTitle("ATM Simulator - Bank UNSIA");
        
        // Set icon aplikasi (window icon)
        try {
            ImageIcon appIcon = new ImageIcon("logo.png");
            setIconImage(appIcon.getImage());
        } catch (Exception e) {
            System.err.println("Gagal mengatur ikon jendela: " + e.getMessage());
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 780);
        setMinimumSize(new Dimension(440, 700));
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(COLOR_BG);

        // Inisialisasi CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(COLOR_BG);

        // Tambahkan semua layar (Card)
        mainPanel.add(buatLayarPin(), "PIN");
        mainPanel.add(buatLayarDashboard(), "DASHBOARD");
        mainPanel.add(buatLayarCekSaldo(), "CEK_SALDO");
        mainPanel.add(buatLayarTarikTunai(), "TARIK_TUNAI");
        mainPanel.add(buatLayarSetorTunai(), "SETOR_TUNAI");
        mainPanel.add(buatLayarTransfer(), "TRANSFER");
        mainPanel.add(buatLayarUbahPin(), "UBAH_PIN");
        mainPanel.add(buatLayarMutasi(), "MUTASI");

        add(mainPanel);

        // Tampilkan layar pertama
        cardLayout.show(mainPanel, "PIN");
        setVisible(true);
    }

    // ============================================================
    // Helper: Membuat tombol menu dengan ikon dan warna
    // ============================================================
    private JButton buatTombolMenu(String label, String ikon, Color warna) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(0, 6));
        btn.setBackground(COLOR_CARD);
        btn.setForeground(COLOR_TEXT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(16, 10, 16, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblIkon = new JLabel(ikon, SwingConstants.CENTER);
        lblIkon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel lblTeks = new JLabel(label, SwingConstants.CENTER);
        lblTeks.setFont(FONT_SMALL);
        lblTeks.setForeground(warna);

        btn.add(lblIkon, BorderLayout.CENTER);
        btn.add(lblTeks, BorderLayout.SOUTH);

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_CARD2);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(warna, 1, true),
                    new EmptyBorder(16, 10, 16, 10)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_CARD);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_BORDER, 1, true),
                    new EmptyBorder(16, 10, 16, 10)
                ));
            }
        });

        return btn;
    }

    // ============================================================
    // Helper: Membuat tombol keypad PIN
    // ============================================================
    private JButton buatTombolKeypad(String label, boolean isAction) {
        JButton btn = new JButton(label);
        btn.setFont(isAction ? new Font("Segoe UI", Font.BOLD, 13) : new Font("Segoe UI", Font.BOLD, 20));
        btn.setBackground(isAction ? COLOR_CARD2 : COLOR_CARD);
        btn.setForeground(COLOR_TEXT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(14, 10, 14, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_ACCENT.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(isAction ? COLOR_CARD2 : COLOR_CARD);
            }
        });

        return btn;
    }

    // ============================================================
    // Helper: Tombol Kembali ke Dashboard
    // ============================================================
    private JButton buatTombolKembali() {
        JButton btn = new JButton("← Kembali ke Menu");
        btn.setFont(FONT_SMALL);
        btn.setBackground(COLOR_CARD2);
        btn.setForeground(COLOR_SUBTEXT);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            refreshDashboard();
            cardLayout.show(mainPanel, "DASHBOARD");
        });
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(COLOR_TEXT); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(COLOR_SUBTEXT); }
        });
        return btn;
    }

    // ============================================================
    // Helper: Dialog Hasil Transaksi
    // ============================================================
    private void tampilkanHasil(String pesan, boolean sukses) {
        Color warna = sukses ? COLOR_SUCCESS : COLOR_DANGER;
        String judul = sukses ? "Berhasil" : "Gagal";

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblIkon = new JLabel(sukses ? "Berhasil" : "Gagal", SwingConstants.CENTER);
        lblIkon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        JLabel lblPesan = new JLabel("<html><center>" + pesan.replace("\n", "<br>") + "</center></html>", SwingConstants.CENTER);
        lblPesan.setFont(FONT_BODY);
        lblPesan.setForeground(warna);

        panel.add(lblIkon, BorderLayout.NORTH);
        panel.add(lblPesan, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, judul, JOptionPane.PLAIN_MESSAGE);
    }

    // ============================================================
    // Helper: Input field style
    // ============================================================
    private JTextField buatTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBackground(COLOR_CARD2);
        tf.setForeground(COLOR_TEXT);
        tf.setCaretColor(COLOR_TEXT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        return tf;
    }

    private JPasswordField buatPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBackground(COLOR_CARD2);
        pf.setForeground(COLOR_TEXT);
        pf.setCaretColor(COLOR_TEXT);
        pf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        pf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        return pf;
    }

    // ============================================================
    // Layar 1: Input PIN
    // ============================================================
    private JPanel buatLayarPin() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);

        // --- Header ---
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(COLOR_BG);
        header.setBorder(new EmptyBorder(30, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        ImageIcon logoIcon = dapatkanLogo(60); // Tinggi 60px
        JLabel lblBank;
        if (logoIcon != null && logoIcon.getIconWidth() > 0) {
            lblBank = new JLabel(logoIcon, SwingConstants.CENTER);
        } else {
            lblBank = new JLabel("ATM Bank UNSIA", SwingConstants.CENTER);
            lblBank.setFont(FONT_TITLE);
            lblBank.setForeground(COLOR_ACCENT);
        }
        header.add(lblBank, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 0, 0);
        JLabel lblSub = new JLabel("Mesin Anjungan Tunai Mandiri", SwingConstants.CENTER);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(COLOR_SUBTEXT);
        header.add(lblSub, gbc);

        panel.add(header, BorderLayout.NORTH);

        // --- Tengah: PIN Dots + Keypad ---
        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setBackground(COLOR_BG);
        center.setBorder(new EmptyBorder(10, 30, 20, 30));

        // -- PIN Input Area --
        JPanel pinArea = new JPanel(new BorderLayout(0, 14));
        pinArea.setBackground(COLOR_CARD);
        pinArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblMasukkanPin = new JLabel("Masukkan PIN Anda", SwingConstants.CENTER);
        lblMasukkanPin.setFont(FONT_HEADER);
        lblMasukkanPin.setForeground(COLOR_TEXT);

        // Dots indikator PIN (6 titik)
        JPanel dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dotsPanel.setBackground(COLOR_CARD);
        pinDots = new JLabel[6];
        // Logika FOR untuk membuat 6 dot PIN
        for (int i = 0; i < 6; i++) {
            pinDots[i] = new JLabel("○");
            pinDots[i].setFont(FONT_PIN_DOT);
            pinDots[i].setForeground(COLOR_BORDER);
            dotsPanel.add(pinDots[i]);
        }

        lblPinError = new JLabel("", SwingConstants.CENTER);
        lblPinError.setFont(FONT_SMALL);
        lblPinError.setForeground(COLOR_DANGER);

        lblPercobaanSisa = new JLabel("", SwingConstants.CENTER);
        lblPercobaanSisa.setFont(FONT_SMALL);
        lblPercobaanSisa.setForeground(COLOR_WARNING);

        pinArea.add(lblMasukkanPin, BorderLayout.NORTH);
        pinArea.add(dotsPanel, BorderLayout.CENTER);
        JPanel errPanel = new JPanel(new GridLayout(2,1));
        errPanel.setBackground(COLOR_CARD);
        errPanel.add(lblPinError);
        errPanel.add(lblPercobaanSisa);
        pinArea.add(errPanel, BorderLayout.SOUTH);

        // -- Keypad --
        JPanel keypad = new JPanel(new GridLayout(4, 3, 8, 8));
        keypad.setBackground(COLOR_BG);

        // Logika FOR untuk membuat tombol 1-9
        String[] tombol = {"1","2","3","4","5","6","7","8","9","Hapus","0","OK"};
        for (int i = 0; i < tombol.length; i++) {
            String label = tombol[i];
            boolean isAction = label.equals("Hapus") || label.equals("OK");
            JButton btn = buatTombolKeypad(label, isAction);

            // Logika SWITCH untuk aksi keypad
            btn.addActionListener(e -> {
                switch (label) {
                    case "Hapus":
                        if (pinInput.length() > 0) {
                            pinInput.deleteCharAt(pinInput.length() - 1);
                            updatePinDots();
                        }
                        break;
                    case "OK":
                        prosesPin();
                        break;
                    default:
                        if (pinInput.length() < 6) {
                            pinInput.append(label);
                            updatePinDots();
                        }
                        break;
                }
            });
            keypad.add(btn);
        }

        center.add(pinArea, BorderLayout.NORTH);
        center.add(keypad, BorderLayout.CENTER);

        // --- Footer ---
        JLabel footer = new JLabel("© 2026 Bank UNSIA - Aman & Terpercaya", SwingConstants.CENTER);
        footer.setFont(FONT_SMALL);
        footer.setForeground(COLOR_SUBTEXT);
        footer.setBorder(new EmptyBorder(10, 0, 20, 0));

        panel.add(center, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private void updatePinDots() {
        // Logika FOR untuk update tampilan dots
        for (int i = 0; i < 6; i++) {
            if (i < pinInput.length()) {
                pinDots[i].setText("●");
                pinDots[i].setForeground(COLOR_ACCENT);
            } else {
                pinDots[i].setText("○");
                pinDots[i].setForeground(COLOR_BORDER);
            }
        }
    }

    private void prosesPin() {
        // Logika IF untuk validasi PIN
        if (pinInput.length() == 0) {
            lblPinError.setText("PIN tidak boleh kosong.");
            return;
        }
        if (atm.validasiPin(pinInput.toString())) {
            // PIN Benar
            percobaan = 0;
            pinInput.setLength(0);
            updatePinDots();
            lblPinError.setText("");
            lblPercobaanSisa.setText("");
            refreshDashboard();
            cardLayout.show(mainPanel, "DASHBOARD");
        } else {
            // PIN Salah
            percobaan++;
            pinInput.setLength(0);
            updatePinDots();
            int sisa = MAX_PERCOBAAN - percobaan;
            lblPinError.setText("PIN salah! Silakan coba lagi.");
            if (sisa > 0) {
                lblPercobaanSisa.setText("Sisa percobaan: " + sisa + "x");
            }
            // Logika IF - blokir kartu jika melebihi batas
            if (percobaan >= MAX_PERCOBAAN) {
                tampilkanHasil("Kartu Anda telah diblokir!\nMaksimal " + MAX_PERCOBAAN + " percobaan PIN terlampaui.", false);
                System.exit(0);
            }
        }
    }

    // ============================================================
    // Layar 2: Dashboard / Menu Utama (7 Menu)
    // ============================================================
    private JPanel buatLayarDashboard() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(COLOR_BG);

        // --- Header Nasabah ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_ACCENT.darker().darker());
        header.setBorder(new EmptyBorder(20, 24, 20, 24));

        lblNamaHeader = new JLabel("👤 " + atm.getNamaNasabah());
        lblNamaHeader.setFont(FONT_HEADER);
        lblNamaHeader.setForeground(Color.WHITE);

        JLabel lblRek = new JLabel("Rek: " + atm.getNomorRekening());
        lblRek.setFont(FONT_SMALL);
        lblRek.setForeground(new Color(200, 220, 255));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        infoPanel.setOpaque(false);
        infoPanel.add(lblNamaHeader);
        infoPanel.add(lblRek);

        ImageIcon logoIcon = dapatkanLogo(28); // Tinggi 28px
        JLabel lblBankLogo;
        if (logoIcon != null && logoIcon.getIconWidth() > 0) {
            lblBankLogo = new JLabel(logoIcon);
        } else {
            lblBankLogo = new JLabel("Bank UNSIA");
            lblBankLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblBankLogo.setForeground(Color.WHITE);
        }

        header.add(infoPanel, BorderLayout.WEST);
        header.add(lblBankLogo, BorderLayout.EAST);

        // --- Kartu Saldo ---
        JPanel saldoCard = new JPanel(new BorderLayout());
        saldoCard.setBackground(COLOR_CARD);
        saldoCard.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblSaldoLabel = new JLabel("Saldo Rekening");
        lblSaldoLabel.setFont(FONT_SMALL);
        lblSaldoLabel.setForeground(COLOR_SUBTEXT);

        lblSaldoDashboard = new JLabel(ATM.formatRupiah(atm.getSaldo()));
        lblSaldoDashboard.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblSaldoDashboard.setForeground(COLOR_SUCCESS);

        saldoCard.add(lblSaldoLabel, BorderLayout.NORTH);
        saldoCard.add(lblSaldoDashboard, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_BG);
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(saldoCard, BorderLayout.SOUTH);

        // --- Grid 7 Menu ---
        JPanel menuGrid = new JPanel(new GridLayout(3, 3, 10, 10));
        menuGrid.setBackground(COLOR_BG);
        menuGrid.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Data menu: label, ikon, warna, card-key
        // Logika FOR untuk membuat semua tombol menu
        String[][] menus = {
            {"Cek Saldo",  "💰" ,   "ACCENT",   "CEK_SALDO"},
            {"Tarik Tunai",  "💸" ,     "DANGER",   "TARIK_TUNAI"},
            {"Setor Tunai",  "💵" ,    "SUCCESS",  "SETOR_TUNAI"},
            {"Transfer",  "↗️" ,        "WARNING",  "TRANSFER"},
            {"Ubah PIN",  "🔐" ,        "ACCENT",   "UBAH_PIN"},
            {"Mutasi Rekening",  "📋" ,"SUBTEXT",  "MUTASI"},
            {"Keluar",  "🚪" ,        "DANGER",   "KELUAR"},
        };

        for (int i = 0; i < menus.length; i++) {
            String label   = menus[i][0];
            String ikon    = menus[i][1];
            String warnaKey = menus[i][2];


            // Logika IF untuk menentukan warna
            Color warna;
            if (warnaKey.equals("ACCENT"))    warna = COLOR_ACCENT;
            else if (warnaKey.equals("DANGER"))  warna = COLOR_DANGER;
            else if (warnaKey.equals("SUCCESS")) warna = COLOR_SUCCESS;
            else if (warnaKey.equals("WARNING")) warna = COLOR_WARNING;
            else                                  warna = COLOR_SUBTEXT;

            // Nomor menu (i+1)
            int nomorMenu = i + 1;
            JButton btn = buatTombolMenu(nomorMenu + ". " + label, ikon, warna);

            btn.addActionListener(e -> {
                // Logika SWITCH untuk navigasi menu
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
                    case 7:
                        keluarAplikasi();
                        break;
                }
            });
            menuGrid.add(btn);
        }
        // Panel placeholder untuk cell ke-8 dan ke-9 agar grid rapi
        menuGrid.add(new JLabel());
        menuGrid.add(new JLabel());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(menuGrid, BorderLayout.CENTER);

        JLabel hint = new JLabel("Pilih menu di atas atau gunakan keyboard angka 1-7", SwingConstants.CENTER);
        hint.setFont(FONT_SMALL);
        hint.setForeground(COLOR_SUBTEXT);
        hint.setBorder(new EmptyBorder(0, 0, 14, 0));
        panel.add(hint, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshDashboard() {
        lblSaldoDashboard.setText(ATM.formatRupiah(atm.getSaldo()));
        lblNamaHeader.setText(atm.getNamaNasabah());
    }

    // ============================================================
    // Layar 3: Cek Saldo (Menu 1)
    // ============================================================
    private JPanel buatLayarCekSaldo() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG);
        JLabel lblJudul = new JLabel("Informasi Saldo", SwingConstants.CENTER);
        lblJudul.setFont(FONT_TITLE);
        lblJudul.setForeground(COLOR_ACCENT);
        header.add(lblJudul, BorderLayout.CENTER);

        // Card saldo utama
        JPanel card = new JPanel(new GridLayout(5, 1, 8, 8));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(24, 24, 24, 24)
        ));

        JLabel lblNama = new JLabel("Nama Nasabah");
        lblNama.setFont(FONT_SMALL);
        lblNama.setForeground(COLOR_SUBTEXT);

        JLabel lblNamaVal = new JLabel(atm.getNamaNasabah());
        lblNamaVal.setFont(FONT_HEADER);
        lblNamaVal.setForeground(COLOR_TEXT);

        JLabel lblRek = new JLabel("Nomor Rekening");
        lblRek.setFont(FONT_SMALL);
        lblRek.setForeground(COLOR_SUBTEXT);

        JLabel lblRekVal = new JLabel(atm.getNomorRekening());
        lblRekVal.setFont(FONT_HEADER);
        lblRekVal.setForeground(COLOR_TEXT);

        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDER);

        card.add(lblNama);
        card.add(lblNamaVal);
        card.add(lblRek);
        card.add(lblRekVal);
        card.add(sep);

        // Card saldo besar
        JPanel saldoCard = new JPanel(new BorderLayout(0, 8));
        saldoCard.setBackground(new Color(20, 60, 30));
        saldoCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_SUCCESS, 1, true),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblSaldoLabel = new JLabel("Saldo Saat Ini", SwingConstants.CENTER);
        lblSaldoLabel.setFont(FONT_SMALL);
        lblSaldoLabel.setForeground(COLOR_SUCCESS);

        JLabel lblSaldoNilai = new JLabel(ATM.formatRupiah(atm.getSaldo()), SwingConstants.CENTER);
        lblSaldoNilai.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblSaldoNilai.setForeground(COLOR_SUCCESS);

        saldoCard.add(lblSaldoLabel, BorderLayout.NORTH);
        saldoCard.add(lblSaldoNilai, BorderLayout.CENTER);

        // Override nilai saldo saat layar ditampilkan
        panel.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent e) {
                lblSaldoNilai.setText(ATM.formatRupiah(atm.getSaldo()));
                lblNamaVal.setText(atm.getNamaNasabah());
                lblRekVal.setText(atm.getNomorRekening());
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent e) {}
            public void ancestorMoved(javax.swing.event.AncestorEvent e) {}
        });

        panel.add(header, BorderLayout.NORTH);
        JPanel tengah = new JPanel(new BorderLayout(0, 14));
        tengah.setBackground(COLOR_BG);
        tengah.add(card, BorderLayout.NORTH);
        tengah.add(saldoCard, BorderLayout.CENTER);
        panel.add(tengah, BorderLayout.CENTER);
        panel.add(buatTombolKembali(), BorderLayout.SOUTH);

        return panel;
    }

    // ============================================================
    // Layar 4: Tarik Tunai (Menu 2)
    // ============================================================
    private JPanel buatLayarTarikTunai() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblJudul = new JLabel("Tarik Tunai", SwingConstants.CENTER);
        lblJudul.setFont(FONT_TITLE);
        lblJudul.setForeground(COLOR_DANGER);

        // Form input
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 12));
        form.setBackground(COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblInfo = new JLabel("<html><b>Ketentuan:</b><br>• Kelipatan Rp 50.000<br>• Maks. Rp 5.000.000 per transaksi</html>");
        lblInfo.setFont(FONT_SMALL);
        lblInfo.setForeground(COLOR_SUBTEXT);

        JLabel lblNominalLabel = new JLabel("Nominal Penarikan (Rp):");
        lblNominalLabel.setFont(FONT_BODY);
        lblNominalLabel.setForeground(COLOR_TEXT);

        JTextField tfNominal = buatTextField("Contoh: 500000");
        tfNominal.setPreferredSize(new Dimension(0, 44));

        // Tombol nominal cepat (logika FOR)
        JPanel quickBtns = new JPanel(new GridLayout(2, 3, 6, 6));
        quickBtns.setBackground(COLOR_CARD);
        long[] quickNominals = {50_000, 100_000, 200_000, 500_000, 1_000_000, 2_000_000};
        for (int i = 0; i < quickNominals.length; i++) {
            long nom = quickNominals[i];
            JButton btn = new JButton(ATM.formatRupiah(nom));
            btn.setFont(FONT_SMALL);
            btn.setBackground(COLOR_CARD2);
            btn.setForeground(COLOR_TEXT);
            btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 2, 6, 2)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> tfNominal.setText(String.valueOf(nom)));
            quickBtns.add(btn);
        }

        JButton btnTarik = new JButton("Tarik Sekarang");
        btnTarik.setFont(FONT_HEADER);
        btnTarik.setBackground(COLOR_DANGER);
        btnTarik.setForeground(Color.WHITE);
        btnTarik.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnTarik.setFocusPainted(false);
        btnTarik.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTarik.addActionListener(e -> {
            try {
                long nominal = Long.parseLong(tfNominal.getText().trim().replace(".", "").replace(",", ""));
                String hasil = atm.tarikTunai(nominal);
                String[] parts = hasil.split(":", 2);
                // Logika IF untuk mengecek status hasil
                if (parts[0].equals("OK")) {
                    tampilkanHasil(parts[1], true);
                    refreshDashboard();
                    tfNominal.setText("");
                } else {
                    tampilkanHasil(parts[1], false);
                }
            } catch (NumberFormatException ex) {
                tampilkanHasil("Masukkan angka yang valid.", false);
            }
        });

        form.add(lblInfo);
        form.add(lblNominalLabel);
        form.add(tfNominal);
        form.add(new JLabel("Atau pilih nominal cepat:", SwingConstants.LEFT));
        form.add(quickBtns);
        form.add(btnTarik);

        panel.add(lblJudul, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buatTombolKembali(), BorderLayout.SOUTH);

        return panel;
    }

    // ============================================================
    // Layar 5: Setor Tunai (Menu 3)
    // ============================================================
    private JPanel buatLayarSetorTunai() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblJudul = new JLabel("Setor Tunai", SwingConstants.CENTER);
        lblJudul.setFont(FONT_TITLE);
        lblJudul.setForeground(COLOR_SUCCESS);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 12));
        form.setBackground(COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblInfo = new JLabel("<html><b>Ketentuan:</b><br>• Minimal setoran Rp 50.000</html>");
        lblInfo.setFont(FONT_SMALL);
        lblInfo.setForeground(COLOR_SUBTEXT);

        JLabel lblNominalLabel = new JLabel("Nominal Setoran (Rp):");
        lblNominalLabel.setFont(FONT_BODY);
        lblNominalLabel.setForeground(COLOR_TEXT);

        JTextField tfNominal = buatTextField("Masukkan nominal setoran...");

        JButton btnSetor = new JButton("Setor Sekarang");
        btnSetor.setFont(FONT_HEADER);
        btnSetor.setBackground(COLOR_SUCCESS);
        btnSetor.setForeground(Color.WHITE);
        btnSetor.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnSetor.setFocusPainted(false);
        btnSetor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSetor.addActionListener(e -> {
            try {
                long nominal = Long.parseLong(tfNominal.getText().trim().replace(".", "").replace(",", ""));
                String hasil = atm.setorTunai(nominal);
                String[] parts = hasil.split(":", 2);
                if (parts[0].equals("OK")) {
                    tampilkanHasil(parts[1], true);
                    refreshDashboard();
                    tfNominal.setText("");
                } else {
                    tampilkanHasil(parts[1], false);
                }
            } catch (NumberFormatException ex) {
                tampilkanHasil("Masukkan angka yang valid.", false);
            }
        });

        form.add(lblInfo);
        form.add(lblNominalLabel);
        form.add(tfNominal);
        form.add(btnSetor);

        panel.add(lblJudul, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buatTombolKembali(), BorderLayout.SOUTH);

        return panel;
    }

    // ============================================================
    // Layar 6: Transfer (Menu 4)
    // ============================================================
    private JPanel buatLayarTransfer() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblJudul = new JLabel("Transfer Dana", SwingConstants.CENTER);
        lblJudul.setFont(FONT_TITLE);
        lblJudul.setForeground(COLOR_WARNING);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 12));
        form.setBackground(COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblRekLabel = new JLabel("Nomor Rekening Tujuan:");
        lblRekLabel.setFont(FONT_BODY);
        lblRekLabel.setForeground(COLOR_TEXT);

        JTextField tfRek = buatTextField("Contoh: 1234-5678-0000");

        JLabel lblNominalLabel = new JLabel("Nominal Transfer (Rp):");
        lblNominalLabel.setFont(FONT_BODY);
        lblNominalLabel.setForeground(COLOR_TEXT);

        JTextField tfNominal = buatTextField("Masukkan nominal transfer...");

        JButton btnTransfer = new JButton("Transfer Sekarang");
        btnTransfer.setFont(FONT_HEADER);
        btnTransfer.setBackground(COLOR_WARNING.darker());
        btnTransfer.setForeground(Color.WHITE);
        btnTransfer.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnTransfer.setFocusPainted(false);
        btnTransfer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTransfer.addActionListener(e -> {
            try {
                String noRek = tfRek.getText().trim();
                long nominal = Long.parseLong(tfNominal.getText().trim().replace(".", "").replace(",", ""));
                String hasil = atm.transfer(noRek, nominal);
                String[] parts = hasil.split(":", 2);
                if (parts[0].equals("OK")) {
                    tampilkanHasil(parts[1], true);
                    refreshDashboard();
                    tfRek.setText("");
                    tfNominal.setText("");
                } else {
                    tampilkanHasil(parts[1], false);
                }
            } catch (NumberFormatException ex) {
                tampilkanHasil("Masukkan angka yang valid.", false);
            }
        });

        form.add(lblRekLabel);
        form.add(tfRek);
        form.add(lblNominalLabel);
        form.add(tfNominal);
        form.add(btnTransfer);

        panel.add(lblJudul, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buatTombolKembali(), BorderLayout.SOUTH);

        return panel;
    }

    // ============================================================
    // Layar 7: Ubah PIN (Menu 5)
    // ============================================================
    private JPanel buatLayarUbahPin() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblJudul = new JLabel("Ubah PIN", SwingConstants.CENTER);
        lblJudul.setFont(FONT_TITLE);
        lblJudul.setForeground(COLOR_ACCENT);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 12));
        form.setBackground(COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblOldPin = new JLabel("PIN Lama:");
        lblOldPin.setFont(FONT_BODY);
        lblOldPin.setForeground(COLOR_TEXT);
        JPasswordField tfOldPin = buatPasswordField("Masukkan PIN lama");

        JLabel lblNewPin = new JLabel("PIN Baru (min. 6 digit):");
        lblNewPin.setFont(FONT_BODY);
        lblNewPin.setForeground(COLOR_TEXT);
        JPasswordField tfNewPin = buatPasswordField("Masukkan PIN baru");

        JLabel lblConfPin = new JLabel("Konfirmasi PIN Baru:");
        lblConfPin.setFont(FONT_BODY);
        lblConfPin.setForeground(COLOR_TEXT);
        JPasswordField tfConfPin = buatPasswordField("Ulangi PIN baru");

        JButton btnUbah = new JButton("Ubah PIN");
        btnUbah.setFont(FONT_HEADER);
        btnUbah.setBackground(COLOR_ACCENT);
        btnUbah.setForeground(Color.WHITE);
        btnUbah.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnUbah.setFocusPainted(false);
        btnUbah.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUbah.addActionListener(e -> {
            String oldPin  = new String(tfOldPin.getPassword());
            String newPin  = new String(tfNewPin.getPassword());
            String confPin = new String(tfConfPin.getPassword());
            String hasil   = atm.ubahPin(oldPin, newPin, confPin);
            String[] parts = hasil.split(":", 2);
            if (parts[0].equals("OK")) {
                tampilkanHasil(parts[1], true);
                tfOldPin.setText("");
                tfNewPin.setText("");
                tfConfPin.setText("");
            } else {
                tampilkanHasil(parts[1], false);
            }
        });

        form.add(lblOldPin);
        form.add(tfOldPin);
        form.add(lblNewPin);
        form.add(tfNewPin);
        form.add(lblConfPin);
        form.add(tfConfPin);
        form.add(btnUbah);

        panel.add(lblJudul, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(buatTombolKembali(), BorderLayout.SOUTH);

        return panel;
    }

    // ============================================================
    // Layar 8: Mutasi Rekening (Menu 6)
    // ============================================================
    private JPanel mutasiPanel;
    private JPanel mutasiList;

    private JPanel buatLayarMutasi() {
        mutasiPanel = new JPanel(new BorderLayout(0, 16));
        mutasiPanel.setBackground(COLOR_BG);
        mutasiPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel lblJudul = new JLabel("Mutasi Rekening", SwingConstants.CENTER);
        lblJudul.setFont(FONT_TITLE);
        lblJudul.setForeground(COLOR_SUBTEXT);

        mutasiList = new JPanel();
        mutasiList.setLayout(new BoxLayout(mutasiList, BoxLayout.Y_AXIS));
        mutasiList.setBackground(COLOR_BG);

        JScrollPane scroll = new JScrollPane(mutasiList);
        scroll.setBackground(COLOR_BG);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_BG);

        mutasiPanel.add(lblJudul, BorderLayout.NORTH);
        mutasiPanel.add(scroll, BorderLayout.CENTER);
        mutasiPanel.add(buatTombolKembali(), BorderLayout.SOUTH);

        return mutasiPanel;
    }

    private void refreshMutasi() {
        mutasiList.removeAll();
        List<Transaction> riwayat = atm.getRiwayat();

        // Logika IF - jika belum ada transaksi
        if (riwayat.isEmpty()) {
            JLabel lbl = new JLabel("Belum ada riwayat transaksi.", SwingConstants.CENTER);
            lbl.setFont(FONT_BODY);
            lbl.setForeground(COLOR_SUBTEXT);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            mutasiList.add(Box.createVerticalStrut(20));
            mutasiList.add(lbl);
        } else {
            // Logika FOR untuk menampilkan semua riwayat
            for (int i = riwayat.size() - 1; i >= 0; i--) {
                Transaction tx = riwayat.get(i);
                JPanel row = new JPanel(new BorderLayout(8, 4));
                row.setBackground(COLOR_CARD);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                row.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_BORDER, 1, true),
                    new EmptyBorder(10, 14, 10, 14)
                ));

                // Tentukan warna berdasarkan jenis transaksi (logika IF)
                Color warna;
                String ikon;
                if (tx.getJenis().startsWith("Tarik")) {
                    warna = COLOR_DANGER;
                    ikon  = "↑";
                } else if (tx.getJenis().startsWith("Setor")) {
                    warna = COLOR_SUCCESS;
                    ikon  = "↓";
                } else {
                    warna = COLOR_WARNING;
                    ikon  = "→";
                }

                JLabel lblJenis = new JLabel(ikon + " " + tx.getJenis());
                lblJenis.setFont(FONT_BODY);
                lblJenis.setForeground(warna);

                JLabel lblWaktu = new JLabel(tx.getWaktu());
                lblWaktu.setFont(FONT_SMALL);
                lblWaktu.setForeground(COLOR_SUBTEXT);

                JLabel lblNominal = new JLabel(ATM.formatRupiah(tx.getNominal()));
                lblNominal.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lblNominal.setForeground(warna);
                lblNominal.setHorizontalAlignment(SwingConstants.RIGHT);

                JLabel lblSaldo = new JLabel("Saldo: " + ATM.formatRupiah(tx.getSaldoAkhir()));
                lblSaldo.setFont(FONT_SMALL);
                lblSaldo.setForeground(COLOR_SUBTEXT);
                lblSaldo.setHorizontalAlignment(SwingConstants.RIGHT);

                JPanel leftPanel = new JPanel(new GridLayout(2, 1));
                leftPanel.setBackground(COLOR_CARD);
                leftPanel.add(lblJenis);
                leftPanel.add(lblWaktu);

                JPanel rightPanel = new JPanel(new GridLayout(2, 1));
                rightPanel.setBackground(COLOR_CARD);
                rightPanel.add(lblNominal);
                rightPanel.add(lblSaldo);

                row.add(leftPanel, BorderLayout.WEST);
                row.add(rightPanel, BorderLayout.EAST);

                mutasiList.add(row);
                mutasiList.add(Box.createVerticalStrut(6));
            }
        }
        mutasiList.revalidate();
        mutasiList.repaint();
    }

    // ============================================================
    // Menu 7: Keluar
    // ============================================================
    private void keluarAplikasi() {
        // Dialog keluar dengan pesan terima kasih
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        ImageIcon logoIcon = dapatkanLogo(65); // Tinggi 65px
        JLabel lblIkon;
        if (logoIcon != null && logoIcon.getIconWidth() > 0) {
            lblIkon = new JLabel(logoIcon, SwingConstants.CENTER);
        } else {
            lblIkon = new JLabel("", SwingConstants.CENTER);
            lblIkon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        }

        JLabel lblPesan = new JLabel(
            "<html><center><b>Terima kasih telah menggunakan ATM Bank UNSIA!</b><br><br>" +
            "Semoga hari Anda menyenangkan.<br>Ambil kartu Anda dan barang bawaan Anda.<br><br>" +
            "<i>— Bank UNSIA, Bank Terpercaya Indonesia —</i></center></html>",
            SwingConstants.CENTER
        );
        lblPesan.setFont(FONT_BODY);
        lblPesan.setForeground(COLOR_TEXT);

        panel.add(lblIkon, BorderLayout.NORTH);
        panel.add(lblPesan, BorderLayout.CENTER);

        int pilihan = JOptionPane.showConfirmDialog(
            this, panel, "Keluar dari ATM",
            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        // Logika IF untuk konfirmasi keluar
        if (pilihan == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ============================================================
    // Main Method - Entry Point
    // ============================================================
    public static void main(String[] args) {
        // Setup FlatLaf Dark Theme
        try {
            FlatDarkLaf.setup();
            UIManager.put("Component.arc", 8);
            UIManager.put("Button.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.width", 8);
        } catch (Exception e) {
            System.err.println("Gagal memuat FlatLaf: " + e.getMessage());
        }

        // Jalankan GUI di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new ATMApplication());
    }
}
