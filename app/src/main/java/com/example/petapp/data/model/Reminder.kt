package com.example.petapp.data.model

data class Reminder(
    val id: Int,
    val title: String = "Hello",
    val petName: String,
    val time: String,
    val date: String,
    val repeat: String,
    val status: Boolean = true,
    val type: String
) {
    override fun toString(): String {
        return "Reminder(id=$id, title='$title', petName='$petName', time='$time', date='$date')"
    }
    fun fomartDate(): String {
        if(type == "daily"){
            return "$time - Daily"
        }
        return "$time - $date"
    }
    fun  namePet(): String{
        return "Pet: $petName"
    }
}