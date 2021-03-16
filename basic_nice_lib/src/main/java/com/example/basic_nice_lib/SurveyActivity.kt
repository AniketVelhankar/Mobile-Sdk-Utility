package com.example.basic_nice_lib

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
//import com.example.nicevolley.MainActivity
//import com.xeoh.android.checkboxgroup.CheckBoxGroup
import org.json.JSONArray
import org.json.JSONObject


class SurveyActivity : AppCompatActivity() {

        private var msg_title="String"
        private var nextmsg=String()
        private var resp_obj=JSONObject()
        private var lookups=JSONArray()
        private var req_obj=JSONObject()
        private var i=0
        private var size=0
        private lateinit var btn_next_q:Button
        private lateinit var txt_question:TextView
        private lateinit var edt_ans:EditText
        private lateinit var mRgAllButtons: RadioGroup
        private var msgarray=JSONArray()
        private var messageBody=JSONArray()
        private var checkBoxMap: HashMap<CheckBox, JSONObject> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        txt_question = findViewById<TextView>(R.id.txt_survey_question) as TextView
        edt_ans = findViewById<EditText>(R.id.edt_survey_ans) as EditText
        btn_next_q = findViewById<Button>(R.id.btn_next_question) as Button
        mRgAllButtons = findViewById<RadioGroup>(R.id.radiogroup)
        resp_obj = JSONObject(intent.getBundleExtra("json_bundle")?.getString("json_response"))
        var temp=JSONObject()
        temp.put("source", resp_obj.getJSONObject("header").getString("source"))
        temp.put("address", JSONObject.NULL)
        temp.put("state", 1)
        temp.put("enterpriseIdfier", "NPXDIGIXUI")
        req_obj.put("header", temp)
        Log.d("tag:Lib_SurveyAc", resp_obj.toString(4))
        msgarray.put(resp_obj.getJSONObject("messageBody").getJSONObject("message"))
        for(j in 1..resp_obj.getJSONObject("messageBody").getJSONArray("nextMessagesSequence").length())
        msgarray.put(
            resp_obj.getJSONObject("messageBody").getJSONArray("nextMessagesSequence")
                .getJSONObject(
                    j - 1
                )
        )
        size=msgarray.length()

        Log.d("all_message", msgarray.toString())
        edt_ans.visibility=View.GONE
        btn_next_q.visibility=View.GONE
        fetchques()

//        val checkBoxGroup = CheckBoxGroup<JSONObject>(checkBoxMap,
//              CheckBoxGroup.CheckedChangeListener {
//
//            })




         val yourRadioGroup = findViewById<View>(R.id.radiogroup) as RadioGroup
        yourRadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                var ans = lookups.getJSONObject(checkedId)
                var input = lookups.getJSONObject(checkedId).getString("value")
                var inputtype = "STRING"
                var anstemp = JSONObject()
                anstemp.put("valueAnswer", ans.getString("value"))
                anstemp.put("lookupValueOtherYN", "N")
                var answers = JSONArray()
                answers.put(anstemp)
                storeans(input, inputtype, answers)

