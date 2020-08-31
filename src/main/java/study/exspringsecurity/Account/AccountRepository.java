package study.exspringsecurity.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Repository
public class AccountRepository  {

    // Mapper를 찾아보자
    private Map<String, Account> accounts = new HashMap<>();
//    private Random random = new Random();

    public Account save(Account account) {
        accounts.put(account.getEmail(), account);
        return account;
    }

    public Account findByEmail(String username) {
        return accounts.get(username);
    }
}
