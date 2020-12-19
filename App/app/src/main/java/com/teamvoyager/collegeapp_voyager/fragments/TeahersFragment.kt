package com.teamvoyager.collegeapp_voyager.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import bitsindri.hncc.collegeapp.R
import bitsindri.hncc.collegeapp.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hbb20.CountryCodePicker
import kotlinx.android.parcel.Parcelize
import java.util.*

class TeahersFragment : Fragment() {

    lateinit var Name: EditText
    lateinit var Email: EditText
    lateinit var Password: EditText
    lateinit var Mobile: EditText
    lateinit var subject: EditText
    lateinit var holding: EditText
    lateinit var qualification: EditText
    lateinit var Register: Button
    lateinit var Address: EditText
    lateinit var City: EditText
    lateinit var State: EditText
    lateinit var Country: EditText
    lateinit var dob: EditText
    lateinit var Zipcode: EditText
    lateinit var upload: ImageView
    //lateinit var uploadPhoto: ImageView
    lateinit var ccp: CountryCodePicker
    lateinit var Auth: FirebaseAuth//Auth is an object instance of firebase
    lateinit var Store: FirebaseFirestore
    lateinit var progressBar: ProgressBar
    lateinit var userId: String
    var downloadedUrl: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_teahers, container, false)

        Name = view.findViewById(R.id.name)
        Email = view.findViewById(R.id.email)
        Password = view.findViewById(R.id.Password)
        qualification =view.findViewById(R.id.qualification)
        subject=view.findViewById(R.id.subject)
        ccp = view.findViewById(R.id.ccp)
        Mobile = view.findViewById(R.id.mobile)
        Register = view.findViewById(R.id.Register)
        Address=view.findViewById(R.id.homeAddress)
        City=view.findViewById(R.id.city)
        State=view.findViewById(R.id.state)
        dob=view.findViewById(R.id.dob)
        Country=view.findViewById(R.id.country)
        Zipcode=view.findViewById(R.id.zipcode)
        upload=view.findViewById(R.id.upload)
        holding=view.findViewById(R.id.holding)
        progressBar = view.findViewById(R.id.progressBar)
        Store = FirebaseFirestore.getInstance()
        Auth = FirebaseAuth.getInstance()

        progressBar.setVisibility(View.INVISIBLE)

        userId = Auth.currentUser?.uid.toString()

        if (Auth.currentUser != null) {//user has an account by checking if the current user object is present
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        Register.setOnClickListener {
            val Email = Email.text.toString()
            val Password = Password.text.toString()
            val name = Name.text.toString()
            val Phone = Mobile.text.toString()
            val Holding = holding.text.toString()
            val address = Address.text.toString()
            val city = City.text.toString()
            val state = State.text.toString()
            val country = Country.text.toString()
            val zipcode = Zipcode.text.toString()
            val DOB = dob.text.toString()
            val CCP= ccp.selectedCountryCode
            val Subject = subject.text.toString()
            val Qualification = qualification.text.toString()




            if (!Email.equals("") && !Password.equals("") && !name.equals("") && !Phone.equals("") &&  !Holding.equals("") && !address.equals("")&& !city.equals("") && !state.equals("") && !country.equals("") && !zipcode.equals("") && !DOB.equals("") && !CCP.equals("") && !Subject.equals("") && !Qualification.equals("")) {
                if (Password.length < 6) {
                    Toast.makeText(
                            activity,
                            "Password Must Be Of >= 6 Characters",
                            Toast.LENGTH_SHORT
                    ).show()
                } else {

                    progressBar.visibility = View.VISIBLE

                    //to register the user in firebase

                    Auth.createUserWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener {//addOnCompleteListener mthod is used to verify whether the task is done successfully or not
                                task ->
                                progressBar.visibility = View.GONE

                                if (task.isSuccessful) {


                                    Toast.makeText(
                                            activity,
                                            "User Created",
                                            Toast.LENGTH_SHORT
                                    ).show()

                                    //saving or retrieving of userid of current user that is regestering by using current instance of firebase authentication Auth


                                    uploadImageToFirebase()

                                    verifyEmail()



                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)
                                    activity?.finish()

                                } else {
                                    Toast.makeText(
                                            activity,
                                            "Error! = " + (task.exception),
                                            Toast.LENGTH_SHORT
                                    ).show()
                                    progressBar.visibility = View.GONE
                                }

                            }

                }

            } else {
                Toast.makeText(activity, "Enter All Credentials", Toast.LENGTH_SHORT)
                        .show()
            }

        }

        upload.setOnClickListener {

            Log.d(
                    "TeachersActivity",
                    "try to show photo selector"
            )//use of implicit intent to launch gallery
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    0
            )// the constant requestcode is used in our next phase to verify what intent our result is coming from

        }

        return view
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("TeachersActivity", "photo was selected")

            selectedPhotoUri = data.data

            //the below code is only meant to view the data also done by picasso
            val bitmap = MediaStore.Images.Media.getBitmap(
                    context?.contentResolver,
                    selectedPhotoUri
            )

            val bitmapDrawable = BitmapDrawable(bitmap)
            upload.setImageURI(selectedPhotoUri)
        }
    }
    private fun uploadImageToFirebase() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()//uuid  refers a unique id
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener { it ->
            Log.d("TeachersActivity", "image uploaded successfully: ${it.metadata?.path}")

            //to access the image url created in the storage section of firebase

            ref.downloadUrl.addOnSuccessListener() {
                it.toString()   //it refers to the url
                Log.d("TeachersActivity", "File Location: $it")
                downloadedUrl = it.toString()


                saveUser(downloadedUrl)


            }
        }

    }


    private fun verifyEmail() {
        val mAuth = Auth
        val mUser = mAuth.currentUser
        mUser!!.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                        activity,
                        "Verification email is sent to" + mUser.email,
                        Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                        activity,
                        "Fail to send verification email ",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun saveUser(imageUrl: String) {
        userId = Auth.currentUser!!.uid

        val Documentreference =Store.collection("Teachers").document(userId)

        val user = User4(
                Name.text.toString(),
                Email.text.toString(),
                Password.text.toString(),
                Mobile.text.toString(),
                Address.text.toString(),
                City.text.toString(),
                State.text.toString(),
                Country.text.toString(),
                dob.text.toString(),
                Zipcode.text.toString(),
                imageUrl,
                holding.text.toString(),
                subject.text.toString(),
                qualification.text.toString(),
                ccp.selectedCountryCode

        )
        Documentreference.set(user).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                Log.d("TeachersActivity", "user saved to firebase firestore")
            } else {
                Log.d("TeachersActivity", "user not saved = " + (task.exception))

            }

        }
    }

}
@Parcelize
class User4(val fname:String, val email:String, val password:String, val mobile:String,val homeAddress:String, val city:String, val state:String, val country:String, val dob:String, val zipcode:String,val imageUrl: String, val holding:String,val subject:String,val qualification:String,val ccp:String):
        Parcelable {
    //hre parceable means wrapping up of object to send it from one activity to another which is not done without using experimental true in build gradle file module app
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "","","","")
}

