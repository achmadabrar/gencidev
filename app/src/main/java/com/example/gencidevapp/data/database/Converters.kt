package com.example.gencidevapp.data.database

import androidx.room.TypeConverter
import com.example.gencidevapp.data.model.Address
import com.example.gencidevapp.data.model.Company
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromAddress(address: Address): String = Gson().toJson(address)

    @TypeConverter
    fun toAddress(addressString: String): Address =
        Gson().fromJson(addressString, Address::class.java)

    @TypeConverter
    fun fromCompany(company: Company): String = Gson().toJson(company)

    @TypeConverter
    fun toCompany(companyString: String): Company =
        Gson().fromJson(companyString, Company::class.java)
}