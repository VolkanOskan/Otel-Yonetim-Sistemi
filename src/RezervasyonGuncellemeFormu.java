import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class RezervasyonGuncellemeFormu extends JFrame {
    private JComboBox<String> musteriComboBox;
    private JTextField odaNoField, baslangicTarihiField, bitisTarihiField, fiyatField;
    private JButton guncelleButton;

    public RezervasyonGuncellemeFormu() {
        setTitle("Rezervasyon Güncelle");
        setLayout(new GridLayout(7, 2));

        musteriComboBox = new JComboBox<>();
        odaNoField = new JTextField();
        baslangicTarihiField = new JTextField();
        bitisTarihiField = new JTextField();
        fiyatField = new JTextField();
        fiyatField.setEditable(false);

        add(new JLabel("Müşteri Adı:"));
        add(musteriComboBox);

        add(new JLabel("Oda No:"));
        add(odaNoField);

        add(new JLabel("Başlangıç Tarihi (Yıl-Ay-Gün):"));
        add(baslangicTarihiField);

        add(new JLabel("Bitiş Tarihi (Yıl-Ay-Gün):"));
        add(bitisTarihiField);

        add(new JLabel("Fiyat:"));
        add(fiyatField);

        guncelleButton = new JButton("Güncelle");
        add(guncelleButton);

        guncelleButton.addActionListener(e -> {
            try {
                String musteriAdiSoyadi = (String) musteriComboBox.getSelectedItem();
                if (musteriAdiSoyadi == null || musteriAdiSoyadi.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Lütfen müşteri adı seçin!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] adSoyad = musteriAdiSoyadi.split(" ");
                String musteriAd = adSoyad[0];
                String musteriSoyad = adSoyad[1];
                int odaNo = Integer.parseInt(odaNoField.getText().trim());
                String baslangicTarihi = baslangicTarihiField.getText().trim();
                String bitisTarihi = bitisTarihiField.getText().trim();

                if (baslangicTarihi.isEmpty() || bitisTarihi.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Tarih alanları boş bırakılamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                try {
                    LocalDate.parse(baslangicTarihi, formatter);
                    LocalDate.parse(bitisTarihi, formatter);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, "Tarih formatı geçersiz. Lütfen Yıl-Ay-Gün formatında girin!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection con = DBConnection.getConnection()) {
                    
                    String checkMusteriSql = "SELECT id FROM Musteriler WHERE ad = ? AND soyad = ?";
                    PreparedStatement checkMusteriPs = con.prepareStatement(checkMusteriSql);
                    checkMusteriPs.setString(1, musteriAd);
                    checkMusteriPs.setString(2, musteriSoyad);
                    ResultSet rsMusteri = checkMusteriPs.executeQuery();
                    if (!rsMusteri.next()) {
                        JOptionPane.showMessageDialog(null, "Geçersiz müşteri adı veya soyadı girdiniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int musteriId = rsMusteri.getInt("id");

                    
                    double fiyat = hesaplaFiyat(baslangicTarihi, bitisTarihi, odaNo);
                    fiyatField.setText(String.valueOf(fiyat));

                    
                    String sql = "UPDATE Rezervasyonlar SET oda_no = ?, baslangic_tarihi = ?, bitis_tarihi = ?, fiyat = ? WHERE musteri_id = ? AND baslangic_tarihi = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, odaNo);
                    ps.setString(2, baslangicTarihi);
                    ps.setString(3, bitisTarihi);
                    ps.setDouble(4, fiyat);
                    ps.setInt(5, musteriId);
                    ps.setString(6, baslangicTarihi);
                    ps.executeUpdate();

                    
                    String odaDurumGuncelleSql = "UPDATE Odalar SET durum = ? WHERE oda_no = ?";
                    PreparedStatement odaDurumPs = con.prepareStatement(odaDurumGuncelleSql);
                    String durum = "Dolu (" + baslangicTarihi + " - " + bitisTarihi + ")";
                    odaDurumPs.setString(1, durum);
                    odaDurumPs.setInt(2, odaNo);
                    odaDurumPs.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Rezervasyon başarıyla güncellendi!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Geçersiz sayısal değer girdiniz!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        
        yukleMusteriAdlari();

        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void yukleMusteriAdlari() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT DISTINCT m.ad, m.soyad " +
                         "FROM Musteriler m " +
                         "JOIN Rezervasyonlar r ON m.id = r.musteri_id";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String musteriAdi = rs.getString("ad");
                String musteriSoyadi = rs.getString("soyad");

                if (musteriAdi != null && musteriSoyadi != null) {
                    String musteriAdiSoyadi = musteriAdi + " " + musteriSoyadi;
                    musteriComboBox.addItem(musteriAdiSoyadi);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private double hesaplaFiyat(String baslangicTarihi, String bitisTarihi, int odaNo) {
        LocalDate baslangic = LocalDate.parse(baslangicTarihi);
        LocalDate bitis = LocalDate.parse(bitisTarihi);
        long gunSayisi = ChronoUnit.DAYS.between(baslangic, bitis);

        double birimFiyat = 0.0;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT tip FROM Odalar WHERE oda_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, odaNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String odaTipi = rs.getString("tip");
                switch (odaTipi) {
                    case "Tek Kişilik":
                        birimFiyat = 3000.0;
                        break;
                    case "Çift Kişilik":
                        birimFiyat = 5800.0;
                        break;
                    case "Suit":
                        birimFiyat = 8000.0;
                        break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return gunSayisi * birimFiyat;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RezervasyonGuncellemeFormu().setVisible(true);
            }
        });
    }
}
