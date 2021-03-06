package com.alab.expenseTracker.service;

import com.alab.expenseTracker.entities.Expense;
import com.alab.expenseTracker.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service

public class ExpenseService {

    private final String ALL = "all";

    @Autowired
    private ExpenseRepository expenseDao;
    SimpleDateFormat sdfOutput = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
    SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public Expense findById(int id) {
        return expenseDao.findOne(id);
    }
    public List<Expense> findAllExpenses() {
        return sort(expenseDao.findAll());
    }
    public void deleteExpense(int id) {expenseDao.delete(id);};

    public void saveExpense(Expense expense) throws ParseException {
        Date date = sdfInput.parse(expense.getDate());
        expense.setDate(sdfOutput.format(date));
        expenseDao.save(expense);
    }

    public List<Expense> getAllExpensesByChosenDate(String dateFrom,
                                                    String dateTo,
                                                    String type) throws ParseException {
        List<Expense> listByChosenDate = new LinkedList<Expense>();
        Date dateF = sdfInput.parse(dateFrom);
        Date dateT = sdfInput.parse(dateTo);

        for (Expense expense : getListByType(type)) {
            Date dateOfExpense = sdfOutput.parse(expense.getDate());
            if (dateOfExpense.after(dateF) && dateOfExpense.before(dateT)) {
                listByChosenDate.add(expense);
            }
        }

        return sort(listByChosenDate);

    }
    public List sort(List list) {
        Collections.sort(list, new Comparator<Expense>() {
            @Override
            public int compare(Expense r1, Expense r2) {
                try {
                    return sdfOutput.parse(r1.getDate()).compareTo(sdfOutput.parse(r2.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return list;
    }
    private List<Expense> getListByType(String type) {
        if (type.equals(ALL)) {
            return expenseDao.findAll();
        }
        List<Expense> listByType = new LinkedList<Expense>();
        for (Expense expense : expenseDao.findAll()) {
            if (expense.getType().equals(type)) {
                listByType.add(expense);
            }
        }
        return listByType;
    }

    public int getTotalCost(List<Expense> allExpensesByChosenDate) {
        int totalCost = 0;
        for (Expense expense : allExpensesByChosenDate) {
            totalCost += expense.getCost();
        }
        return totalCost;
    }
}
