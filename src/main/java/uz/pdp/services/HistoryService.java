package uz.pdp.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import uz.pdp.model.History;
import uz.pdp.repository.BaseService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author Sanjarbek Allayev, чт 16:07. 01.01.2022
 */

public class HistoryService implements BaseService<History, List<History>,String> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void write(List<History> historyList) {
        try {
            File file = new File("historyList.json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, historyList);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Writing error " + e.toString());
        }
    }

    @Override
    public List<History> read() {
        List<History> list = new ArrayList<>();
        try {
            list = objectMapper.readValue(new File("historyList.json"),
                    new TypeReference<ArrayList<History>>() {
                    });
        }catch (Exception | NoClassDefFoundError e){
            e.printStackTrace();
        }
        return  list;
    }

    @Override
    public String add(History history) {
        List<History> historyList = read();
        history.setOverall((history.getProduct().getPrice()).add(BigDecimal.valueOf((history.getProduct().getAmount()))));
        historyList.add(history);
        write(historyList);
        return "SUCCESS";
    }

    @Override
    public History getById(UUID id) {
        List<History> historyList = read();
        Iterator<History> iterator = historyList.iterator();
        while(iterator.hasNext()) {
            History history = iterator.next();
            if(history.getCustomer().getId().equals(id))
                return history;
        }
        return null;
    }

    public List<History> getHistoryById(UUID id) {
        List<History> myHistory = new ArrayList<>();
        for (History history : read()) {
            if (history.getCustomer().getId().equals(id))
                myHistory.add(history);
        }
        return myHistory;
    }

    @SneakyThrows
    public void getAllOrderExcelFile() {
        List<History> historyList = read();
        XWPFDocument document = new XWPFDocument();
        boolean b = false;
        for(History history:historyList) {

            XWPFParagraph paragraph = document.createParagraph();
            b = true;
            XWPFRun run = paragraph.createRun();
            String string = "Customer name: '"+history.getCustomer().getFullName() + "' == " + history.getCustomer().getPhoneNumber() + " Date: " + history.getDate() + " Total: " + history.getOverall() + " so'm";
            run.setText(string);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(new File("orders.docx"));

        document.write(fileOutputStream);
        fileOutputStream.close();

    }

    public BigDecimal getTotalPrice(UUID id) {
        List<History> historyList = read();
        BigDecimal totalPrice = BigDecimal.valueOf(0);
        for (History history: getHistoryById(id)){
                totalPrice=totalPrice.add(history.getProduct().getPrice().multiply(BigDecimal.valueOf(history.getProduct().getAmount())));
            }

        return totalPrice;
    }
    @Override
    public List<History> getAll() {
        List<History> historyList = read();
        return historyList;
    }

    @Override
    public String check(String   t) {
        return null;
    }
}
