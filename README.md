# Weather Texbot
Get weather across the world without using phone data

**Implementation**
* The development of this application was done entirely in Java using Gradle
* Twilio used to receive and send SMS
* https://openweathermap.org/ used to get weather data
* JSON-simple used to parse weather data
* Spark framework used to handle REST endpoint
* shadowJar used to build gradle project by combining dependency classes and resources with a project's into a single output Jar
* Heroku used to host application on remote server

<p align="center"> 
  <img src="https://user-images.githubusercontent.com/46686623/77495403-d2a00c80-6e1e-11ea-9c83-db1cbf1ed448.png" width=25%/> 
  <img src="https://user-images.githubusercontent.com/46686623/77495411-d6339380-6e1e-11ea-8514-a9b8d84d0dca.png" width=25%/>
</p>

## Quick test

Open an issue and we'll get it worked out since Twilio requires phone number verification if a new number wants to use the application.

## To test on personal computer

Follow the Twilio quickstart guide found [here](https://www.twilio.com/docs/sms/quickstart/java) to get phone number and set up development environment. 
  
Get API key for https://openweathermap.org/. 

Download Heroku CLI [here](https://devcenter.heroku.com/articles/getting-started-with-java#set-up)

Set environment variables for TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN & WEATHERAPI_KEY.

Clone project to personal computer.

Call `gradle shadowjar` in root directory to build project.

Create Heroku app by calling `Heroku create` in root directory of project.

Commit to heroku by calling `git push heroku`

See how to set Twilio webhook url to Heroku app [here](https://www.twilio.com/docs/sms/tutorials/how-to-receive-and-reply-python)

Text your number with a city name!

## Resources

Twilio: https://www.twilio.com/docs</br>
Spark documentation: http://sparkjava.com/documentation#getting-started</br>
Open weather map API: https://openweathermap.org/</br>
Heroku: https://devcenter.heroku.com/</br>
ShadowJar: https://github.com/johnrengelman/shadow


