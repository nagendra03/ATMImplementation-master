package com.progressoft.induction.atm.Impl;

import com.progressoft.induction.atm.BankingSystem;
import com.progressoft.induction.atm.Banknote;
import com.progressoft.induction.atm.exceptions.AccountNotFoundException;
import com.progressoft.induction.atm.exceptions.InsufficientFundsException;
import com.progressoft.induction.atm.exceptions.NotEnoughMoneyInATMException;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class BankingSystemImpl implements BankingSystem {
   Map<String, BigDecimal> accountBalanceMap = new HashMap<String, BigDecimal>();
   EnumMap<Banknote,Integer> atmCashMap = new EnumMap<>(Banknote.class);

    public BankingSystemImpl() {
        atmCashMap.put(Banknote.FIFTY_JOD,10);
        atmCashMap.put(Banknote.TWENTY_JOD,20);
        atmCashMap.put(Banknote.TEN_JOD,100);
        atmCashMap.put(Banknote.FIVE_JOD,100);

        accountBalanceMap.put("123456789", BigDecimal.valueOf(1000.0));
        accountBalanceMap.put("111111111", BigDecimal.valueOf(1000.0));
        accountBalanceMap.put("222222222", BigDecimal.valueOf(1000.0));
        accountBalanceMap.put("333333333", BigDecimal.valueOf(1000.0));
        accountBalanceMap.put("444444444", BigDecimal.valueOf(1000.0));
    }

    public BigDecimal sumOfMoneyInAtm(){
       BigDecimal sum = atmCashMap.entrySet()
                .stream()
                .map(entry -> entry.getKey().getValue().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum;
    }


    @Override
    public BigDecimal getAccountBalance(String accountNumber){
        if (!accountBalanceMap.containsKey(accountNumber)) {
            throw new AccountNotFoundException(accountNumber+" Not found in Bank.");
        }
        return accountBalanceMap.get(accountNumber);
    }

    @Override
    public void debitAccount(String accountNumber, BigDecimal amount) {
        BigDecimal currentValue = getAccountBalance(accountNumber);
        if (amount.compareTo(currentValue) > 0) {
            throw new InsufficientFundsException("Amount exceeds current balance in ATM! ");
        }
        accountBalanceMap.put(accountNumber, currentValue.subtract(amount));
    }

    public void substractFromNotesMap(Banknote banknote, Integer count){
        atmCashMap.put(banknote, atmCashMap.get(banknote) - count);
    }

    public Integer getAtmNoteCount(Banknote banknote) {
        return atmCashMap.getOrDefault(banknote, 0);
    }
}
