package test.java;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

import com.utils.DbUtil;

public class TokenGeneratorMain {

    private static String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static void main(String[] args) {
        int days = 7;
        if (args != null && args.length > 0) {
            try {
                days = Integer.parseInt(args[0]);
            } catch (Exception ignored) {
            }
        }

        String token = generateToken();
        LocalDateTime expiration = LocalDateTime.now().plusDays(days);

        String sql = "INSERT INTO api_token(token, date_expiration) VALUES (?, ?)";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, token);
            ps.setTimestamp(2, Timestamp.valueOf(expiration));
            ps.executeUpdate();

            System.out.println("TOKEN=" + token);
            System.out.println("EXPIRATION=" + expiration);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
