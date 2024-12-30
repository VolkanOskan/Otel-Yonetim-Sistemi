import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Fiyatlandirma {

    public static double hesaplaFiyat(int odaNo, String baslangicTarihi, String bitisTarihi) {
        double fiyat = 0.0;
        int gunSayisi = hesaplaGunSayisi(baslangicTarihi, bitisTarihi);
        
        try (Connection con = DBConnection.getConnection()) {
            
            String sql = "SELECT tip_id, durum FROM Odalar WHERE oda_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, odaNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String durum = rs.getString("durum");
                if ("Boş".equals(durum)) {
                    int tipId = rs.getInt("tip_id");
                    
                    String fiyatSql = "SELECT fiyat FROM OdaTipleri WHERE id = ?";
                    PreparedStatement fiyatPs = con.prepareStatement(fiyatSql);
                    fiyatPs.setInt(1, tipId);
                    ResultSet fiyatRs = fiyatPs.executeQuery();

                    if (fiyatRs.next() && gunSayisi>=0) {
                        double odaFiyat = fiyatRs.getDouble("fiyat");
                        fiyat = odaFiyat * gunSayisi;
                    }
                }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fiyat;
    }

    private static int hesaplaGunSayisi(String baslangicTarihi, String bitisTarihi) {
        LocalDate baslangic = LocalDate.parse(baslangicTarihi);
        LocalDate bitis = LocalDate.parse(bitisTarihi);
        return (int) ChronoUnit.DAYS.between(baslangic, bitis);
    }
}
