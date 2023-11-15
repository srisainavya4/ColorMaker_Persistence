package com.example.colormaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MyViewModel: ViewModel() {
    val _bcl= MutableStateFlow<Int>(0)
    val _rcl= MutableStateFlow<Int>(0)
    val _gcl= MutableStateFlow<Int>(0)
    val _rsw= MutableStateFlow<Boolean>(false)
    val _gsw= MutableStateFlow<Boolean>(false)
    val _bsw= MutableStateFlow<Boolean>(false)
    val _allcl= MutableStateFlow<Array<Int>>(Array<Int>(3){0})
    val _allst= MutableStateFlow<Array<Boolean>>(Array<Boolean>(3){false})
    val prefs=MyPreferencesRepository.get()
    var BoxColor:IntArray= IntArray(3)
    var SwitchStates:BooleanArray= BooleanArray(3)

    fun saveColor(value:Int,clr:String){
           viewModelScope.launch {
               prefs.saveClr(value,clr)
           }
    }
    fun saveState(value:Boolean,clr:String){
        viewModelScope.launch {
            prefs.saveState(value,clr)
        }
    }

    fun flowUIValues(){
        viewModelScope.launch {
            combine(prefs.rclr,prefs.gclr,prefs.bclr){
                r,g,b->
                    Triple(r,g,b)
            }.collectLatest {
                _rcl.value=it.first
                _gcl.value=it.second
                _bcl.value=it.third
                _allcl.value= arrayOf(it.first,it.second,it.third)
            }
        }
        viewModelScope.launch {
            combine(prefs.rsw,prefs.gsw,prefs.bsw){
                    r,g,b->
                Triple(r,g,b)
            }.collectLatest {
                _rsw.value=it.first
                _gsw.value=it.second
                _bsw.value=it.third
                _allst.value= arrayOf(it.first,it.second,it.third)
            }
        }

    }



}