import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

import java.util.ArrayList;
import java.util.List;

public class ItemList {
    private ArrayList<String> itemNames= new ArrayList<String>(); ;


    public ArrayList<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(ArrayList<String> itemNames) {
        this.itemNames = itemNames;
    }

    public void addItemNames(List<HtmlElement> items) {
        for(HtmlElement htmlItem : items) {
            List<HtmlElement> groupItems = htmlItem.getByXPath(".//ul/li");
            for (HtmlElement groupItem : groupItems) {
                HtmlAnchor itemAnchor = ((HtmlAnchor) groupItem.getFirstByXPath(".//a"));
                String itemName = itemAnchor.getHrefAttribute();
                itemNames.add(itemName);
            }
        }
    }
}
