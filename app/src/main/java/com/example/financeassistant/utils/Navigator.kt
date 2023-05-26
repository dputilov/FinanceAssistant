package com.example.financeassistant.utils

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.financeassistant.classes.*
import com.example.financeassistant.credit.CreditItemActivity
import com.example.financeassistant.diagram.CreditDiagramActivity
import com.example.financeassistant.exchange.ExchangeActivity
import com.example.financeassistant.flat.FlatActivity
import com.example.financeassistant.flat.mainPage.FlatMainFragment
import com.example.financeassistant.graphic.GraphicItemActivity
import com.example.financeassistant.flatPayment.FlatPaymentActivity
import com.example.financeassistant.graphic.GraphicListActivity
import com.example.financeassistant.payment.PaymentActivity
import com.example.financeassistant.settings.SettingsActivity
import com.example.financeassistant.task.TaskItemActivity
import com.google.gson.Gson

enum class NavigatorResultCode (val resultCode: Int) {
    Payment(10),
    Graphic(20),
    FlatPayment(30),
    Task(40),
    CreateTask(41),
    EditTask(42),
    DeleteTask(43),
    Flat(50),
    GalleryRequest(60),
    Settings(70),
    Exchange(80),
    Credit(90)
}

enum class Action (val actionCode: Int) {
    Create(1),
    Delete(2)
}

class Navigator {

