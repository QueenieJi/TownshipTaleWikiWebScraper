import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

public class LinkScraper {
    private ArrayList<Item> items;
    private String path = "/Users/jiyuqi/Documents/GitHub/TownshipTaleWikiWebScraper/TownshipTaleWikiWebScraper/scraper/src/main";

    public LinkScraper() {
        this.items = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void downloadImage (String strImageURL){
        //get file name from image path
        String strImageName =
                strImageURL.substring(strImageURL.lastIndexOf(":") + 1 );
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scrapDescription (HtmlPage itemPage, Item item) {
        HtmlElement contentHtml = itemPage.getHtmlElementById("mw-content-text");
        HtmlElement description = contentHtml.getFirstByXPath(".//div[@class='mw-parser-output']/p[1]");
        item.setDescription(description.getVisibleText());
    }

    public void scrapImage(HtmlPage itemPage, Item item, String baseUrl) {
        HtmlElement contentHtml = itemPage.getHtmlElementById("mw-content-text");
        HtmlElement imageTable = contentHtml.getFirstByXPath(".//div[@class='mw-parser-output']/table[1]");
        if (imageTable == null) {
            item.setImage("None");
            return;
        }
        HtmlAnchor image = imageTable.getFirstByXPath(".//tbody/tr[2]/th/a");
        if (image.getHrefAttribute().equals( "/0.0.9.0")) {
            item.setImage("None");
            return;
        }
        String imageUrl = image.getHrefAttribute();

        downloadImage(baseUrl + imageUrl.substring(1));
        item.setImage(getPath() + "/images" + imageUrl.substring( imageUrl.lastIndexOf(":") + 1 ));
    }

    public void scrapItemList(ArrayList<String> itemNames, String baseUrl, WebClient client) throws IOException {
        for (String name : itemNames) {
            Item item = new Item();
            name = name.substring(1);
            item.setName(name);

            String itemUrl = baseUrl + name;
            HtmlPage itemPage = client.getPage(itemUrl);

            scrapDescription(itemPage, item);
            scrapImage(itemPage, item, baseUrl);

            this.addItem(item);
        }
    }
}
