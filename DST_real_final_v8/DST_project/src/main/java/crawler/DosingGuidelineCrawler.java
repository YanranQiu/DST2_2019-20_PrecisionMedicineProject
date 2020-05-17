package crawler;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class DosingGuidelineCrawler extends BaseCrawler {

    private static final Logger log = LoggerFactory.getLogger(DosingGuidelineCrawler.class);

    public static final String URL_BASE = "https://api.pharmgkb.org/v1/data%s";
    public static final String URL_GUIDELINES = "https://api.pharmgkb.org/v1/site/guidelinesByDrugs";
    private Path path = new File("dosingGuideline.data").toPath();

    public void doCrawlerDosingGuidelineList() {
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = this.getURLContent(URL_GUIDELINES);
        Gson gson = new Gson();
        Map drugLabels = gson.fromJson(content, Map.class);
        List<Map> data = (List<Map>) drugLabels.get("data");
        data.stream().forEach(x -> {
            log.info("{}", x);
            List.of("cpic", "cpnds", "dpwg", "fda", "pro").forEach(source -> {
                List<Map> guidelineList = (List<Map>) x.get(source);
                guidelineList.forEach(guideline -> {
                    String url = (String) guideline.get("url");
                    doCrawlerDosingGuideline(url);
                });
            });
        });
    }

    public void doCrawlerDosingGuideline(String url) {
        String content = this.getURLContent(String.format(URL_BASE, url));
        try {
            Files.writeString(path, content, StandardOpenOption.APPEND);
            Files.writeString(path, "\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
