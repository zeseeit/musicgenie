package any.audio.Models;

/**
 * Created by Ankit on 2/22/2017.
 */

public class NavItem {

    private static String TRENDING_TEXT = "\uE80E";
    private static String SEARCH_TEXT = "\uE8B6";
    private static String DOWNLOADS_TEXT = "\uE884";
    private static String ABOUT_TEXT = "\uE88E";
    private static String SETTINGS_TEXT = "\uE8B8";
    private static String UPDATES_TEXT = "\uE153";

    public static String[] icons = {TRENDING_TEXT,SEARCH_TEXT,DOWNLOADS_TEXT,SETTINGS_TEXT,ABOUT_TEXT,UPDATES_TEXT};
    public static String[] titles = {"Trending","My Search","Downloads","Settings","About Us","Updates"};
    public String icon;
    public String title;
    public boolean isSelected;

    public NavItem(String icon, String title, boolean isSelected) {
        this.icon = icon;
        this.title = title;
        this.isSelected = isSelected;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
