package com.softwareproject.travelguideapp.util

class DataSingleton private constructor() {

    companion object {
        private var instance: DataSingleton? = null

        fun createInstance(): DataSingleton {
            if (instance == null)
                instance = DataSingleton()

            return instance!!
        }
    }
    private val questionList = listOf<String>(
        "Çocukken en sevdiğiniz sanatçı",
        "İlk evcil hayvanınızın adı",
        "En yakın arkadaşınız",
        "İlk gittiğiniz konser",
        "İlkokul öğretmeninizin adı"
    )

    private val backendQuestionList = listOf<String>(
        "Cocukken en sevdiginiz sanatci",
        "Ilk evcil hayvaninizin adi",
        "En yakin arkadasiniz",
        "Ilk gittiginiz konser",
        "Ilkokul ogretmeninizin adi"
    )

    fun getQuestionList(): List<String>{
        return questionList
    }

    fun getBackendQuestionItem(index : Int ) : String{
        return backendQuestionList[index]
    }
}