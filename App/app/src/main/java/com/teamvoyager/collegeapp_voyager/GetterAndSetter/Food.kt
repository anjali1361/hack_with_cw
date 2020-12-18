package com.teamvoyager.collegeapp_voyager.GetterAndSetter

import kotlin.properties.Delegates

 class Food {
     var id:Int
     var category:String
    var image_url:String


    constructor(category:String,image_url:String,id:Int){
        this.image_url = image_url
        this.category = category
        this.id= id
    }
}