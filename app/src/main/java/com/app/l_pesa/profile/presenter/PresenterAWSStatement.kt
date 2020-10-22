package com.app.l_pesa.profile.presenter

import android.content.Context
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.app.l_pesa.BuildConfig
import com.app.l_pesa.R
import com.app.l_pesa.common.SharedPref
import com.app.l_pesa.profile.inter.ICallBackStatementDelete
import com.app.l_pesa.profile.inter.ICallBackStatementUpload
import com.app.l_pesa.profile.inter.ICallBackUpload
import com.app.l_pesa.profile.model.ResUserInfo
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.io.File


class PresenterAWSStatement {

    private var s3Client: AmazonS3Client? = null

    fun uploadStatementFile(ctxOBJ: Context, callBack: ICallBackStatementUpload, stFile: File?)
    {

        val sharedPref=SharedPref(ctxOBJ)
        val userData = Gson().fromJson<ResUserInfo.Data>(sharedPref.profileInfo, ResUserInfo.Data::class.java)

        val getTIME = System.currentTimeMillis()
        val cachingCredentialsProvider = CognitoCachingCredentialsProvider(
                ctxOBJ,
                BuildConfig.AWS_PULL, // Identity Pool ID
                Regions.EU_CENTRAL_1
        )

        s3Client    = AmazonS3Client(cachingCredentialsProvider)

        val transferUtility = TransferUtility.builder()
                .context(ctxOBJ)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .s3Client(s3Client)
                .build()

        s3Client!!.setRegion(Region.getRegion(Regions.EU_CENTRAL_1))

        val userId=userData.userInfo!!.id.toString()+"_"

        try {
            val uploadObserver = transferUtility.upload(
                    BuildConfig.AWS_BUCKET + "/uploads/statements",
                    "a_st_$userId$getTIME.PDF",
                    stFile,
                    CannedAccessControlList.PublicRead
            )


        uploadObserver.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state)
                {
                   // val url = "https://" + BuildConfig.AWS_BUCKET+".s3.amazonaws.com/" + uploadObserver.key
                    callBack.onSuccessUploadAWS(uploadObserver.key)
                }

            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percent = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percent.toInt()
                callBack.onProgressUploadAWS(percentDone)
            }

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
                callBack.onFailureUploadAWS(ctxOBJ.resources.getString(R.string.something_went_wrong))

            }

        })
        }catch (e:Exception){
            print(e.message)
        }
    }


    fun deleteStatementAWS(ctxOBJ: Context, callBack: ICallBackStatementDelete, fileName: String,id:Int)
    {

        try {
            val cachingCredentialsProvider = CognitoCachingCredentialsProvider(
                    ctxOBJ,
                    BuildConfig.AWS_PULL, // Identity Pool ID
                    Regions.EU_CENTRAL_1
            )


            s3Client    = AmazonS3Client(cachingCredentialsProvider)
            s3Client!!.setRegion(Region.getRegion(Regions.EU_CENTRAL_1))

            doAsync {
                s3Client?.deleteObject(DeleteObjectRequest(BuildConfig.AWS_BUCKET+"/uploads/statements",fileName))
                callBack.onSuccessDeleteAWS(id)
            }



        } catch (e: AmazonServiceException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace()
            callBack.onFailureDeleteAWS(e.errorMessage)
        } catch (e: Exception) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace()
            callBack.onFailureDeleteAWS(e.message!!)
        }


    }
}