package com.example.androidfalldetection
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment


class Configuration : Fragment(R.layout.configuration), TextWatcher {
    private lateinit var textField: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.configuration, container, false)

        textField = view!!.findViewById(R.id.editTextPhone)
        saveButton = view.findViewById(R.id.saveButton)

        val sharedPreferences = requireActivity().applicationContext.getSharedPreferences(
            Constants.AppName,
            Context.MODE_PRIVATE
        )
        val phoneNumber = sharedPreferences.getString(Constants.PhoneNumber, null)

        textField.addTextChangedListener(this)

        phoneNumber?.also {
            textField.setText(it)
        }

        saveButton.setOnClickListener {
            val phoneNumberFieldText = textField.text.trim().toString()

            with(sharedPreferences.edit()) {
                putString(Constants.PhoneNumber, phoneNumberFieldText)
                apply()
            }

            Toast.makeText(view.context, "Phone number saved", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        saveButton.isEnabled =
            s != null && s.trim() != "" && s.length <= 10 && Patterns.PHONE.matcher(s).matches()
    }
}