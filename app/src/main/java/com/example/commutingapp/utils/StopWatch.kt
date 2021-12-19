
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopWatch  {
    private val timeRunInSeconds = MutableLiveData<Long>()


    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
    }
    fun getTimeCommuteMillis():MutableLiveData<Long> = timeRunInMillis
    fun getTimeCommuteInSeconds():MutableLiveData<Long> = timeRunInSeconds

    fun postInitialValues(){
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }
    private var isTimerEnabled = false
    private var timeCommute:Long = 0L
    private var lapTime:Long = 0L
    private var timeStarted:Long = 0L
    private var lastSecondTimestamp:Long = 0L


     fun pause() {
        isTimerEnabled = false
    }

     fun stop() {
         isTimerEnabled = false
         postInitialValues()
    }

     fun start() {
        initializeTimer()
        CoroutineScope(Dispatchers.Main).launch{
            isTimerEnabled = true
            runTimer()
        }
    }
    private suspend fun runTimer(){

        while(isTimerEnabled) {
            createLapTime()
            increaseMillis()
            if (secondHasElapsed()) {
                increaseSeconds()
                increaseTimeStamp()
            }
            delay(50L)
        }
        increaseTimeCommute()
    }
    private fun initializeTimer(){
        isTimerEnabled = true
        timeStarted = System.currentTimeMillis()
    }
    private fun increaseTimeCommute(){
        timeCommute += lapTime
    }
    private fun createLapTime(){
        lapTime = System.currentTimeMillis() - timeStarted
    }
    private fun increaseMillis(){
        timeRunInMillis.postValue(timeCommute + lapTime)
    }
    private fun increaseSeconds(){
        timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
    }
    private fun increaseTimeStamp(){
        lastSecondTimestamp += 1000L
    }
    private fun secondHasElapsed():Boolean{ return timeRunInMillis.value!! >= lastSecondTimestamp + 1000L}
}