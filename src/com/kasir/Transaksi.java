package com.kasir;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Menyimpan daftar belanja, menghitung total & kembalian, serta membuat teks struk.
 */
public class Transaksi {

    private final List<Barang> daftarBarang = new ArrayList<>();

    public void tambahBarang(Barang barang) {
        daftarBarang.add(barang);
    }

    public void hapusBarang(int index) {
        if (index >= 0 && index < daftarBarang.size()) {
            daftarBarang.remove(index);
        }
    }

    public List<Barang> getDaftarBarang() {
        return daftarBarang;
    }

    public boolean isKosong() {
        return daftarBarang.isEmpty();
    }

    public long hitungTotal() {
        long total = 0;
        for (Barang b : daftarBarang) {
            total += b.getSubtotal();
        }
        return total;
    }

    public long hitungKembalian(long uangBayar) {
        return uangBayar - hitungTotal();
    }

    public void reset() {
        daftarBarang.clear();
    }

    /**
     * Membersihkan input angka dari tanda titik, koma, spasi, dan teks "Rp",
     * lalu mengubahnya menjadi long. Contoh: "2.000.000" -> 2000000.
     *
     * @throws NumberFormatException jika hasilnya bukan angka valid
     */
    public static long parseAngka(String teks) {
        if (teks == null) {
            throw new NumberFormatException("Input kosong");
        }
        String bersih = teks.replaceAll("(?i)rp", "").replaceAll("[.,\\s]", "");
        if (bersih.isEmpty()) {
            throw new NumberFormatException("Input kosong");
        }
        return Long.parseLong(bersih);
    }

    public static String formatRupiah(long nilai) {
        NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag("id-ID"));
        return "Rp " + nf.format(nilai);
    }

    public String cetakStruk(long uangBayar) {
        StringBuilder sb = new StringBuilder();
        String garis = "========================================\n";
        String garisPutus = "----------------------------------------\n";

        sb.append(garis);
        sb.append("             TOKO SERBA ADA\n");
        sb.append("          STRUK PEMBELIAN\n");
        sb.append(garis);
        sb.append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        sb.append(garisPutus);

        for (Barang b : daftarBarang) {
            sb.append(b.getNama()).append("\n");
            sb.append(String.format("  %d x %s", b.getJumlah(), formatRupiah(b.getHarga())));
            String subtotal = formatRupiah(b.getSubtotal());
            sb.append(String.format("%" + Math.max(1, 40 - 2
                    - String.valueOf(b.getJumlah()).length() - 3
                    - formatRupiah(b.getHarga()).length()) + "s", subtotal));
            sb.append("\n");
        }

        sb.append(garisPutus);
        sb.append(String.format("%-14s %25s\n", "TOTAL", formatRupiah(hitungTotal())));
        sb.append(String.format("%-14s %25s\n", "BAYAR", formatRupiah(uangBayar)));
        sb.append(String.format("%-14s %25s\n", "KEMBALI", formatRupiah(hitungKembalian(uangBayar))));
        sb.append(garis);
        sb.append("      Terima kasih atas kunjungan Anda\n");
        sb.append(garis);
        return sb.toString();
    }
}