                for (ite in 1..yourRadioGroup.childCount) {
                    yourRadioGroup.removeViewAt(0)
                }
                fetchques()
            }
        })

        btn_next_q.setOnClickListener {

            if (edt_ans.visibility == View.VISIBLE) {
                var ans = JSONArray()
                var ansobj = JSONObject()
                ansobj.put("valueAnswer", edt_ans.text.toString())
                ansobj.put("lookupValueOtherYN", "N")
                ans.put(ansobj)
                storeans(edt_ans.text.toString(), "STRING", ans)
                edt_ans.visibility = View.INVISIBLE
            }
            else if(!checkBoxMap.isEmpty())
            {

                var ans = JSONArray()
                for((key,value) in checkBoxMap)
                {
                    if(key.isChecked==true)
                    {

                        var ansobj = JSONObject()
                        ansobj.put("valueAnswer", value.getString("value"))
                        ansobj.put("lookupValueOtherYN", "N")
                        ans.put(ansobj)
                    }
                }
                storeans("NA","STRING",ans)
                for((key,value) in checkBoxMap)
                {
                    key.visibility=View.GONE

                }
                checkBoxMap.clear()
            }
            fetchques()
        }
    }
    fun storeans(userinput: String, inputtype: String, answers: JSONArray)
    {   val msgtemp=JSONObject()
        msgtemp.put("userInput", userinput)
        msgtemp.put("userInputType", inputtype)
        msgtemp.put(
            "messageSettings",
            msgarray.getJSONObject(i - 1).getJSONObject("messageSettings")
        )
        Log.d("msgsettings", msgarray.getJSONObject(i-1).getJSONObject("messageSettings").toString())
        msgtemp.getJSONObject("messageSettings").put("answers", answers)
        msgtemp.getJSONObject("messageSettings").put("lookupValueOtherYN", "N")
        messageBody.put(msgtemp)
    }
    fun fetchques()
    {
        Log.d("msgarray:" + i, msgarray.toString())
        if(i!=size && nextmsg!="END") {
            //Store Previous Ans(s) and Display next question sequence

            msg_title=msgarray.getJSONObject(i).getString("messageText")
            txt_question.text=msg_title
            if(msgarray.getJSONObject(i).getString("messageType")=="SUBMIT")
            {
                btn_next_q.text="SUBMIT"
                btn_next_q.visibility=View.VISIBLE
            }
            lookups = msgarray.getJSONObject(i).getJSONObject("messageSettings").getJSONArray("lookups")
            if (msgarray.getJSONObject(i).getJSONObject("messageSettings").getString("questionType")=="COMMENT")
            {
                edt_ans.visibility=View.VISIBLE
                btn_next_q.visibility=View.VISIBLE
            }
            else if(msgarray.getJSONObject(i).getJSONObject("messageSettings").getString("questionType")=="MULTISELECT")
            {
                displaymsq(lookups)
                btn_next_q.visibility=View.VISIBLE
            }
            else
            {
                btn_next_q.visibility=View.INVISIBLE
                displaymcq(lookups)
            }


            // check lookup size

            //pass to the fun: radio(lookupsize,jsonobject)
        }
        else if(i==size) {
            //Call To fetch Second API generateReq Fun
            size=-1
            req_obj.put("messageBody", messageBody)

            generateReq(req_obj)

           // Log.d("MessageAaarray",msgarray.toString(4))
           // nextmsg=msgarray.getJSONObject(i).getString("messageText")


        }
        else if(nextmsg=="END")
        {
            //How To End
            //Redirect URL
        }
        i++
        // Log.d("tag:Lib_SurveyAc", next_ques_json.toString(4))
        //call same activity again with different question

    }
    fun displaymsq(lookup: JSONArray)
    {
        val ll = findViewById<View>(R.id.l2) as LinearLayout
        
        for(iter in 1 ..lookup.length())
        {
            val cb = CheckBox(this)
            cb.id = iter-1
            cb.text=lookup.getJSONObject(iter - 1).getString("displayValue")

            ll.addView(cb)
            checkBoxMap.put(cb,lookup.getJSONObject(iter - 1))

        }

    }
//    init {
//        System.loadLibrary("keys")
//    }
    private fun generateReq(reqobj: JSONObject)
    {

        //external fun getreplyAPI(): String

        Log.d("RequestForSecond", reqobj.toString(1))
         val url="https://qa15app.3gqa.satmetrix.com/npxapi/conversation/v1.0/reply"
        //val url = Base64.decode(getreplyAPI(), Base64.DEFAULT).toString()
        val jsonObjectRequest =object : JsonObjectRequest(
            Method.POST, url, reqobj,
            { response ->
                conversation(response)

            },
            { error ->
                Log.d("Error:", error.toString())
                //TODO: Handle error
            }
        )
        {    @Throws(AuthFailureError::class)
        override fun getHeaders(): Map<String, String>
        {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Basic"
            headers["tenantId"] = "YUQLOUP"
            return headers
        }

        }
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
    fun displaymcq(lookup: JSONArray)
    {
        var n=lookup.length()

            mRgAllButtons.setOrientation(LinearLayout.VERTICAL)

            for (iter in 1..n)
            {
                val rdbtn = RadioButton(this)
                rdbtn.id = iter-1
                rdbtn.text = lookup.getJSONObject(iter - 1).getString("displayValue")
                mRgAllButtons.addView(rdbtn)
            }

    }
fun conversation(resp2: JSONObject) {
//Code To Use Next Question Json Object
    Log.d("Response", resp2.toString(4))
    msgarray= JSONArray()
    msgarray.put(resp2.getJSONObject("messageBody").getJSONObject("message"))
    for(iterator in 1..resp2.getJSONObject("messageBody").getJSONArray("nextMessagesSequence").length())
    msgarray.put(
        resp2.getJSONObject("messageBody").getJSONArray("nextMessagesSequence").getJSONObject(
            iterator - 1
        )
    )
    nextmsg=msgarray.getJSONObject(1).getString("messageText")
    i=0
    fetchques()
    Log.d("Message Array", msgarray.toString())

}
}


