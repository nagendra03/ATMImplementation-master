package com.progressoft.induction.atm.Impl;

import com.progressoft.induction.atm.ATM;
import com.progressoft.induction.atm.Banknote;
import com.progressoft.induction.atm.exceptions.AccountNotFoundException;
import com.progressoft.induction.atm.exceptions.InsufficientFundsException;
import com.progressoft.induction.atm.exceptions.NotEnoughMoneyInATMException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.progressoft.induction.atm.Banknote.*;

public class ATMImpl implements ATM {
    private final BankingSystemImpl bankingSystem = new BankingSystemImpl();

    @Override
    public List<Banknote> withdraw(String accountNumber, BigDecimal amount) {
        BigDecimal currentBalance = checkBalance(accountNumber);
        if (amount.compareTo(currentBalance) > 0) {
            throw new InsufficientFundsException("Abort! Insufficient Balance in User Account");
        }
        BigDecimal moneyInAtm = bankingSystem.sumOfMoneyInAtm();
        if (amount.compareTo(moneyInAtm) > 0) {
            throw new NotEnoughMoneyInATMException("Abort! Insufficient balance in ATM");
        }
        List<Banknote> result;
        try {
            result = getBestArrangementOfMoney(amount);
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
            result = getArrangementOfMoney(amount);
        }
        bankingSystem.debitAccount(accountNumber, amount);
        return result;
    }

    private List<Banknote> getBestArrangementOfMoney(BigDecimal amount) {
        List<Banknote> notes = new ArrayList<>();//175 2*50 2*20 4*10 3*5
        // 3*50 1* 20 1*5
        boolean flag = true;
        int fiftyCount = 0;
        int twentyCount = 0;
        int tenCount = 0;
        int fiveCount = 0;
        int currentFiftyAtmCount = bankingSystem.getAtmNoteCount(FIFTY_JOD);
        int currentTwentyAtmCount = bankingSystem.getAtmNoteCount(TWENTY_JOD);
        int currentTenAtmCount = bankingSystem.getAtmNoteCount(TEN_JOD);
        int currentFiveAtmCount = bankingSystem.getAtmNoteCount(FIVE_JOD);
        while (amount.doubleValue() > 0 && flag) {
            flag = false;
            if (amount.compareTo(FIFTY_JOD.getValue()) >= 0 && currentFiftyAtmCount > 0) {
               amount =  amount.subtract(FIFTY_JOD.getValue());
                fiftyCount++;
                flag = true;
                currentFiftyAtmCount--;
                notes.add(FIFTY_JOD);
            }
            if (amount.compareTo(Banknote.TWENTY_JOD.getValue()) >= 0 && currentTwentyAtmCount > 0) {
                amount = amount.subtract(Banknote.TWENTY_JOD.getValue());
                twentyCount++;
                flag = true;
                currentTwentyAtmCount--;
                notes.add(Banknote.TWENTY_JOD);
            }
            if (amount.compareTo(TEN_JOD.getValue()) >= 0 && currentTenAtmCount > 0) {
                amount = amount.subtract(TEN_JOD.getValue());
                tenCount++;
                flag = true;
                currentTenAtmCount--;
                notes.add(TEN_JOD);
            }
            if (amount.compareTo(Banknote.FIVE_JOD.getValue()) >= 0 && currentFiveAtmCount > 0) {
                amount = amount.subtract(Banknote.FIVE_JOD.getValue());
                fiveCount++;
                flag = true;
                currentFiveAtmCount--;
                notes.add(Banknote.FIVE_JOD);
            }
        }
        if (amount.doubleValue() > 0) { //2*50 1*5
            throw new RuntimeException("Notes combination does not match");
        }
        bankingSystem.substractFromNotesMap(FIFTY_JOD, fiftyCount);
        bankingSystem.substractFromNotesMap(FIFTY_JOD, twentyCount);
        bankingSystem.substractFromNotesMap(FIFTY_JOD, tenCount);
        bankingSystem.substractFromNotesMap(FIFTY_JOD, fiveCount);
        return notes;
    }

    private List<Banknote> getArrangementOfMoney(BigDecimal amount) {
        List<Banknote> result = new ArrayList<>();
        List<Object> remAmtAndQty = getAmount(amount, FIFTY_JOD);
        amount = (BigDecimal) remAmtAndQty.get(0);
        int fiftyCount = (Integer) remAmtAndQty.get(1);
        int twentyCount = 0;
        int tenCount = 0;
        int fiveCount = 0;


        if (amount.doubleValue() > 0) {
            remAmtAndQty = getAmount(amount, TWENTY_JOD);
            amount = (BigDecimal) remAmtAndQty.get(0);
            twentyCount = (Integer) remAmtAndQty.get(1);
            ;
        }

        if (amount.doubleValue() > 0) {
            remAmtAndQty = getAmount(amount, TEN_JOD);
            amount = (BigDecimal) remAmtAndQty.get(0);
            tenCount = (Integer) remAmtAndQty.get(1);
        }

        if (amount.doubleValue() > 0) {
            remAmtAndQty = getAmount(amount, FIVE_JOD);
            amount = (BigDecimal) remAmtAndQty.get(0);
            fiveCount = (Integer) remAmtAndQty.get(1);
        }
        bankingSystem.substractFromNotesMap(FIFTY_JOD, fiftyCount);
        bankingSystem.substractFromNotesMap(TWENTY_JOD, twentyCount);
        bankingSystem.substractFromNotesMap(TEN_JOD, tenCount);
        bankingSystem.substractFromNotesMap(FIVE_JOD, fiveCount);
        while (fiftyCount > 0) {
            result.add(FIFTY_JOD);
            fiftyCount--;
        }
        while (twentyCount > 0) {
            result.add(TWENTY_JOD);
            twentyCount--;
        }
        while (tenCount > 0) {
            result.add(TEN_JOD);
            tenCount--;
        }
        while (fiveCount > 0) {
            result.add(FIVE_JOD);
            fiveCount--;
        }

        return result;

    }

    private List<Object> getAmount(BigDecimal amount, Banknote banknote) {
        BigDecimal ans = amount.divideToIntegralValue(banknote.getValue());
        Integer fiftyCount = Math.min(ans.intValue(), bankingSystem.getAtmNoteCount(banknote));
        amount = banknote.getValue().multiply(BigDecimal.valueOf(fiftyCount));
        List<Object> list = new ArrayList<>();
        list.add(amount);
        list.add(fiftyCount);
        return list;
    }

    @Override
    public BigDecimal checkBalance(String accountNumber) {
        return bankingSystem.getAccountBalance(accountNumber);
    }
}
