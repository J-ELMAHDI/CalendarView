package com.emj.calendarview

import java.util.*

data class CalendarEvent(var dateFrom: GregorianCalendar? = null,var dateTo: GregorianCalendar? = null) {
    fun reorderDate(date: GregorianCalendar) {
        val tempFrom = dateFrom
        val tempTo = dateTo
        if (tempFrom != null && tempTo != null) {
            if(tempFrom<tempTo){
                dateFrom = date
                dateTo = date
            }else{
                dateFrom = getMin(tempFrom,tempTo,date)
                dateTo = getMax(tempFrom,tempTo,date)
            }
        }else{
             dateFrom = date
             dateTo  = date
        }
    }

    private fun getMin(dateF: GregorianCalendar, dateT: GregorianCalendar, dateN: GregorianCalendar): GregorianCalendar {
        var minDate = dateF
        if(minDate > dateT){
            minDate = dateT
        }
        if(minDate > dateN){
            minDate = dateN
        }
        return minDate
    }


    private fun getMax(dateF: GregorianCalendar, dateT: GregorianCalendar, dateN: GregorianCalendar): GregorianCalendar {
        var maxDate = dateF
        if(maxDate < dateT){
            maxDate = dateT
        }
        if(maxDate < dateN){
            maxDate = dateN
        }
        return maxDate
    }
}
