import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OdaEklemeOdaTipiBelirleme {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            
            String deleteSql = "DELETE FROM Odalar";
            PreparedStatement deletePs = con.prepareStatement(deleteSql);
            deletePs.executeUpdate();

            
            for (int odaNo = 1; odaNo <= 30; odaNo++) {
                int tipId;
                if (odaNo <= 10) {
                    tipId = getTipId("Tek Kişilik");
                } else if (odaNo <= 20) {
                    tipId = getTipId("Çift Kişilik");
                } else {
                    tipId = getTipId("Suit");
                }
                
                String sql = "INSERT INTO Odalar (oda_no, tip_id, durum) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, odaNo);
                ps.setInt(2, tipId);
                ps.setString(3, "Boş");
                ps.executeUpdate();
            }
            System.out.println("Odalar başarıyla eklendi ve oda tipleri belirlendi.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static int getTipId(String tip) {
        int tipId = -1;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT id FROM OdaTipleri WHERE tip = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, tip);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tipId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipId;
    }
}
