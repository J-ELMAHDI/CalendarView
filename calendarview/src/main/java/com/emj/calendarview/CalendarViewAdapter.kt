package com.emj.calendarview


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.calendarview.R
import java.util.*


class CalendarViewAdapter(
    private val monthlyDates: List<GregorianCalendar>,
    private val currentDate: GregorianCalendar,
    private val dateMin: GregorianCalendar?,
    private val dateMax: GregorianCalendar?,
    private val colorEnabledDay: Int,
    private val colorDisabledDay: Int,
    private val colorSelection: Int,
    val calendarEvent: CalendarEvent,
    private val itemClicked: ItemCalendarClicked
) : RecyclerView.Adapter<CalendarViewAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.list_item, parent, false)
        return MyViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemDate = itemView.findViewById<TextView>(R.id.calendar_date_id)

        fun bind(position: Int) {
            val dateCal = GregorianCalendar().apply {
                time = getItem(position).time
            }

            itemDate.text = "${dateCal[Calendar.DAY_OF_MONTH]}"
            itemView.setOnClickListener {
                itemClicked.onItemCalendarClicked(position)
            }
            if (highlightDay(currentDate, dateCal, dateMin, dateMax)) {
                itemView.isEnabled = true
                itemDate.setTextColor(colorEnabledDay)
            } else {
                itemView.isEnabled = false
                itemDate.setTextColor(colorDisabledDay)
            }

            val tempFrom = calendarEvent.dateFrom
            val tempTo = calendarEvent.dateTo

            if ((tempFrom != null && tempTo != null) && (dateCal in tempFrom..tempTo)) {
                itemDate.setBackgroundColor(colorSelection)
            } else {
                itemDate.background = null
            }
        }

    }

    override fun getItemCount(): Int {
        return monthlyDates.size
    }

    fun setRange(date: GregorianCalendar) {
        calendarEvent.reorderDate(date)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): GregorianCalendar {
        return monthlyDates[position]
    }

    companion object {
        fun highlightDay(
            currentDate: GregorianCalendar,
            dateCal: GregorianCalendar,
            dateMin: GregorianCalendar?,
            dateMax: GregorianCalendar?
        ): Boolean {
            val highlightedMonth = (
                    (dateCal[Calendar.MONTH] + 1) == (currentDate.get(Calendar.MONTH) + 1)
                            &&
                            (dateCal[Calendar.YEAR]) == (currentDate.get(Calendar.YEAR))
                    )

            var highlightedMin = highlightedMonth
            dateMin?.let {
                if ((dateCal[Calendar.YEAR] == it.get(Calendar.YEAR))
                    &&
                    (dateCal[Calendar.MONTH] + 1) == (it.get(Calendar.MONTH) + 1)
                ) {
                    highlightedMin =
                        (dateCal[Calendar.DAY_OF_MONTH] >= it.get(Calendar.DAY_OF_MONTH))
                }
            }

            var highlightedMax = highlightedMonth
            dateMax?.let {
                if ((dateCal[Calendar.YEAR] == it.get(Calendar.YEAR))
                    &&
                    (dateCal[Calendar.MONTH] + 1) == (it.get(Calendar.MONTH) + 1)
                ) {
                    highlightedMax =
                        (dateCal[Calendar.DAY_OF_MONTH] <= it.get(Calendar.DAY_OF_MONTH))
                }
            }

            return highlightedMin && highlightedMax
        }
    }

}
