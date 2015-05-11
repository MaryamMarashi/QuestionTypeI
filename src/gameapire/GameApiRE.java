/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameapire;

/**
 *
 * @author MaryamMarashi
 */
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author MaryamMarashi
 */
public class GameApiRE {

    public String getRandomList(List<String> list) {
        Random rand = new Random();
        int index = rand.nextInt(list.size());
        return list.get(index);

    }

    public static void main(String[] args) throws IOException, ParseException {
        try {

            String apiKey = "AIzaSyACuJhaLWUWxKBXeNu5oTJtsXcHaS1b_W0";
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            String query = "[{\"limit\": 100,\"mid\":null,\"name\":null,\"type\":\"/visual_art/artwork\"}]";

            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread?query=" + URLEncoder.encode(query, "UTF-8"));
            url.put("query", query);
            url.put("key", apiKey);
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());

            JSONArray results = (JSONArray) response.get("result");

            ArrayList<String> midList = new ArrayList<String>();
            ArrayList<String> nameList = new ArrayList<String>();
            for (Object result : results) {
                midList.add(JsonPath.read(result, "$.mid").toString());
                nameList.add(JsonPath.read(result, "$.name").toString());

                //System.out.println(nameList);
            }
            Random rand = new Random();
            int index = rand.nextInt(midList.size());
            String mid = midList.get(index);
            String name = nameList.get(index);

            System.out.println("Who is the painter of " + name + " ?");

            String topicId = mid;
            GenericUrl url2 = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + topicId);
            url2.put("key", apiKey);

            HttpRequest request1 = requestFactory.buildGetRequest(url2);
            HttpResponse httpResponse1 = request1.execute();
            JSONObject topic = (JSONObject) parser.parse(httpResponse1.parseAsString());
            String topic1 = JsonPath.read(topic, "$.property['/visual_art/artwork/artist'].values[0].text").toString();
            System.out.println("Correct Answer: " + topic1);

            String artistQuery = "[{\"limit\": 100,\"name\":null,\"type\":\"/visual_art/visual_artist\"}]";

            GenericUrl artistUrl = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread?query=" + URLEncoder.encode(artistQuery, "UTF-8"));
            artistUrl.put("query", artistQuery);
            artistUrl.put("key", apiKey);
            //System.out.println(artistUrl);
            HttpRequest request2 = requestFactory.buildGetRequest(artistUrl);
            HttpResponse httpResponse2 = request2.execute();

            JSONObject artist = (JSONObject) parser.parse(httpResponse2.parseAsString());
            //System.out.println(artist);
            JSONArray artists = (JSONArray) artist.get("result");

            ArrayList<String> artistList = new ArrayList<String>();
            GameApiRE mql = new GameApiRE();
            for (Object result : artists) {
                artistList.add(JsonPath.read(result, "$.name").toString());
                artistList.remove(topic1);

            }
            for (int i = 0; i < 3; i++) {
                System.out.println("Other Answers: " + mql.getRandomList(artistList));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
