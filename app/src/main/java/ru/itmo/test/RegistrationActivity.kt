package ru.itmo.test

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.itmo.test.data.DataManager
import ru.itmo.test.data.UserData
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail



class RegistrationActivity : AppCompatActivity() {
    private var score = 0
    private var time = 0L
	private val PASSWORD = ""  // Change this field for production!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        intent.extras?.also {
            score = it.getInt("score", 0)
            time = it.getLong("time", 0)
        }

        button_done.setOnClickListener {
            if (checkBox.isChecked) {
                val progressDialog = ProgressDialog(this).apply {
                    this.setMessage(getString(R.string.pls_wait))
                    this.setCancelable(false)
                    this.show()
                }

                GlobalScope.launch(Dispatchers.Main) {
                    val expectedCode = DataManager.getLastUserCode().await() + 1
                    DataManager.insertEntry(UserData().apply {
                        user_code = expectedCode
                        test_score = score
                        test_time = time
                        name = name_input.text.toString()
                        email = email_input.text.toString()
                    }).await()

                    try {
                        val email = EmailContent(name_input.text.toString(), time, expectedCode, score)

                        BackgroundMail.newBuilder(this@RegistrationActivity)
                            .withUsername("itmo.test.app@gmail.com")
                            .withPassword(PASSWORD)
                            .withMailto(email_input.text.toString())
                            .withType(BackgroundMail.TYPE_HTML)
                            .withSubject(email.topic)
                            .withBody(email.text)
                            .withOnSuccessCallback {
                                startActivity(
                                    Intent(
                                        this@RegistrationActivity,
                                        ResultActivity::class.java
                                    ).apply {
                                        this.putExtra("score", score)
                                        this.putExtra("time", time)
                                        this.putExtra("code", expectedCode)
                                        this.putExtra("name", name_input.text.toString())
                                        this.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                    })
                            }
                            .withOnFailCallback {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Что-то пошло не так, попробуйте снова. Возможно, почта указана неверно",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressDialog.dismiss()
                            }
                            .send()
                    }catch (e: Exception){ // awful code, don't do the same
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Что-то пошло не так, попробуйте снова. Возможно, почта указана неверно",
                            Toast.LENGTH_LONG
                        ).show()
                        progressDialog.dismiss()
                    }
                }
            }else{
                Toast.makeText(
                    this@RegistrationActivity,
                    "Вы не согласились на обработку персональных данных",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        button_cancel.setOnClickListener {
            showExitDialog()
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }

    fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_title)
            .setMessage(R.string.exit_confirm)
            .setPositiveButton(android.R.string.yes) { _, _ -> finish() }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }


    class EmailContent(name: String, time: Long, code: Int, score: Int){
        val topic = "Тест на дне открытых дверей в ИТМО"
        val text = "<html>\n" +
                "   <head>\n" +
                "      <style>\n" +
                "         .banner-color {\n" +
                "         background-color: #EC0B43;\n" +
                "         }\n" +
                "         .title-color {\n" +
                "         color: #0066cc;\n" +
                "         }\n" +
                "         @media screen and (min-width: 500px) {\n" +
                "         .banner-color {\n" +
                "         background-color: #1e51a4;\n" +
                "         }\n" +
                "         .title-color {\n" +
                "         color: #EC0B43;\n" +
                "         }\n" +
                "         }\n" +
                "      </style>\n" +
                "   </head>\n" +
                "   <body>\n" +
                "      <div style=\"background-color:#ececec;padding:0;margin:0 auto;font-weight:200;width:100%!important\">\n" +
                "         <table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"table-layout:fixed;font-weight:200;font-family:Helvetica,Arial,sans-serif\" width=\"100%\">\n" +
                "            <tbody>\n" +
                "               <tr>\n" +
                "                  <td align=\"center\">\n" +
                "                     <center style=\"width:100%\">\n" +
                "                        <table bgcolor=\"#FFFFFF\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"margin:0 auto;max-width:512px;font-weight:200;width:inherit;font-family:Helvetica,Arial,sans-serif\" width=\"512\">\n" +
                "                           <tbody>\n" +
                "                              <tr>\n" +
                "                                 <td bgcolor=\"#F3F3F3\" width=\"100%\" style=\"background-color:#f3f3f3;padding:12px;border-bottom:1px solid #ececec\">\n" +
                "                                    <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-weight:200;width:100%!important;font-family:Helvetica,Arial,sans-serif;min-width:100%!important\" width=\"100%\">\n" +
                "                                       <tbody>\n" +
                "                                          <tr>\n" +
                "                                             <td align=\"left\" valign=\"middle\" width=\"50%\"><span style=\"margin:0;color:#4c4c4c;white-space:normal;display:inline-block;text-decoration:none;font-size:12px;line-height:20px\">День открытых дверей</span></td>\n" +
                "                                             <td valign=\"middle\" width=\"50%\" align=\"right\" style=\"padding:0 0 0 10px\"><span style=\"margin:0;color:#4c4c4c;white-space:normal;display:inline-block;text-decoration:none;font-size:12px;line-height:20px\">15 декабря 2019</span></td>\n" +
                "                                             <td width=\"1\">&nbsp;</td>\n" +
                "                                          </tr>\n" +
                "                                       </tbody>\n" +
                "                                    </table>\n" +
                "                                 </td>\n" +
                "                              </tr>\n" +
                "                              <tr>\n" +
                "                                 <td align=\"left\">\n" +
                "                                    <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-weight:200;font-family:Helvetica,Arial,sans-serif\" width=\"100%\">\n" +
                "                                       <tbody>\n" +
                "                                          <tr>\n" +
                "                                             <td width=\"100%\">\n" +
                "                                                <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-weight:200;font-family:Helvetica,Arial,sans-serif\" width=\"100%\">\n" +
                "                                                   <tbody>\n" +
                "                                                      <tr>\n" +
                "                                                         <td align=\"center\" bgcolor=\"#8BC34A\" style=\"padding:20px 48px;color:#ffffff\" class=\"banner-color\">\n" +
                "                                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-weight:200;font-family:Helvetica,Arial,sans-serif\" width=\"100%\">\n" +
                "                                                               <tbody>\n" +
                "                                                                  <tr>\n" +
                "                                                                     <td align=\"center\" width=\"100%\">\n" +
                "                                                                        <h1 style=\"padding:0;margin:0;color:#ffffff;font-weight:500;font-size:20px;line-height:24px\">Регистрация в ITMO.TEST</h1>\n" +
                "                                                                     </td>\n" +
                "                                                                  </tr>\n" +
                "                                                               </tbody>\n" +
                "                                                            </table>\n" +
                "                                                         </td>\n" +
                "                                                      </tr>\n" +
                "                                                      <tr>\n" +
                "                                                         <td align=\"center\" style=\"padding:20px 0 10px 0\">\n" +
                "                                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-weight:200;font-family:Helvetica,Arial,sans-serif\" width=\"100%\">\n" +
                "                                                               <tbody>\n" +
                "                                                                  <tr>\n" +
                "                                                                     <td align=\"center\" width=\"100%\" style=\"padding: 0 15px;text-align: justify;color: rgb(76, 76, 76);font-size: 12px;line-height: 18px;\">\n" +
                "                                                                        <h3 style=\"font-weight: 600; padding: 0px; margin: 0px; font-size: 16px; line-height: 24px; text-align: center;\" class=\"title-color\">Привет $name,</h3>\n" +
                "                                                                        <p style=\"margin: 20px 0 30px 0;font-size: 15px;text-align: center;\">Ты успешно зарегистрировался в приложении ITMO.TEST. Твой код пользователя: <b>$code</b>. </br> В тесте ты набрал $score баллов, затратив ${time / 1000f} секунд - отличный результат!</p>\n" +
                "                                                                     </td>\n" +
                "                                                                  </tr>\n" +
                "                                                               </tbody>\n" +
                "                                                            </table>\n" +
                "                                                         </td>\n" +
                "                                                      </tr>\n" +
                "                                                      <tr>\n" +
                "                                                      </tr>\n" +
                "                                                      <tr>\n" +
                "                                                      </tr>\n" +
                "                                                   </tbody>\n" +
                "                                                </table>\n" +
                "                                             </td>\n" +
                "                                          </tr>\n" +
                "                                       </tbody>\n" +
                "                                    </table>\n" +
                "                                 </td>\n" +
                "                              </tr>\n" +
                "                              <tr>\n" +
                "                                 <td align=\"left\">\n" +
                "                                    <table bgcolor=\"#FFFFFF\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"padding:0 24px;color:#999999;font-weight:200;font-family:Helvetica,Arial,sans-serif\" width=\"100%\">\n" +
                "                                       <tbody>\n" +
                "                                          <tr>\n" +
                "                                             <td align=\"center\" width=\"100%\">\n" +
                "                                                <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-weight:200;font-family:Helvetica,Arial,sans-serif\" width=\"100%\">\n" +
                "                                                   <tbody>\n" +
                "                                                      <tr>\n" +
                "                                                         <td align=\"center\" valign=\"middle\" width=\"100%\" style=\"border-top:1px solid #d9d9d9;padding:12px 0px 20px 0px;text-align:center;color:#4c4c4c;font-weight:200;font-size:12px;line-height:18px\">Разработано на\n" +
                "                                                            <br><b>факультете ИКТ ИТМО</b>\n" +
                "                                                         </td>\n" +
                "                                                      </tr>\n" +
                "                                                   </tbody>\n" +
                "                                                </table>\n" +
                "                                             </td>\n" +
                "                                          </tr>\n" +
                "                                          <tr>\n" +
                "                                             <td align=\"center\" width=\"100%\">\n" +
                "                                                <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-weight:200;font-family:Helvetica,Arial,sans-serif\" width=\"100%\">\n" +
                "                                                   <tbody>\n" +
                "                                                      <tr>\n" +
                "                                                         <td align=\"center\" style=\"padding:0 0 8px 0\" width=\"100%\"></td>\n" +
                "                                                      </tr>\n" +
                "                                                   </tbody>\n" +
                "                                                </table>\n" +
                "                                             </td>\n" +
                "                                          </tr>\n" +
                "                                       </tbody>\n" +
                "                                    </table>\n" +
                "                                 </td>\n" +
                "                              </tr>\n" +
                "                           </tbody>\n" +
                "                        </table>\n" +
                "                     </center>\n" +
                "                  </td>\n" +
                "               </tr>\n" +
                "            </tbody>\n" +
                "         </table>\n" +
                "      </div>\n" +
                "   </body>\n" +
                "</html>"
    }
}
