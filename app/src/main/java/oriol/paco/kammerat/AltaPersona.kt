package oriol.paco.kammerat

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import java.util.regex.Pattern

class AltaPersona {
    private val endpoint = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/persongroups/" + Constants.GROUP_ID + "/persons"

    fun altaPersona(uriBase: String, correuP: String) {
        FuelManager.instance.baseHeaders = mapOf(
                "Content-Type" to "application/json",
                "Ocp-Apim-Subscription-Key" to Constants.KEY
        )

        val correu = correuP.replace("\"", "\\\"")

        endpoint.httpPost().body("{\"name\": \"$correu\"}").responseString { request, response, result ->
            //do something with response
            when (result) {
                is Result.Failure -> {
                    println(result.getException())
                }
                is Result.Success -> {
                    val data = result.get()
                    val p = Pattern.compile("(\"[^\"]{36}\")")
                    val m = p.matcher(data)
                    m.find()
                    val personID = m.group().toString()
                    RegisterActivity().ferAlta(personID)
                    AfegirFoto().afegirFoto(uriBase, personID)
                }
            }
        }
    }
}