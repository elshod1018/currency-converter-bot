package com.company.util;

import com.company.controller.MainController;
import com.company.entity.Currency;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardUtil {
    public static ReplyKeyboard getCurrenciesMarkup() {

        List<Currency> currencies = MainController.getCurrencies();

        List<InlineKeyboardButton> rows=new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList=new ArrayList<>();
        InlineKeyboardButton buttonUZS=new InlineKeyboardButton();
        buttonUZS.setText("UZS");
        buttonUZS.setCallbackData("UZS/1");
        rows.add(buttonUZS);

        for (int i = 0; i < currencies.size(); i++) {
            InlineKeyboardButton button=new InlineKeyboardButton();
            button.setText(currencies.get(i).getCcy());
            button.setCallbackData(currencies.get(i).getCcy()+"/"+currencies.get(i).getRate());
            rows.add(button);
            if (i%5==0){
                rowList.add(rows);
                rows=new ArrayList<>();
            }
        }
        return new InlineKeyboardMarkup(rowList);
    }
}
