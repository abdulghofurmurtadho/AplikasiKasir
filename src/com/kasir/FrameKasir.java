package com.kasir;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Tampilan utama aplikasi kasir (Java Swing).
 */
public class FrameKasir extends JFrame {

    private final Transaksi transaksi = new Transaksi();

    private JTextField txtNama;
    private JTextField txtHarga;
    private JTextField txtJumlah;
    private JTextField txtBayar;
    private JLabel lblTotal;
    private JLabel lblKembalian;
    private JTable tabel;
    private DefaultTableModel modelTabel;

    public FrameKasir() {
        initKomponen();
    }

    private void initKomponen() {
        setTitle("Aplikasi Kasir (Point of Sale)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buatPanelInput(), BorderLayout.NORTH);
        add(buatPanelTabel(), BorderLayout.CENTER);
        add(buatPanelPembayaran(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);
    }

    // ---------------------------------------------------------------- Panel Input
    private JPanel buatPanelInput() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Input Barang"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        txtNama = new JTextField(20);
        txtHarga = new JTextField(12);
        txtJumlah = new JTextField(6);

        // Format harga otomatis (2000000 -> 2.000.000) saat kolom ditinggalkan
        txtHarga.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    long harga = Transaksi.parseAngka(txtHarga.getText());
                    java.text.NumberFormat nf = java.text.NumberFormat
                            .getInstance(java.util.Locale.forLanguageTag("id-ID"));
                    txtHarga.setText(nf.format(harga));
                } catch (NumberFormatException ex) {
                    // dibiarkan; validasi dilakukan saat tombol Tambah ditekan
                }
            }
        });

        JButton btnTambah = new JButton("Tambah ke Keranjang");
        btnTambah.addActionListener(e -> tambahBarang());
        // Tekan Enter di kolom jumlah = Tambah
        txtJumlah.addActionListener(e -> tambahBarang());

        JButton btnHapus = new JButton("Hapus Item Terpilih");
        btnHapus.addActionListener(e -> hapusItemTerpilih());

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        panel.add(new JLabel("Nama Barang"), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(txtNama, c);

        c.gridx = 2; c.weightx = 0;
        panel.add(new JLabel("Harga (Rp)"), c);
        c.gridx = 3; c.weightx = 0.6;
        panel.add(txtHarga, c);

        c.gridx = 4; c.weightx = 0;
        panel.add(new JLabel("Jumlah"), c);
        c.gridx = 5; c.weightx = 0.2;
        panel.add(txtJumlah, c);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelTombol.add(btnTambah);
        panelTombol.add(btnHapus);
        c.gridx = 1; c.gridy = 1; c.gridwidth = 5; c.weightx = 1;
        panel.add(panelTombol, c);

        return panel;
    }

    // ---------------------------------------------------------------- Panel Tabel
    private JScrollPane buatPanelTabel() {
        modelTabel = new DefaultTableModel(
                new Object[]{"No", "Nama Barang", "Harga", "Jumlah", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(modelTabel);
        tabel.setRowHeight(24);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer kanan = new DefaultTableCellRenderer();
        kanan.setHorizontalAlignment(SwingConstants.RIGHT);
        tabel.getColumnModel().getColumn(2).setCellRenderer(kanan);
        tabel.getColumnModel().getColumn(3).setCellRenderer(kanan);
        tabel.getColumnModel().getColumn(4).setCellRenderer(kanan);
        tabel.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Belanja"));
        scroll.setPreferredSize(new Dimension(700, 250));
        return scroll;
    }

    // ---------------------------------------------------------------- Panel Pembayaran
    private JPanel buatPanelPembayaran() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel info = new JPanel(new GridLayout(3, 2, 8, 8));
        info.setBorder(BorderFactory.createTitledBorder("Pembayaran"));

        lblTotal = new JLabel(Transaksi.formatRupiah(0), SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotal.setForeground(new Color(0, 100, 0));

        txtBayar = new JTextField();
        txtBayar.setHorizontalAlignment(SwingConstants.RIGHT);
        txtBayar.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtBayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateKembalian(); }
            @Override public void removeUpdate(DocumentEvent e) { updateKembalian(); }
            @Override public void changedUpdate(DocumentEvent e) { updateKembalian(); }
        });

        lblKembalian = new JLabel(Transaksi.formatRupiah(0), SwingConstants.RIGHT);
        lblKembalian.setFont(new Font("SansSerif", Font.BOLD, 22));

        info.add(new JLabel("Total Bayar"));
        info.add(lblTotal);
        info.add(new JLabel("Uang Bayar (Rp)"));
        info.add(txtBayar);
        info.add(new JLabel("Kembalian"));
        info.add(lblKembalian);

        JButton btnBayar = new JButton("Bayar & Cetak Struk");
        btnBayar.setFont(btnBayar.getFont().deriveFont(Font.BOLD, 14f));
        btnBayar.addActionListener(e -> bayarDanCetak());

        JButton btnBaru = new JButton("Transaksi Baru");
        btnBaru.setFont(btnBaru.getFont().deriveFont(Font.BOLD, 14f));
        btnBaru.addActionListener(e -> transaksiBaru());

        JPanel tombol = new JPanel(new GridLayout(2, 1, 8, 8));
        tombol.add(btnBayar);
        tombol.add(btnBaru);

        panel.add(info, BorderLayout.CENTER);
        panel.add(tombol, BorderLayout.EAST);
        return panel;
    }

    // ---------------------------------------------------------------- Logika Aksi
    private void tambahBarang() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            tampilkanError("Nama barang tidak boleh kosong.", txtNama);
            return;
        }

        long harga;
        try {
            harga = Transaksi.parseAngka(txtHarga.getText());
        } catch (NumberFormatException ex) {
            tampilkanError("Harga harus berupa angka. Contoh: 2000000 atau 2.000.000", txtHarga);
            return;
        }
        if (harga <= 0) {
            tampilkanError("Harga harus lebih dari 0.", txtHarga);
            return;
        }

        int jumlah;
        try {
            long j = Transaksi.parseAngka(txtJumlah.getText());
            if (j <= 0 || j > Integer.MAX_VALUE) {
                throw new NumberFormatException();
            }
            jumlah = (int) j;
        } catch (NumberFormatException ex) {
            tampilkanError("Jumlah beli harus berupa angka bulat lebih dari 0.", txtJumlah);
            return;
        }

        transaksi.tambahBarang(new Barang(nama, harga, jumlah));
        refreshTabel();

        txtNama.setText("");
        txtHarga.setText("");
        txtJumlah.setText("");
        txtNama.requestFocus();
    }

    private void hapusItemTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu barang di tabel yang akan dihapus.",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        transaksi.hapusBarang(baris);
        refreshTabel();
    }

    private void refreshTabel() {
        modelTabel.setRowCount(0);
        int no = 1;
        for (Barang b : transaksi.getDaftarBarang()) {
            modelTabel.addRow(new Object[]{
                no++,
                b.getNama(),
                Transaksi.formatRupiah(b.getHarga()),
                b.getJumlah(),
                Transaksi.formatRupiah(b.getSubtotal())
            });
        }
        lblTotal.setText(Transaksi.formatRupiah(transaksi.hitungTotal()));
        updateKembalian();
    }

    private void updateKembalian() {
        String teks = txtBayar.getText().trim();
        if (teks.isEmpty()) {
            lblKembalian.setText(Transaksi.formatRupiah(0));
            lblKembalian.setForeground(Color.BLACK);
            return;
        }
        try {
            long bayar = Transaksi.parseAngka(teks);
            long kembali = transaksi.hitungKembalian(bayar);
            if (kembali < 0) {
                lblKembalian.setText("Kurang " + Transaksi.formatRupiah(-kembali));
                lblKembalian.setForeground(Color.RED);
            } else {
                lblKembalian.setText(Transaksi.formatRupiah(kembali));
                lblKembalian.setForeground(Color.BLACK);
            }
        } catch (NumberFormatException ex) {
            lblKembalian.setText("-");
            lblKembalian.setForeground(Color.RED);
        }
    }

    private void bayarDanCetak() {
        if (transaksi.isKosong()) {
            tampilkanError("Keranjang masih kosong. Tambahkan barang terlebih dahulu.", txtNama);
            return;
        }

        long bayar;
        try {
            bayar = Transaksi.parseAngka(txtBayar.getText());
        } catch (NumberFormatException ex) {
            tampilkanError("Uang bayar harus berupa angka. Contoh: 2500000 atau 2.500.000", txtBayar);
            return;
        }

        if (bayar < transaksi.hitungTotal()) {
            tampilkanError("Uang bayar kurang dari total belanja.", txtBayar);
            return;
        }

        JTextArea area = new JTextArea(transaksi.cetakStruk(bayar));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 380));

        JOptionPane.showMessageDialog(this, scroll, "Struk Pembelian", JOptionPane.PLAIN_MESSAGE);
    }

    private void transaksiBaru() {
        transaksi.reset();
        modelTabel.setRowCount(0);
        txtNama.setText("");
        txtHarga.setText("");
        txtJumlah.setText("");
        txtBayar.setText("");
        lblTotal.setText(Transaksi.formatRupiah(0));
        lblKembalian.setText(Transaksi.formatRupiah(0));
        lblKembalian.setForeground(Color.BLACK);
        txtNama.requestFocus();
    }

    private void tampilkanError(String pesan, JTextField fokus) {
        JOptionPane.showMessageDialog(this, pesan, "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        if (fokus != null) {
            fokus.requestFocus();
            fokus.selectAll();
        }
    }
}
