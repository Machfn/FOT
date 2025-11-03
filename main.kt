import java.io.File
// import java.nio.file.Files
import java.nio.file.Paths
import java.util.Arrays
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.HttpURLConnection
import kotlin.system.exitProcess
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.ArrayList;
import java.util.UUID
import kotlin.ExperimentalStdlibApi


// import jdk.internal.util.xml.impl.Input


fun sendFile(add: String, key: String, nameF: String, data: ByteArray) {
    var url = URL("http://$add/receive")
    var postData = "key=$key&fileName=$nameF&fileData=${String(data)}"
    
    val conn = url.openConnection()
    conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    conn.setRequestProperty("Content-Length", postData.length.toString())

    DataOutputStream(conn.getOutputStream()).use { it.writeBytes(postData) }
    BufferedReader(InputStreamReader(conn.getInputStream())).use { bf -> {
        var line: String?
        while (bf.readLine().also { line = it } != null) {
            println(line)
        }
    }}
}


fun strt(): ByteArray {
    var setup = File("setup.txt")
    var needed = setup.readText().split("\n") // This does [server address, passkey]
    var servAd = needed[0]
    var pKey = needed[1].replace(" ", "")
    var currentPath = System.getProperty("user.dir")
    println("Current Directory: $currentPath")

    print("What file would you like to transfer?: ")
    val transf = readLine()
    if (!File("$currentPath/$transf").exists()) {
        println("File does not exist!")
        exitProcess(0)
    }

    var pat: String = Paths.get("$currentPath/$transf").toString()
    var ll = File(pat)
    var raw = ll.readBytes()
    
    // File("testOutput.txt").writeBytes(raw)
    return raw
    // sendFile(servAd, pKey, transf.toString(), raw)
}

class Handshk {
    var inId: String = "";
    var desti: String = "";
    var chunkAmount: Int = 0;
    fun generateShake(ID: String, Dest: String, Leng: Int) {
        inId = ID;
        desti = Dest;
        chunkAmount = Leng;


        // Send length so server can open that many sockets
        var url = URL(desti)
        var postData = "ID=$inId&Amount=$chunkAmount";

        val conn = url.openConnection()
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        conn.setRequestProperty("Content-Length", postData.length.toString())
    
        DataOutputStream(conn.getOutputStream()).use { it.writeBytes(postData) }
        BufferedReader(InputStreamReader(conn.getInputStream())).use { bf -> {
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                if (line.equals("True")) {

                }
            }
        }}
    }
}

//Chunk class
class Chunk {
    var order: Int = -1
    var id: String = ""
    var byC: String = ""

}


//Slice up Array into sending chunks

// fun splitUp(fileR: ByteArray): Array<ByteArray> {
fun splitUp(fileR: ByteArray): Array<Chunk> {
    var chunks = ArrayList<Chunk>();
    val uId = UUID.randomUUID().toString();
    val fileSize = fileR.size
    val chunkSize = (fileSize/4)
    for (i in 0..3) {
            var faDa = fileR.sliceArray((i*chunkSize)..(chunkSize + (i*chunkSize) - 1))
            var fileChunk = Chunk()

            //apending info
            fileChunk.order = i
            fileChunk.id = uId
            fileChunk.byC = faDa.toString()

            chunks.get(i).add(fileChunk);
    }

    return chunks;
}

fun helpL() {
    println(" [1] New Server Address")
    println(" [2] Update Auth")
    println(" [3] Sending A File")
    println(" [4] Support Pages")
    println(" [5] Donate")
}


fun main() {
    val choice = readLine()
    if (choice == "-h") {
        helpL()
    } else {
        val di = splitUp(strt())
        for (chu in di) {
            println("File: ")
            println(" - Order: " + chu.order)
            println(" - Id: " + chu.id)
            println(" - Bytes: " + chu.byC)
        }
    }
}