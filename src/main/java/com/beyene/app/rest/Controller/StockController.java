package com.beyene.app.rest.Controller;

import com.beyene.app.rest.Model.Stock;
import com.beyene.app.rest.Repository.StockRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockRepository stockRepository;
    private String stocks;
    private Map stockMap;


    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
    @GetMapping(value = "/")
    public String welcomePage() throws URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
       this.stocks = "Welcome";
        try{
            URL u = new URL("https://obscure-oasis-94568.herokuapp.com/etfs/etf");
            HttpURLConnection hr = (HttpURLConnection) u.openConnection();
            if(hr.getResponseCode()==200){
                InputStream im= hr.getInputStream();
                BufferedReader br= new BufferedReader(new InputStreamReader(im));
                this.stocks = br.readLine();
                Map<String,Object> map = mapper.readValue(this.stocks, Map.class);
                this.stockMap = map;
//                System.out.println(map);
            }
        } catch (Exception e){
            System.out.println(e);
        }

        return this.stocks;
    }


    @GetMapping(value = "/users")
    public ResponseEntity getAllStocks(){

        return ResponseEntity.ok(this.stockRepository.findAll());
    }

    @PostMapping("/save")
    public ResponseEntity saveStock(@RequestBody Stock stock){
    return ResponseEntity.ok(this.stockRepository.save(stock));
    }
    @GetMapping ("mapper")
    public Map getStockMap(){

        return (Map) this.stockMap.get("0");
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateStock(@PathVariable long id, @RequestBody Stock stock){
        Stock updatedStock = this.stockRepository.findById(id).get();
        updatedStock.setName(stock.getName());
        updatedStock.setSymbol(stock.getSymbol());
        updatedStock.setPrice(stock.getPrice());
        return ResponseEntity.ok(this.stockRepository.save(updatedStock));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteStock(@PathVariable long id){
        Stock deletedStock = this.stockRepository.findById(id).get();
        this.stockRepository.delete(deletedStock);
        return ResponseEntity.ok(deletedStock);
    }
}
