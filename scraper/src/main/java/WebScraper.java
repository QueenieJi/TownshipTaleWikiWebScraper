
import java.io.*;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebScraper {

    public static void main(String[] args) throws IOException {

        String baseUrl = "https://townshiptale.gamepedia.com/" ;
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        ItemList itemsLists = new ItemList();
        LinkScraper linkScraper = new LinkScraper();
        CSVConverter csvConverter = new CSVConverter();

        try {
            String categoryUrl = baseUrl + "Category:Items";
            HtmlPage page = client.getPage(categoryUrl);

            List<HtmlElement> items =  page.getByXPath("//div[@class='mw-category-group']") ;
            if(items.isEmpty()){
                System.out.println("No items found !");
            }else{
                // read items into itemLists
                itemsLists.addItemNames(items);
                // scrap each item
                linkScraper.scrapItemList(itemsLists.getItemNames(), baseUrl, client);
                // convert to csv
                csvConverter.writeCSVFile(linkScraper.getItems());
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}