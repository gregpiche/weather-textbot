import com.twilio.Twilio;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import static spark.Spark.port;
import static spark.Spark.post;


public class WeatherApp {

    // Find your Account Sid and Token at twilio.com/user/account
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    public static final String WEATHER_KEY = System.getenv("WEATHERAPI_KEY");

    public static void main(String[] args)
    {
        init();
    }

    public static void init() {
        port(getHerokuAssignedPort());
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        post("/sms", (req, res) -> {
            String city = req.queryParams("Body");
            String contents = getUrlContents(city);
            res.type("application/xml");
            Body body = new Body
                    .Builder(contents)
                    .build();
            Message sms = new Message
                    .Builder()
                    .body(body)
                    .build();
            MessagingResponse twiml = new MessagingResponse
                    .Builder()
                    .message(sms)
                    .build();
            return twiml.toXml();
        });
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    public static String getUrlContents(String city)
    {
        //inline will store the JSON data streamed in string format
        String inline = "";

        try {
            // Pass the desired url as an object
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + WEATHER_KEY + "&units=metric");

            // Type cast url to HttpURLConnection Object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set the request type
            conn.setRequestMethod("GET");

            // Open connection to stream to corresponding API
            conn.connect();

            // Get response code
            int responsecode = conn.getResponseCode();
            System.out.println("Response code is: " + responsecode);

            if (responsecode != 200){
                //throw new RuntimeException("HttpResponseCode: " + responsecode);
                return inline = "We couldn't find that city, make sure you enter a valid city!";
            }else{
                //Scanner functionality will read the JSON data from the stream
                Scanner sc = new Scanner(url.openStream());
                while(sc.hasNext())
                {
                    inline += sc.nextLine();
                }
                //System.out.println("\nJSON Response in String format");
                //System.out.println(inline);

                //Close the stream when reading the data has been finished
                sc.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return getFormat(inline);
    }

    private static String getFormat(String weather){
        String msg = new String();

        try {
            //JSONParser reads the data from string object and break each data into key value pairs
            JSONParser parse = new JSONParser();
            //Type caste the parsed json data in json object
            JSONObject jobj = (JSONObject) parse.parse(weather);
            //Store the JSON object in JSON array as objects (For level 1 array element i.e main)
            //Get object for main
            JSONObject main = (JSONObject) jobj.get("main");

            //Get data from JSON main object
            int temp;
            int feels_like;
            int temp_min;
            int temp_max;
            try {
                temp = (int) Math.round((double) main.get("temp"));
            }
            catch (ClassCastException e){
                temp = Math.round((long) main.get("temp"));
            }
            try {
                feels_like = (int) Math.round((double) main.get("feels_like"));
            }
            catch (ClassCastException e){
                feels_like = Math.round((long) main.get("feels_like"));
            }
            try {
                temp_min = (int) Math.round((double)main.get("temp_min"));
            }
            catch (ClassCastException e){
                temp_min = Math.round((long) main.get("temp_min"));
            }
            try {
                temp_max = (int) Math.round((double) main.get("temp_max"));
            }
            catch (ClassCastException e){
                temp_max = Math.round((long) main.get("temp_max"));
            }
            long humidity = (long) main.get("humidity");

            //Get data for wind
            JSONObject wind = (JSONObject) jobj.get("wind");
            double windSpeed;
            try {
                windSpeed = (double) wind.get("speed");
            }
            catch (ClassCastException e){
                windSpeed = (long) wind.get("speed");
            }

            //Get data for clouds
            JSONObject clouds = (JSONObject) jobj.get("clouds");
            long cloudsAll = (long) clouds.get("all");

            //Get data for sys
            JSONObject sys = (JSONObject) jobj.get("sys");
            long sunrise = (long) sys.get("sunrise");
            long sunset = (long) sys.get("sunset");
            String countryCode = (String) sys.get("country");

            //Get information from JSON object
            int timezone = Math.toIntExact((long)jobj.get("timezone"));
            String name = (String) jobj.get("name");


            // Convert from UNIX, utc to human readable time and data*/
            java.util.Date sunriseTime = new java.util.Date(sunrise *1000);
            java.util.Date sunsetTime = new java.util.Date(sunset *1000);

            // Set timezone offset
            sunriseTime.setTime(sunriseTime.getTime() + timezone*1000);
            sunsetTime.setTime(sunsetTime.getTime() + timezone*1000);

            // Format date to remove timezone
            SimpleDateFormat ft= new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
            String noTimeZoneRise = ft.format(sunriseTime);
            String noTimeZoneSet = ft.format(sunsetTime);

            //SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*Calendar rise = Calendar.getInstance();
            rise.setTime(sunriseTime);
            rise.add(Calendar.SECOND, -timezone);

            Calendar set = Calendar.getInstance();
            set.setTime(sunsetTime);
            rise.add(Calendar.SECOND, -timezone);
            // String rise = isoFormat.format(sunriseTime);
            // String set = isoFormat.format(sunsetTime);
            // isoFormat.setTimeZone(TimeZone.getTimeZone(zone));*/

            //Formatted data
            msg =   "Weather info for " + name + ", " + countryCode + " is:\n" +
                    "Current temperature: " + temp + "\u00B0" + "C\n" +
                    "Feels like: " + feels_like + "\u00B0" + "C\n" +
                    "Min temp: " + temp_min + "\u00B0" + "C\n" +
                    "Max temp: " + temp_max + "\u00B0" + "C\n" +
                    "Humidity: " + humidity + "%\n" +
                    "Wind Speed: " + windSpeed + "km/h\n" +
                    "Cloud coverage: " + cloudsAll + "%\n" +
                    "Sunrise: " + noTimeZoneRise + "\n" +
                    "Sunset: " + noTimeZoneSet;
            System.out.println(msg);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return msg;
    }
}
