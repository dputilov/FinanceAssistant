package com.example.financeassistant.internet.api

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * Enumeration of Api error codes
 */
enum class EyeErrorCode (val code: Int) {
    /** Any unknown error.  */
    UNKNOWN_ERROR(-1),

    /** No Internet access / network error.  */
    NETWORK_ERROR(-1004),

    /** Access forbidden.  */
    ACCESS_FORBIDDEN(403),

    /** Not authorized error.  */
    NOT_AUTHORIZED(401),

    /** Not authorized error.  */
    BAD_REQUEST(400),

    /** Not found error.  */
    NOT_FOUND(404),

    /** User not registered error. Comes when tries to login with the email which is not registered yet.  */
    USER_NOT_REGISTERED(430),

    /** User not found.  */
    USER_NOT_FOUND(431),

    /** User blocked.  */
    USER_BLOCKED(432),

    /** User not registered before.  */
    USER_NOT_REGISTERED_BEFORE(433),

    /** Wrong decryption of the User credentials.  */
    DECRYPTION_FAILED(434),

    /**
     * Account Name is taken. Comes when trying to register User or update User with the Account Name which is already taken.
     */
    USER_ACCOUNT_NAME_IS_TAKEN(435),

    /**
     * Invalid User credentials. Comes when trying to authenticate user. Means that password is invalid.
     * If email is wrong then another error would come.
     */
    USER_CREDENTIALS_INVALID(436),

    /** Email is taken. Comes when trying to register with the email which is already taken.  */
    USER_EMAIL_IS_TAKEN(437),
}

/**
 * ApiException class.
 */
class ApiException(_exception: Throwable?,
                   val _errorCode: EyeErrorCode) : RuntimeException(_exception) {

    companion object {

        fun errorFrom(retrofitError: Throwable, retrofit: Retrofit) : ApiException{
            // A network error happened
            if (retrofitError is IOException) {
                return ApiException(retrofitError, EyeErrorCode.NETWORK_ERROR)
            }

            // We had non-200 HTTP error
            if (retrofitError is HttpException) {
                // If server provides with the Error response body, parse it and return corresponding error
                val apiServerErrorCode = getApiServerErrorCode(retrofitError.response(), retrofit)
                if (apiServerErrorCode != null) {
                    val eyeErrorCode = getEyeErrorCode(apiServerErrorCode)
                    return ApiException(retrofitError, eyeErrorCode)
                }

                // Check with the "processable" status codes
                if (retrofitError.code() == EyeErrorCode.NOT_AUTHORIZED.code) {
                    return ApiException(retrofitError, EyeErrorCode.NOT_AUTHORIZED)
                }

                if (retrofitError.code() == EyeErrorCode.ACCESS_FORBIDDEN.code) {
                    return ApiException(retrofitError, EyeErrorCode.ACCESS_FORBIDDEN)
                }

                if (retrofitError.code() == EyeErrorCode.NOT_FOUND.code) {
                    return ApiException(retrofitError, EyeErrorCode.NOT_FOUND)
                }

                if (retrofitError.code() == EyeErrorCode.BAD_REQUEST.code) {
                    return ApiException(retrofitError, EyeErrorCode.BAD_REQUEST)
                }

                if (retrofitError.code() == EyeErrorCode.USER_EMAIL_IS_TAKEN.code) {
                    return ApiException(retrofitError, EyeErrorCode.USER_EMAIL_IS_TAKEN)
                }

                if (retrofitError.code() == EyeErrorCode.USER_ACCOUNT_NAME_IS_TAKEN.code) {
                    return ApiException(retrofitError, EyeErrorCode.USER_ACCOUNT_NAME_IS_TAKEN)
                }
            }

            return ApiException(retrofitError, EyeErrorCode.UNKNOWN_ERROR)
        }

        /**
         * ApiServerError class to deserialize Network Error response body
         */
        data class ApiServerError (
                @SerializedName("error")
                val errorCode: String,

                @SerializedName("error_description")
                val errorDescription: String
        )

        private fun getApiServerErrorCode(response: Response<*>?, retrofit: Retrofit?) : String? {
            if (response?.errorBody() == null || retrofit == null) {
                return null
            }

            return try {
                val converter: Converter<ResponseBody, ApiServerError> =
                        retrofit.responseBodyConverter(ApiServerError::class.java, arrayOfNulls<Annotation>(0))
                val apiServerError = converter.convert(response.errorBody()!!)
                apiServerError?.errorCode

            } catch (e: IOException) {
                null
            }
        }

        /**
         * Enumeration of Api Server error status codes representations
         */
        enum class ApiServerErrorStatusCode (val statusCode: String) {
            USER_CREDENTIALS_INVALID("InvalidUserCredentialsStatusCode"),
            USER_NOT_FOUND("UserNotFoundStatusCode"),
            USER_NOT_REGISTERED_BEFORE("UserNotRegisteredBeforeStatusCode"),
            USER_NOT_REGISTERED("UserNotRegisteredStatusCode"),
            DECRYPTION_FAILED("DecryptionFailedStatusCode"),
        }


        private fun getEyeErrorCode(apiServerErrorCode: String) : EyeErrorCode {
            var eyeErrorCode = EyeErrorCode.UNKNOWN_ERROR
            if (apiServerErrorCode == ApiServerErrorStatusCode.USER_CREDENTIALS_INVALID.statusCode) {
                eyeErrorCode = EyeErrorCode.USER_CREDENTIALS_INVALID
            } else if (apiServerErrorCode == ApiServerErrorStatusCode.USER_NOT_FOUND.statusCode) {
                eyeErrorCode = EyeErrorCode.USER_NOT_FOUND
            } else if (apiServerErrorCode == ApiServerErrorStatusCode.USER_NOT_REGISTERED_BEFORE.statusCode) {
                eyeErrorCode = EyeErrorCode.USER_NOT_REGISTERED_BEFORE
            } else if (apiServerErrorCode == ApiServerErrorStatusCode.USER_NOT_REGISTERED.statusCode) {
                eyeErrorCode = EyeErrorCode.USER_NOT_REGISTERED
            } else if (apiServerErrorCode == ApiServerErrorStatusCode.DECRYPTION_FAILED.statusCode) {
                eyeErrorCode = EyeErrorCode.DECRYPTION_FAILED
            }
            return eyeErrorCode
        }
    }
}