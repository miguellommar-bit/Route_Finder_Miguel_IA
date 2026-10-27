package com.miguel.routefinder.repository;

import com.miguel.routefinder.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private final RowMapper<User> mapper = (rs, i) -> {
        User u = new User();
        u.setUserID(rs.getInt("UserID"));
        u.setEmail(rs.getString("Email"));
        u.setHashPassword(rs.getString("HashPassword"));
        u.setRole(rs.getString("Role"));
        u.setName(rs.getString("Name"));
        u.setSurname(rs.getString("Surname"));
        String dob = rs.getString("DateOfBirth");
        u.setDateOfBirth(dob == null ? null : LocalDate.parse(dob));
        return u;
    };

    public boolean insertUser(User u) {
        return jdbc.update("INSERT INTO \"User\" (Email, HashPassword, Role, Name, Surname, DateOfBirth) VALUES (?, ?, ?, ?, ?, ?)",
                u.getEmail(), u.getHashPassword(), u.getRole(), u.getName(), u.getSurname(),
                u.getDateOfBirth() == null ? null : u.getDateOfBirth().toString()) > 0;
    }

    public User getUserByEmail(String email) {
        List<User> r = jdbc.query("SELECT * FROM \"User\" WHERE Email = ?", mapper, email);
        return r.isEmpty() ? null : r.get(0);
    }
}