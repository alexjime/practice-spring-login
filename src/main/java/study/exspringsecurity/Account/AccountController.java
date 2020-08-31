package study.exspringsecurity.Account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
//    private final AccountRepository accountRepository;

    @GetMapping("/create")
    public Account create() {
        Account account = new Account();
        account.setEmail("pugki380@gmail.com");
        account.setPassword("password");

        return accountService.save(account);
    }
}
