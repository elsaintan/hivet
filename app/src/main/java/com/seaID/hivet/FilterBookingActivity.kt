package com.seaID.hivet

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class FilterBookingActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var spinner: Spinner
    lateinit var tanggal : TextView
    lateinit var ok : Button
    var daerah : String ?= null
    private var formatDate = SimpleDateFormat("dd MMMM yyyy", Locale.US)
    var date : String ?= null
    var counter : Int = 0
    val pets = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_booking)

        spinner = findViewById(R.id.daerahS)
        tanggal = findViewById(R.id.tanggal)
        ok = findViewById(R.id.button2)

        val type = intent.getIntExtra("type", 0)
        if (type == 2){
            val tanggall = intent.getStringExtra("tanggal")
            tanggal.setText(tanggall)

        }

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.daerah, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        val daerah = intent.getStringExtra("daerah")
        if (daerah != null){
            val spinnerPosition : Int = adapter.getPosition(daerah)
            spinner.setSelection(spinnerPosition)
        }

        tanggal.setOnClickListener {
            val getData : Calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, 
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    val selectDate : Calendar = Calendar.getInstance()
                    selectDate.set(Calendar.YEAR, i)
                    selectDate.set(Calendar.MONTH, i2)
                    selectDate.set(Calendar.DAY_OF_MONTH, i3)
                    date = formatDate.format(selectDate.time)
                    tanggal.setText(date)
                    //Toast.makeText(this, "Date "+date, Toast.LENGTH_SHORT).show()
                }, getData.get(Calendar.YEAR), getData.get(Calendar.MONTH), getData.get(Calendar.DAY_OF_MONTH))
            datePicker.show()

        }

        ok.setOnClickListener {
            val intent = Intent(this, ListDokterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("type", 2)
            intent.putExtra("tanggal", date)
            intent.putExtra("daerah", daerah)
            startActivity(intent)

            //Toast.makeText(this,"Tanggal "+daerah, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        counter++
        if (counter == 1){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = parent?.getItemAtPosition(position).toString()
        daerah = text


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}