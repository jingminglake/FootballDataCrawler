package com.yaxionh.parser;

import com.yaxionh.common.Constants;
import com.yaxionh.common.HttpHelper;
import com.yaxionh.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DongQiuDiParser extends Parser {

    private String _keyWord = "";

    public static String getDownloadUrl(String newUrl)
    {
        System.out.println("******************");
        String downloadUrl = null;

        String html = HttpHelper.getHtmlFromDQDUrl(newUrl);
        //System.out.println(html);

        Document doc = Jsoup.parse(html);
        Elements divs = doc.select("div.area-download");

        //System.out.println("divs_size:" + divs.size());

        //System.out.println(divs.first().select("a").first().attr("data_url"));

        downloadUrl = divs.first().select("a").first().attr("data_url");

        return downloadUrl;
    }

    private List<String> getNewsLink(String url, String keyWord) {
        List<String> res = new ArrayList<>();
        String html = HttpHelper.getHtmlFromDQDUrl(url);

        Document doc = Jsoup.parse(html);
        Element newsList = doc.getElementById("news_list");

        Elements lis =  newsList.getElementsByTag("h2");
        System.out.println("size:" + lis.size());

        for (Element li : lis)
        {
            Elements newsLink = li.select("a[href]");

            String newsTitle = newsLink.text();
            if (newsTitle.contains(keyWord)) { // if contains keyword, get link
                System.out.println(newsTitle);
                String link = newsLink.attr("href");
                res.add(link);
            }
//            if (title.contains(keyWord))
//            {
//                //System.out.println(li.select("div.icon").first().select("a").first().attr("href"));
//                String href = li.select("div.icon").first().select("a").first().attr("href");
//                String newUrl = "http://shouji.baidu.com/" + href;
//                System.out.println(newUrl);
//                //System.out.println(li.select("a.app_name").attr("href"));
//                //Elements app_as = li.select("div.icon");
//                //System.out.println("app_as_size:" + app_as.size());
//
//                String downloadUrl = getDownloadUrl(newUrl);
//
//                title = title.replace("/", "_");
//                FileOperation.downloadFromUrl(Common.getBaiDuMarketName(),
//                        keyWord, title + "_" + num + ".apk", downloadUrl);
//                ++num;
//            }
        }
        return res;
    }

    @Override
    public void parserHtml(String keyWord) {
        _keyWord = keyWord;
        int num = 0;
        String url = null;
//        try {
//            url = Constants.testUrl + URLEncoder.encode(keyWord, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        List<String> allLinks = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            url = Constants.newsListUrlBase + i;
            System.out.println(url);
            allLinks.addAll(getNewsLink(url, keyWord));
        }
        //System.out.println("---------all Links---------");
        List<String> allPictureLinks = getAllPictureLinks(allLinks);
        downloadPictures(allPictureLinks);
    }

    private List<String> getAllPictureLinks(List<String> allLinks) {
        List<String> res = new ArrayList<>();
        for (String link : allLinks) {
            String html = HttpHelper.getHtmlFromDQDUrl(link);
            res.add(getMainPictureLinkFromNewsHtml(html));
        }
        return res;
    }

    private String getMainPictureLinkFromNewsHtml(String html) {
        Document doc = Jsoup.parse(html);
        Element detail = doc.select("div.detail").first();
        Element pic =  detail.getElementsByTag("img").first();
        String res  = pic.attr("src");
        return res;
    }

    private void downloadPictures(List<String> allPicLinks) {
        for (int i = 0; i < allPicLinks.size(); i++) {
            try {
                HttpHelper.download(allPicLinks.get(i), _keyWord + i + ".jpg", "./output/");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
