import com.twilio.Twilio;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class WeatherApp {

    // Find your Account Sid and Token at twilio.com/user/account
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    public static final String WEATHER_KEY = System.getenv("WEATHERAPI_KEY");

    public static void main(String[] args)
    {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String city = "Montreal";
        String contents = getUrlContents(city);
        //String output = getFormat(contents);
        //System.out.println(output);
    }

    private static String getUrlContents(String city)
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

            if (responsecode != 200)
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            else{
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
            double temp = Math.round((double) main.get("temp"));
            int feels_like = (int) Math.round((double) main.get("feels_like"));
            int temp_min = Math.round((long)main.get("temp_min"));
            int temp_max = (int) Math.round((double) main.get("temp_max"));
            long humidity = (long) main.get("humidity");

            //Get data for wind
            JSONObject wind = (JSONObject) jobj.get("wind");
            double windSpeed = (double) wind.get("speed");

            //Get data for clouds
            JSONObject clouds = (JSONObject) jobj.get("clouds");
            long cloudsAll = (long) clouds.get("all");

            //Get data for sys
            JSONObject sys = (JSONObject) jobj.get("sys");
            long sunrise = (long) sys.get("sunrise");
            long sunset = (long) sys.get("sunset");
            String countryCode = (String) sys.get("country");

            //Get information from JSON object
            long timezone = (long) jobj.get("timezone");
            String name = (String) jobj.get("name");

            //Convert from UNIX, utc to human readable time and data
            java.util.Date sunriseTime = new java.util.Date(sunrise *1000);
            java.util.Date sunsetTime = new java.util.Date(sunset *1000);

            //Formatted data
            msg =   "Weather info for " + name + ", " + countryCode + " is:\n" +
                    "Current temperature: " + temp + "\u00B0" + "C\n" +
                    "Feels like: " + feels_like + "\u00B0" + "C\n" +
                    "Min temp: " + temp_min + "\u00B0" + "C\n" +
                    "Max temp: " + temp_max + "\u00B0" + "C\n" +
                    "Humidity: " + humidity + "%\n" +
                    "Wind Speed: " + windSpeed + "km/h\n" +
                    "Cloud coverage: " + cloudsAll + "%\n" +
                    "Sunrise: " + sunriseTime + "\n" +
                    "Sunset: " + sunsetTime;
            System.out.println(msg);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return msg;
    }
}
