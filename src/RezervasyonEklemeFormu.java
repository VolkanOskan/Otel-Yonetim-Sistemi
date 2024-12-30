import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RezervasyonEklemeFormu extends JFrame {
    private JComboBox<String> musteriAdComboBox;
    private JTextField odaNoField, baslangicTarihiField, bitisTarihiField, fiyatField;
    private JButton ekleButton, fiyatHesaplaButton;

    public RezervasyonEklemeFormu() {
        setTitle("Rezervasyon Ekle");
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Müşteri Adı:"));
        musteriAdComboBox = new JComboBox<>();
        add(musteriAdComboBox);

        add(new JLabel("Oda No (1-30):"));
        odaNoField = new JTextField();
        add(odaNoField);

        add(new JLabel("Başlangıç Tarihi (Yıl-Ay-Gün):"));
        baslangicTarihiField = new JTextField();
        add(baslangicTarihiField);

        add(new JLabel("Bitiş Tarihi (Yıl-Ay-Gün):"));
        bitisTarihiField = new JTextField();
        add(bitisTarihiField);

        add(new JLabel("Fiyat (TL):"));
        fiyatField = new JTextField();
        fiyatField.setEditable(false);
        add(fiyatField);

        fiyatHesaplaButton = new JButton("Fiyat Hesapla");
        add(fiyatHesaplaButton);

        fiyatHesaplaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String odaNoText = odaNoField.getText().trim();
                    if (!odaNoText.isEmpty()) {
                        int odaNo = Integer.parseInt(odaNoText);
                        if (odaNo < 1 || odaNo > 30) {
                            JOptionPane.showMessageDialog(null, "Oda bulunamadı.Lütfen geçerli bir değer giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        String baslangicTarihi = baslangicTarihiField.getText().trim();
                        String bitisTarihi = bitisTarihiField.getText().trim();

                        
                        LocalDate baslangic = LocalDate.parse(baslangicTarihi);
                        LocalDate bitis = LocalDate.parse(bitisTarihi);
                        LocalDate bugun = LocalDate.now();

                        if (baslangic.isBefore(bugun) || bitis.isBefore(bugun)) {
                            JOptionPane.showMessageDialog(null, "Tarihler bugünden önce olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (odaNoText.isEmpty() || baslangicTarihi.isEmpty() || bitisTarihi.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                        } else {
                            double fiyat = Fiyatlandirma.hesaplaFiyat(odaNo, baslangicTarihi, bitisTarihi);
                            fiyatField.setText(String.valueOf(fiyat));
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,"Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Geçersiz oda numarası!", "Hata", JOptionPane.ERROR_MESSAGE);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, "Geçersiz tarih formatı! Lütfen '(Yıl-Ay-Gün)' formatını kullanın.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ekleButton = new JButton("Ekle");
        add(ekleButton);

        ekleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String musteriAd = (String) musteriAdComboBox.getSelectedItem();
                    String odaNoText = odaNoField.getText().trim();
                    if (!odaNoText.isEmpty()) {
                        int odaNo = Integer.parseInt(odaNoText);
                        if (odaNo < 1 || odaNo > 30) {
                            JOptionPane.showMessageDialog(null, "Oda bulunamadı.Lütfen geçerli bir değer giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        String baslangicTarihi = baslangicTarihiField.getText().trim();
                        String bitisTarihi = bitisTarihiField.getText().trim();

                        
                        LocalDate baslangic = LocalDate.parse(baslangicTarihi);
                        LocalDate bitis = LocalDate.parse(bitisTarihi);
                        LocalDate bugun = LocalDate.now();

                        if (baslangic.isBefore(bugun) || bitis.isBefore(bugun)) {
                            JOptionPane.showMessageDialog(null, "Tarihler bugünden önce olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (bitis.isBefore(baslangic)) {
                        	JOptionPane.showMessageDialog(null, "Bitiş tarihi , baslangıç tarihinden önce olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                     
                        String fiyat = fiyatField.getText().trim();
                        if (musteriAd.isEmpty() || odaNoText.isEmpty() || baslangicTarihi.isEmpty() || bitisTarihi.isEmpty() || fiyat.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                        } else {
                            int musteriId = getMusteriIdByAd(musteriAd);
                            try (Connection con = DBConnection.getConnection()) {
                                String checkSql = "SELECT * FROM Rezervasyonlar WHERE oda_no = ? AND ((baslangic_tarihi BETWEEN ? AND ?) OR (bitis_tarihi BETWEEN ? AND ?) OR (? BETWEEN baslangic_tarihi AND bitis_tarihi) OR (? BETWEEN baslangic_tarihi AND bitis_tarihi))";
                                PreparedStatement checkPs = con.prepareStatement(checkSql);
                                checkPs.setInt(1, odaNo);
                                checkPs.setString(2, baslangicTarihi);
                                checkPs.setString(3, bitisTarihi);
                                checkPs.setString(4, baslangicTarihi);
                                checkPs.setString(5, bitisTarihi);
                                checkPs.setString(6, baslangicTarihi);
                                checkPs.setString(7, bitisTarihi);
                                ResultSet rs = checkPs.executeQuery();

                                if (rs.next()) {
                                    JOptionPane.showMessageDialog(null, "Bu tarihlerde bu oda zaten dolu.", "Hata", JOptionPane.ERROR_MESSAGE);
                                }
                                else {
                                    
                                    String sql = "INSERT INTO Rezervasyonlar (musteri_id, oda_no, baslangic_tarihi, bitis_tarihi, fiyat) VALUES (?, ?, ?, ?, ?)";
                                    PreparedStatement ps = con.prepareStatement(sql);
                                    ps.setInt(1, musteriId);
                                    ps.setInt(2, odaNo);
                                    ps.setString(3, baslangicTarihi);
                                    ps.setString(4, bitisTarihi);
                                    ps.setDouble(5, Double.parseDouble(fiyat));
                                    ps.executeUpdate();

                                    
                                    String odaDurumGuncelleSql = "UPDATE Odalar SET durum = ? WHERE oda_no = ?";
                                    PreparedStatement odaDurumPs = con.prepareStatement(odaDurumGuncelleSql);
                                    String durum = "Dolu (" + baslangicTarihi + " - " + bitisTarihi + ")";
                                    odaDurumPs.setString(1, durum);
                                    odaDurumPs.setInt(2, odaNo);
                                    odaDurumPs.executeUpdate();

                                    
                                   
                                    JOptionPane.showMessageDialog(null, "Rezervasyon başarıyla eklendi.");
                                    
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Lütfen Oda No alanını doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                    } 
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Geçersiz oda numarası!", "Hata", JOptionPane.ERROR_MESSAGE);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, "Geçersiz tarih formatı! Lütfen '(Yıl-Ay-Gün)' formatını kullanın.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setSize(600, 500); 
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void yukleMusteriAdlari() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT ad, soyad FROM Musteriler";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String musteriAd = rs.getString("ad") + " " + rs.getString("soyad");
                musteriAdComboBox.addItem(musteriAd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getMusteriIdByAd(String musteriAd) {
        int musteriId = -1;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT id FROM Musteriler WHERE CONCAT(ad, ' ', soyad) = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, musteriAd);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                musteriId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return musteriId;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RezervasyonEklemeFormu rezervasyonEklemeFormu = new RezervasyonEklemeFormu();
                rezervasyonEklemeFormu.yukleMusteriAdlari();
                rezervasyonEklemeFormu.setVisible(true);
               
                
            }
        });
    }
}