    companion object {
        const val EXTRA_PUSH_NOTIFICATION_KEY = "EXTRA_PUSH_NOTIFICATION_KEY"
        const val EXTRA_PAYMENT_KEY = "EXTRA_PAYMENT_KEY"
        const val EXTRA_ACTION_KEY = "EXTRA_ACTION_KEY"
        const val EXTRA_CREDIT_KEY = "EXTRA_CREDIT_KEY"
        const val EXTRA_FLAT_PAYMENT_KEY = "EXTRA_FLAT_PAYMENT_KEY"
        const val EXTRA_TASK_KEY = "EXTRA_TASK_KEY"
        const val EXTRA_FLAT_KEY = "EXTRA_FLAT_KEY"

        fun navigateChooseImageFromGallery(activity: Activity, fragment: FlatMainFragment){
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            //fragment.registerForActivityResult()
            fragment.startActivityForResult(photoPickerIntent, NavigatorResultCode.GalleryRequest.resultCode)
        }

        fun navigateToFlatActivity(activity: Activity, fragment: Fragment, flat: Flat){
            val intent = Intent(activity, FlatActivity::class.java)
            val flatJson = Gson().toJson(flat)
            intent.putExtra(EXTRA_FLAT_KEY, flatJson)
            fragment.startActivityForResult(intent, NavigatorResultCode.Flat.resultCode)
        }

        fun exitFromFlatActivity(activity: Activity, flat: Flat?){
            val resultIntent = Intent()
            flat?.also {
                val flatJson = Gson().toJson(it)
                resultIntent.putExtra(EXTRA_FLAT_KEY, flatJson)
            }
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }

        fun navigateToTaskActivity(activity: Activity, fragment: Fragment, task: Task, resultCode : Int = NavigatorResultCode.Task.resultCode){
            val intent = Intent(activity, TaskItemActivity::class.java)
            val taskJson = Gson().toJson(task)
            intent.putExtra(EXTRA_TASK_KEY, taskJson)
            fragment.startActivityForResult(intent, resultCode)
        }

        fun exitFromTaskActivity(activity: Activity, task: Task?){
            val resultIntent = Intent()
            task?.also {
                val taskJson = Gson().toJson(it)
                resultIntent.putExtra(EXTRA_TASK_KEY, taskJson)
            }
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }

        fun navigateToFlatPaymentActivity(activity: Activity, fragment: Fragment, flatPayment: FlatPayment){
            val intent = Intent(activity, FlatPaymentActivity::class.java)
            val flatPaymentJson = Gson().toJson(flatPayment)
            intent.putExtra(EXTRA_FLAT_PAYMENT_KEY, flatPaymentJson)
            fragment.startActivityForResult(intent, NavigatorResultCode.FlatPayment.resultCode)
        }

        fun exitFromFlatPaymentActivity(activity: Activity, flatPayment: FlatPayment){
            val resultIntent = Intent()
            val flatPaymentJson = Gson().toJson(flatPayment)
            resultIntent.putExtra(EXTRA_FLAT_PAYMENT_KEY, flatPaymentJson)
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }

        fun navigateToGraphicActivity(activity: Activity, fragment: Fragment, credit: Credit){
            val intent = Intent(activity, GraphicListActivity::class.java)
            val creditJson = Gson().toJson(credit)
            intent.putExtra(EXTRA_CREDIT_KEY, creditJson)
            fragment.startActivityForResult(intent, NavigatorResultCode.Graphic.resultCode)
        }

        fun exitFromGraphicActivity(activity: Activity){
            val resultIntent = Intent()
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }

        fun navigateToCreditDiagramActivity(activity: Activity, fragment: Fragment, credit: Credit? = null){
            val intent = Intent(activity, CreditDiagramActivity::class.java)
            if (credit != null) {
                val creditJson = Gson().toJson(credit)
                intent.putExtra(EXTRA_CREDIT_KEY, creditJson)
            }
            fragment.startActivity(intent)
        }

        fun navigateToPaymentActivity(activity: Activity, fragment: Fragment, payment: Payment){
            val intent = Intent(activity, PaymentActivity::class.java)
            val payJson = Gson().toJson(payment)
            intent.putExtra(EXTRA_PAYMENT_KEY, payJson)
            fragment.startActivityForResult(intent, NavigatorResultCode.Payment.resultCode)
        }

        fun navigateToPaymentActivity(activity: FragmentActivity, fragment: Fragment, payment: Payment) {
            val intent = Intent(activity, PaymentActivity::class.java)
            val payJson = Gson().toJson(payment)
            intent.putExtra(EXTRA_PAYMENT_KEY, payJson)
            fragment.startActivityForResult(intent, NavigatorResultCode.Payment.resultCode)
        }

        fun exitFromPaymentActivity(activity: Activity, action: Int, payment: Payment) {
            val resultIntent = Intent()
            val paymentJson = Gson().toJson(payment)
            resultIntent.putExtra(EXTRA_PAYMENT_KEY, paymentJson)
            resultIntent.putExtra(EXTRA_ACTION_KEY, action)
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }

        fun navigateToGraphicItemActivity(activity: Activity, fragment: Fragment, payment: Payment){
            val intent = Intent(activity, GraphicItemActivity::class.java)
            val payJson = Gson().toJson(payment)
            intent.putExtra(EXTRA_PAYMENT_KEY, payJson)
            fragment.startActivityForResult(intent, NavigatorResultCode.Payment.resultCode)
        }

        fun exitFromGraphicItemActivity(activity: Activity, action: Int, payment: Payment) {
            val resultIntent = Intent()
            val paymentJson = Gson().toJson(payment)
            resultIntent.putExtra(EXTRA_PAYMENT_KEY, paymentJson)
            resultIntent.putExtra(EXTRA_ACTION_KEY, action)
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }


        fun exitFromGraphicListActivity(activity: Activity, credit: Credit) {
            val resultIntent = Intent()
            val creditJson = Gson().toJson(credit)
            resultIntent.putExtra(EXTRA_CREDIT_KEY, creditJson)
            activity.setResult(Activity.RESULT_OK, resultIntent)
            activity.finish()
        }

        fun navigateToSettingsActivity(activity: Activity, fragment: Fragment){
            val intent = Intent(activity, SettingsActivity::class.java)
            fragment.startActivityForResult(intent, NavigatorResultCode.Settings.resultCode)
        }

        fun navigateToExchangeActivity(activity: Activity, fragment: Fragment, resultLauncher: ActivityResultLauncher<Intent?>?){
            val intent = Intent(activity, ExchangeActivity::class.java)
            //fragment.startActivityForResult(intent, NavigatorResultCode.Exchange.resultCode)
            resultLauncher?.launch(intent)

        }

        fun navigateToCreditActivity(activity: Activity, fragment: Fragment, credit: Credit){
            val intent = Intent(activity, CreditItemActivity::class.java)
            val creditJson = Gson().toJson(credit)
            intent.putExtra(EXTRA_CREDIT_KEY, creditJson)
            fragment.startActivityForResult(intent, NavigatorResultCode.Credit.resultCode)
        }

    }


}