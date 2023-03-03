package net.cwjn.idf.compat.patchouli;

public class PageObject {

    private String name;
    private String icon = "";
    private String category;
    private Page[] pages;

    public PageObject(String n, String c, String e) {
        name = n;
        icon = "";
        category = c;
        pages = new Page[]{new Page1(e), new Page2(e)};
    }

    interface Page {}

    private static class Page1 implements Page {
        public String type;
        public String entity;
        public String text;
        public Page1(String s) {
            type = "patchouli:entity";
            entity = s;
            text = "";
        }
    }

    private static class Page2 implements Page {
        public String type;
        public String entity;
        public Page2(String s) {
            type = "idf:entity";
            entity = s;
        }
    }

}
