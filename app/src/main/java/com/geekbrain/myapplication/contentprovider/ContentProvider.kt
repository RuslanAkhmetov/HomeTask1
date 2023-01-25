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
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import androidx.appcompat.widget.AppCompatTextView
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.databinding.FragmentContentProviderBinding

const val REQUEST_CODE = 42

class ContentProvider: Fragment() {
    private var _binding: FragmentContentProviderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContentProviderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!checkPermission()){
            requestPermission()
        } else {
            getContacts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        @JvmStatic
        fun newInstance() =
            ContentProvider()
    }

    private fun checkPermission(): Boolean =
        context?.let {
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS)
            }  == PackageManager.PERMISSION_GRANTED

    private fun getContacts() {
        context?.let {
            val contentResolver: ContentResolver = it.contentResolver

            val cursorWithContacts: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + "ASC"
            )

            cursorWithContacts?.let {
                cursor ->
                for (i in 0..cursor.count) {
                    if(cursor.moveToPosition(i)){
                        val colIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

                        val name = cursor
                            .getString(colIndex)
                        addView(it, name)

                    }
                }
            }

            cursorWithContacts?.close()

        }
    }

    private fun addView(context: Context, textToShow: String?) {
        binding.containerForContacts.addView(AppCompatTextView(context).apply {
            text = textToShow
            textSize = resources.getDimension(R.dimen.main_container_text_size)
        })

    }

    private fun requestPermission() {
        val shouldRequestRationally =
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)

        if (shouldRequestRationally){
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.read_contacts))
                .setMessage(getString(R.string.explanation))
                .setPositiveButton(getString(R.string.access_granted)) { _, _ ->
                    requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
                }
                .setNegativeButton(getString(R.string.access_denied)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContacts()
                } else {
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.read_contacts))
                        .setMessage(getString(R.string.explanation))
                        .setNegativeButton(getString(R.string.close)) {dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }
            }
        }
    }


}