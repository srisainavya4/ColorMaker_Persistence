package com.example.colormaker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


class MyPreferencesRepository private constructor(private val dataStore: DataStore<Preferences>){

    private val RCLR= intPreferencesKey("rclr")
    private val GCLR= intPreferencesKey("gclr")
    private val BCLR= intPreferencesKey("bclr")
    val rclr:Flow<Int> = this.dataStore.data.map { prefs -> prefs[RCLR]?:0 }.distinctUntilChanged()
    val gclr:Flow<Int> = this.dataStore.data.map { prefs -> prefs[GCLR]?:0 }.distinctUntilChanged()
    val bclr:Flow<Int> = this.dataStore.data.map { prefs -> prefs[BCLR]?:0 }.distinctUntilChanged()
    private val RSW= booleanPreferencesKey("rsw")
    private val GSW= booleanPreferencesKey("gsw")
    private val BSW= booleanPreferencesKey("bsw")
    val rsw:Flow<Boolean> = this.dataStore.data.map { prefs -> prefs[RSW]?:false }.distinctUntilChanged()
    val gsw:Flow<Boolean> = this.dataStore.data.map { prefs -> prefs[GSW]?:false }.distinctUntilChanged()
    val bsw:Flow<Boolean> = this.dataStore.data.map { prefs -> prefs[BSW]?:false }.distinctUntilChanged()

    private suspend fun saveInt(key:Preferences.Key<Int>,value:Int){
        this.dataStore.edit{
            prefs-> prefs[key]=value
        }
    }
    private suspend fun saveBool(key:Preferences.Key<Boolean>,value:Boolean){
        this.dataStore.edit{
                prefs-> prefs[key]=value
        }
    }

    suspend fun saveClr(value: Int,clr:String){
        val key: Preferences.Key<Int> = when(clr){
            "red"->RCLR
            "green" ->GCLR
            "blue" ->BCLR
            else ->{
                throw NoSuchFieldException("Invalid Input")
            }
        }
        this.saveInt(key,value)
    }
    suspend fun saveState(value: Boolean,clr:String){
        val key: Preferences.Key<Boolean> = when(clr){
            "red"->RSW
            "green" ->GSW
            "blue" ->BSW
            else ->{
                throw NoSuchFieldException("Invalid Input")
            }
        }
        this.saveBool(key,value)
    }

    companion object{
        private const val PREFERENCES_DATA_FILE_NAME="settings"
        private var INSTANCE: MyPreferencesRepository? = null
        fun initialize(context: Context){
            val dataStore= PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile((PREFERENCES_DATA_FILE_NAME))
            }
            INSTANCE= MyPreferencesRepository(dataStore)
        }

        fun get():MyPreferencesRepository{
            return INSTANCE?: throw IllegalStateException("MyPreferenceRepository has not yet been Initialized()'ed")
        }
    }



}
