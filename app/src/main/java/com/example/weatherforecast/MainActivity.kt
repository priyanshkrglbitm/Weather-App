package com.example.weatherforecast

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.weatherforecast.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Delhi")
        searchCity()
    }

    private fun searchCity(){
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }



    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "0776135f81c839ed012f3ed9ecf455f2", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val seaLevel = responseBody.main.sea_level
                    val feelsLike = responseBody.main.feels_like
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main?:"Unknown"

                    val sunriseFormatted = formatTime(responseBody.sys.sunrise.toLong())
                    val sunsetFormatted = formatTime(responseBody.sys.sunset.toLong())


                    binding.temp.text = "$temperature °C"
                    binding.weather.text= condition
                    binding.maxTemp.text = "Max Temp : $maxTemp °C"
                    binding.minTemp.text = "Min Temp : $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.sunrise.text = "$sunriseFormatted"
                    binding.sunset.text = "$sunsetFormatted"
                    binding.seaLevel.text = "$seaLevel hPa"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.feelsLike.text = "$feelsLike °C"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text ="$cityName"
                    changeImageAccordingToWeatherCondition(condition)



                } else {
                    Log.e("TAG", "Response was not successful or body was null")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("TAG", "Error fetching weather data", t)
            }
        })
    }



    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp * 1000) // Convert seconds to milliseconds
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }



    fun dayName(timeStamp:Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format((Date()))
    }

//    private fun isNightTime(): Boolean {
//        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
//        return currentHour < 6 || currentHour >= 18
//    }

    private fun changeImageAccordingToWeatherCondition(condition: String) {
//        val isNight = isNightTime()
        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }
            "Mist", "Haze", "Partly Clouds", "Overcast", "Foggy", "Clouds" -> {
                    binding.root.setBackgroundResource(R.drawable.colud_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                }
            "Light Rain", "Drizzle", "Showers", "Moderate Rain", "Heavy Rain","Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.lottieAnimationView.setAnimation(R.raw.snow)
                }
        }
        binding.lottieAnimationView.playAnimation()
    }

}
