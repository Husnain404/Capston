package com.capstone.token;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    @Query(value = """
      select t from Token t inner join User u
      on t.user.id = u.id
      where u.id = :id and (t.expired = false or t.revoked = false)
      """)
    List<Token> findAllValidTokenByUser(Long id);

    Optional<Token> findByToken(String token);


    @Modifying
    @Query("DELETE FROM Token t WHERE t.expired = true OR t.revoked = true ")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
