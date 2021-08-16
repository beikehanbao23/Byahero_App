package UI

interface LoadingScreen {
    fun showLoading()
    fun finishLoading()
    fun makeLoading(attributesVisibility:Boolean,progressBarVisibility:Int)

}