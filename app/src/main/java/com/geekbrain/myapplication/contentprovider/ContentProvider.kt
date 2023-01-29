package com.geekbrain.myapplication.contentprovider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.databinding.FragmentContentProviderBinding

const val REQUEST_CODE = 42

class ContentProvider : Fragment() {
    private val TAG = "ContentProvider"
    private val granted = PackageManager.PERMISSION_GRANTED
    private var _binding: FragmentContentProviderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentProviderBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!checkPermission()) {
            requestPermission()
        } else {
            getContacts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContentProvider()
    }

    private fun checkPermission(): Boolean {
        var result = false

        context?.let {
            val readContacts =
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS) == granted
            val readPhoneNo =
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS) == granted
            val readPhoneStates =
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_PHONE_STATE) == granted
            val phoneCall =
                ContextCompat.checkSelfPermission(it, Manifest.permission.CALL_PHONE) == granted
            result = readContacts && readPhoneNo && readPhoneStates && phoneCall
        }
        return result
    }


    private fun getContacts() {
        context?.let {
            val contentResolver: ContentResolver = it.contentResolver

            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " LIKE '0%'",
                null,
                null
            )

            cursorWithContacts?.let { cursor ->
                for (i in 0..cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        val idColumnNumber = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                        val id = cursor.getString(idColumnNumber)
                        val contactsNameColIndex =
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val name =
                            cursor.getString(contactsNameColIndex)
                        var phoneNo = ""
                        val contactsHasPhoneNumberColIndex =
                            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                        if (cursor.getInt(contactsHasPhoneNumberColIndex) > 0) {
                            val phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(id),
                                null
                            )

                            if (phoneCursor?.moveToNext()== true) {
                                val contactsPhoneColIndex =
                                    phoneCursor.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER
                                    )
                                phoneNo = phoneCursor.getString(contactsPhoneColIndex)
                            }
                            phoneCursor?.close()
                        }

                        addView(it, name, phoneNo)

                    }
                }
            }
            cursorWithContacts?.close()
        }
    }

    private fun addView(context: Context, name: String?, phoneNo: String?) {
        val textToShow ="$name - $phoneNo"
        binding.containerForContacts.addView(AppCompatTextView(context).apply {
            text = textToShow
            textSize = resources.getDimension(R.dimen.main_container_text_size)
            setOnClickListener {
                val mIntent = Intent(Intent.ACTION_CALL)
                mIntent.data = Uri.parse("tel:$phoneNo")
                try {
                    startActivity(mIntent)
                } catch (e : SecurityException){
                    e.printStackTrace()
                }
            }
        })

    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestPermission() {
        val shouldRequestRationally =
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)

        if (shouldRequestRationally) {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.read_contacts))
                .setMessage(getString(R.string.explanation))
                .setPositiveButton(getString(R.string.access_granted)) { _, _ ->
                    myRequestPermission()
                }
                .setNegativeButton(getString(R.string.access_denied)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            myRequestPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("DEPRECATION")
    private fun myRequestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
            ), REQUEST_CODE
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == granted &&
                    grantResults[1] == granted &&
                    grantResults[2] == granted &&
                    grantResults[3] == granted
                ) {
                    getContacts()
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: PackageManager.PERMISSION_GRANTED =" +
                            "${PackageManager.PERMISSION_GRANTED}")
                    Log.i(TAG, "onRequestPermissionsResult: grantResults = \n" +
                            "${grantResults[0]}\n" +
                            " ${grantResults[1]}\n ${grantResults[2]}\n ${grantResults[3]}")
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.read_contacts))
                        .setMessage(getString(R.string.explanation))
                        .setNegativeButton(getString(R.string.close)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }
            }
        }
    }


}