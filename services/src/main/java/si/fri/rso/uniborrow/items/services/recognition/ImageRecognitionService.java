package si.fri.rso.uniborrow.items.services.recognition;

import org.json.JSONArray;
import org.json.JSONObject;
import si.fri.rso.uniborrow.items.services.beans.ItemBean;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import java.time.temporal.ChronoUnit;

import java.util.logging.Logger;

@ApplicationScoped
public class ImageRecognitionService {

    private static final Logger LOG = Logger.getLogger(ItemBean.class
            .getSimpleName());

    @CircuitBreaker
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getTagsFallback")
    public List<String> getTags(String imgUrl) {
        try {
            String credentialsToEncode = "acc_8f2f7d39a41f16f" + ":" + "14f65a65fa9bdf6e7db47cfeaa8652f0";
            String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

            String endpoint_url = "https://api.imagga.com/v2/tags";
            String image_url = imgUrl;

            String url = endpoint_url + "?image_url=" + image_url;
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

            connection.setRequestProperty("Authorization", "Basic " + basicAuth);

            int responseCode = connection.getResponseCode();

            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String jsonResponse = connectionInput.readLine();

            connectionInput.close();

            List<String> listOfTags = new ArrayList();

            JSONObject obj = new JSONObject(jsonResponse);
            JSONArray tags = obj.getJSONObject("result").getJSONArray("tags");
            for(int i = 0; i < 4; i++) {
                listOfTags.add(tags.getJSONObject(i).getJSONObject("tag").getString("en"));
            }
            return listOfTags;

        }
        catch(Exception e) {
            LOG.severe(e.getMessage());
        }
        return null;
    }

    public List<String> getTagsFallback(String imgUrl) {
        LOG.info("Imagga API not available.");
        return null;
    }

}
