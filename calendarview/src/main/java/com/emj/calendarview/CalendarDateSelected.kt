package com.emj.calendarview

import java.util.*

interface CalendarDateSelected {
    fun onCalendarDateSelected(dateFrom: GregorianCalendar?,dateTo: GregorianCalendar?)
}