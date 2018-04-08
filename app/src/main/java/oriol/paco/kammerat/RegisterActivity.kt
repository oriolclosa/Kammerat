package oriol.paco.kammerat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException

class RegisterActivity : AppCompatActivity() {

    lateinit var fileToUpload: String
    lateinit var correuAct: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        submitButton.setOnClickListener { crearPersona(fileToUpload) }
        takeButton.setOnClickListener { mostrarImatges() }
    }

    private fun mostrarImatges(){
        val pickPhoto = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent.data
                fileToUpload = getRealPathFromURI(selectedImage)
            }
            1 -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent.data
                fileToUpload = getRealPathFromURI(selectedImage)
            }
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath()
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun crearPersona(imatge: String){
        correuAct = emailRegister.text.toString()
        AltaPersona().altaPersona(imatge, correuAct)
    }

    fun ferAlta(persona: String){
        println("DONO D'ALTA: " + persona)
        try {
            Class.forName("org.postgresql.Driver")
            val conn = DriverManager.getConnection(
                    "jdbc:postgresql://kammerat.cybqc7ksnnjo.eu-west-1.rds.amazonaws.com:5432/users", "root", "2018CopeRDS!")
            val stsql = "INSERT INTO users VALUES ('$correuAct', md5('${passwordReg.text.toString()}'), '${nameReg.text.toString()}', '$persona');"
            val st = conn.createStatement()
            st.executeQuery(stsql)
            conn.close()
        } catch (se: SQLException) {
            println("SQL ERROR: " + se.toString())
        } catch (e: ClassNotFoundException) {
            println("SQL CLASS ERROR: " + e.message)
        }

        val intent = Intent(this, GalleryActivity::class.java)
        intent.putExtra("correu", correuAct)
        startActivity(intent)
    }
}
