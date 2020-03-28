
import java.io.*;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.DomElement;

public class WebScraper {
    static String path = "/Users/jiyuqi/Documents/GitHub/TownshipTaleWikiWebScraper/TownshipTaleWikiWebScraper/scraper/src/main";

    static String getPath() {
        return path;
    }
    // Download images using Jsoup
    private static void downloadImage(String strImageURL){
        //get file name from image path
        String strImageName =
                strImageURL.substring(strImageURL.lastIndexOf(":") + 1 );

//        System.out.println("Saving: " + strImageName + ", from: " + strImageURL);

        try {

            //open the stream from URL
            URL urlImage = new URL(strImageURL);
            InputStream in = urlImage.openStream();

            byte[] buffer = new byte[4096];
            int n = -1;

            // change the absolute path to your current path
            OutputStream os =
                    new FileOutputStream( getPath() + "/images" + "/" + strImageName );

            //write bytes to the output stream
            while ( (n = in.read(buffer)) != -1 ){
                os.write(buffer, 0, n);
            }
            //close the stream
            os.close();
//            System.out.println("Image saved");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void JsonToCSV(String jsonString) throws IOException {
        JsonNode jsonTree = new ObjectMapper().readTree(jsonString);
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.writerFor(JsonNode.class)
                .with(csvSchema)
                .writeValue(new File(getPath() +"/resources/result.csv"), jsonTree);
    }
    public static void main(String[] args) throws IOException {

        String baseUrl = "https://townshiptale.gamepedia.com/" ;
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        ArrayList<String> itemNames= new ArrayList<String>();
        String json = "[";
        try {
            String categoryUrl = baseUrl + "Category:Items";
            HtmlPage page = client.getPage(categoryUrl);

            List<HtmlElement> items =  page.getByXPath("//div[@class='mw-category-group']") ;
            if(items.isEmpty()){
                System.out.println("No items found !");
            }else{
                for(HtmlElement htmlItem : items) {
                    List<HtmlElement> groupItems = htmlItem.getByXPath(".//ul/li");
                    for (HtmlElement groupItem : groupItems) {
                        HtmlAnchor itemAnchor = ((HtmlAnchor) groupItem.getFirstByXPath(".//a"));
                        String itemName = itemAnchor.getHrefAttribute();
                        itemNames.add(itemName);
                    }
                }
                System.out.println(itemNames);
                for (String name : itemNames) {
                    Item item = new Item();
                    name = name.substring(1);
                    item.setName(name);

                    String itemUrl = baseUrl + name;
                    HtmlPage itemPage = client.getPage(itemUrl);


                    HtmlElement contentHtml = itemPage.getHtmlElementById("mw-content-text");
                    DomElement domContent = itemPage.getElementById("mw-content-text");
                    HtmlElement description = contentHtml.getFirstByXPath(".//div[@class='mw-parser-output']/p[1]");
                    item.setDescription(description.getVisibleText());

                    HtmlElement imageTable = contentHtml.getFirstByXPath(".//div[@class='mw-parser-output']/table[1]");
                    if (imageTable == null) {
                        item.setImage("None");
                        ObjectMapper mapper = new ObjectMapper();
                        String jsonString = mapper.writeValueAsString(item);
                        json += jsonString;
                        json += ",";
                        continue;
                    }
                    HtmlAnchor image = imageTable.getFirstByXPath(".//tbody/tr[2]/th/a");
                    if (image.getHrefAttribute().equals( "/0.0.9.0")) {
//                        System.out.println(image);
                        item.setImage("None");
                        ObjectMapper mapper = new ObjectMapper();
                        String jsonString = mapper.writeValueAsString(item);
                        json += jsonString;
                        json += ",";
                        continue;
                    }
                    String imageUrl = image.getHrefAttribute();

                    downloadImage(baseUrl + imageUrl.substring(1));
                    item.setImage(getPath() + "/images" + imageUrl.substring( imageUrl.lastIndexOf(":") + 1 ));
//                    System.out.println(item.getImage());
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = mapper.writeValueAsString(item);
                    json += jsonString;
                    json += ",";
//                    System.out.println(jsonString);
                }

            }
        } catch(Exception e){
            e.printStackTrace();
        }
        json = json.substring(0, json.length()-1);
        json += "]";
        System.out.println(json);
        JsonToCSV(json);
    }

}