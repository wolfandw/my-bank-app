package io.github.wolfandw.frontui.service.impl;

import io.github.wolfandw.frontui.dto.AccountDto;
import io.github.wolfandw.frontui.dto.AccountPageDto;
import io.github.wolfandw.frontui.dto.CashAction;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Заглушка.
 * <p>
 * Только для тестовых целей, чтобы ознакомиться с работой фронта
 */
@Deprecated
@Service
public class AccountStub {
    private String name = "Иванов Иван";
    private LocalDate birthdate = LocalDate.of(2001, 1, 1);
    private int sum = 100;

    private final List<AccountDto> accounts = List.of(
            new AccountDto("petrov", "Петров Петр"),
            new AccountDto("sidorov", "Сидоров Сидор")
    );

    public String getByLogin(String login) {
        return accounts.stream()
                .filter(account -> account.login().equals(login))
                .map(AccountDto::name)
                .findFirst()
                .get();
    }

    public void setNameAndBirthdate(String name, LocalDate birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }

    public void editCash(Model model, int value, CashAction action) {
        if (action == CashAction.GET && sum < value) {
            fillModel(model, List.of("Недостаточно средств на счету"), null);
        } else {
            sum = action == CashAction.GET ? sum - value : sum + value;
            fillModel(model, null, action == CashAction.GET ? "Снято %d руб".formatted(value) : "Положено %d руб".formatted(value));
        }
    }

    public String transfer(Model model, @RequestParam("value") int value, @RequestParam("login") String login) {
        if (sum < value) {
            fillModel(model, List.of("Недостаточно средств на счету"), null);
        } else {
            sum = sum - value;
            fillModel(model, null, "Успешно переведено %d руб клиенту %s".formatted(value, getByLogin(login)));
        }

        return "main";
    }

    public void fillModel(Model model, @Nullable List<String> errors, @Nullable String info) {
        model.addAttribute("name", name);
        model.addAttribute("birthdate", birthdate.format(DateTimeFormatter.ISO_DATE));
        model.addAttribute("sum", sum);
        model.addAttribute("accounts", accounts);
        model.addAttribute("errors", errors);
        model.addAttribute("info", info);
    }

    public AccountPageDto fillModel(List<String> errors, @Nullable String info) {
        AccountPageDto accountPageDto = new AccountPageDto();
        accountPageDto.setName(name);
        accountPageDto.setBirthDate(birthdate.format(DateTimeFormatter.ISO_DATE));
        accountPageDto.setSum(sum);
        accountPageDto.setAccounts(accounts);
        accountPageDto.setErrors(errors);
        accountPageDto.setInfo(info);
        return accountPageDto;
    }

    public AccountPageDto editAccount(String name, LocalDate birthdate) {
        this.name = name;
        this.birthdate = birthdate;
        return fillModel(null, null);
    }

    public AccountPageDto editCash(int value, CashAction action) {
        if (action == CashAction.GET && sum < value) {
            return fillModel(List.of("Недостаточно средств на счету"), null);
        } else {
            sum = action == CashAction.GET ? sum - value : sum + value;
            return fillModel(null, action == CashAction.GET ? "Снято %d руб".formatted(value) : "Положено %d руб".formatted(value));
        }
    }

    public AccountPageDto transfer(@RequestParam("value") int value, @RequestParam("login") String login) {
        if (sum < value) {
            return fillModel(List.of("Недостаточно средств на счету"), null);
        } else {
            sum = sum - value;
            return fillModel(null, "Успешно переведено %d руб клиенту %s".formatted(value, getByLogin(login)));
        }
    }
}
