package com.company.controller;

import com.company.container.ComponentContainer;
import com.company.entity.Currency;
import com.company.util.InlineKeyboardUtil;
import com.company.util.ReplyKeyboardUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.io.*;
import java.net.URL;
import java.util.*;


public class MainController {

//    private static double fromVal;
//    private static double toVal;
//    private static String from;
//    private static String to;

    public static void handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        User user = message.getFrom();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (text.equals("/start")) {

            sendMessage.setText("Welcome  " + user.getFirstName()+ " 👏👏👏");
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getMainMenuKeyboard());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals("Convert currency")) {
            sendMessage.setText("From Currency...");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCurrenciesMarkup());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            ComponentContainer.adminStatus.put(chatId, "from");
        } else if (text.equals("Show currency")) {

            //Currencies in pdf file

            File pdfFile=currenciesPDF();
            InputFile file=new InputFile(pdfFile);

            SendDocument document=new SendDocument();
            document.setChatId(chatId);
            document.setDocument(file);
            ComponentContainer.MY_BOT.sendMsg(document);
        } else if (ComponentContainer.adminStatus.get(chatId).equals("to")) {
            try {
                double value= Double.parseDouble(text);
//                double convertedValue=(value*fromVal)/toVal;
//                sendMessage.setText(value+" "+from+" = "+Math.ceil(convertedValue)+" "+to);
                String[] strings = ComponentContainer.convertFromTo.get(chatId).split("/");
                double from = Double.parseDouble(strings[1]);
                double to = Double.parseDouble(strings[3]);
                double convertedValue=(value*from)/to;
                sendMessage.setText(text+" "+strings[0]+" = "+convertedValue+" "+strings[2]);

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.adminStatus.remove(chatId);
            }catch (NumberFormatException e){
                sendMessage.setText("Wrong format entered, please enter numeric value: ");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }
    }

    public static void handleCallback(Message message, String data) {
        Long chatId = message.getChatId();
        if (ComponentContainer.adminStatus.get(chatId).equals("from")) {
//            String[] split = data.split("/");
//            from = split[0];
//            fromVal = Double.parseDouble(split[1]);
            ComponentContainer.convertFromTo.put(chatId, data+"/");
            ComponentContainer.adminStatus.remove(chatId);
            ComponentContainer.adminStatus.put(chatId, "to");
            EditMessageText editMessageText=new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(message.getMessageId());
            editMessageText.setText("To Currency...");
            editMessageText.setReplyMarkup((InlineKeyboardMarkup) InlineKeyboardUtil.getCurrenciesMarkup());
//            SendMessage sendMessage=new SendMessage(String.valueOf(chatId), "To Currency...");
//            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCurrenciesMarkup());
//            ComponentContainer.MY_BOT.sendMsg(sendMessage);

            ComponentContainer.MY_BOT.sendMsg(editMessageText);
        }else if(ComponentContainer.adminStatus.get(chatId).equals("to")){

//            String[] split = data.split("/");
//            to = split[0];
//            toVal = Double.parseDouble(split[1]);

            ComponentContainer.convertFromTo.put(chatId,
                    ComponentContainer.convertFromTo.get(chatId)+data);
            EditMessageText editMessageText=new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(message.getMessageId());
            editMessageText.setText("Continue...");
//            editMessageText.setDisableWebPagePreview(true);
            ComponentContainer.MY_BOT.sendMsg(editMessageText);
            SendMessage sendMessage1=new SendMessage(String.valueOf(chatId), "Enter value: ");
            ComponentContainer.MY_BOT.sendMsg(sendMessage1);
        }

    }

    public static List<Currency> getCurrencies() {
        ArrayList<Currency> currencies = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            Currency[] currencies1 = mapper.readValue(new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json"), Currency[].class);
            currencies = new ArrayList<>(Arrays.asList(currencies1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currencies;
    }

    private static File currenciesPDF(){
        List<Currency> currencies=getCurrencies();
        currencies.sort(Comparator.comparingInt(Currency::getId));

        File file=new File("src/main/resources/currencies.pdf");
        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdfDocument=new PdfDocument(writer);
             Document document=new Document(pdfDocument)) {

             Paragraph paragraph=new Paragraph("Currencies");
             paragraph.setTextAlignment(TextAlignment.CENTER);
             paragraph.setBold();

//             float[] size={150, 150, 150};
             Table table=new Table(6);
             table.addCell("Id");
             table.addCell("Currency Uzb");
             table.addCell("Currency Ccy");
             table.addCell("Difference");
             table.addCell("Currency Rate");
             table.addCell("Locale date");


            for (Currency currency : currencies) {
                table.addCell(String.valueOf(currency.getId()));
                table.addCell(currency.getCcyNmUZ());
                table.addCell(currency.getCcy());
                table.addCell(currency.getDiff());
                table.addCell(currency.getRate());
                table.addCell(currency.getDate());
            }
            document.add(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
