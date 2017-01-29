package any.audio.Models;


import java.util.ArrayList;

public class ExploreItemModel {

    public String sectionTitle;
    ArrayList<ItemModel> list;

    public ExploreItemModel(String sectionTitle) {

        this.sectionTitle = sectionTitle;

    }

    public ExploreItemModel(String sectionTitle, ArrayList<ItemModel> list) {
        this.sectionTitle = sectionTitle;
        this.list = list;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public ArrayList<ItemModel> getList() {
        return list;
    }

    public void setList(ArrayList<ItemModel> list) {
        this.list = list;
    }
}
