package hld.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * Created by Administrator on 2016/12/13.
 * 支付宝支付接口
 */

public class AliPay extends CordovaPlugin {
  private Activity activity;
  private static final int SDK_PAY_FLAG = 1;
  private static final int SDK_AUTH_FLAG = 2;

  private CallbackContext callbackContext;


  @SuppressLint("HandlerLeak")
  private Handler mHandler = new Handler() {
    @SuppressWarnings("unused")
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case SDK_PAY_FLAG: {
          @SuppressWarnings("unchecked")
          PayResult payResult = new PayResult((Map<String, String>) msg.obj);
          /**
           * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
           */
          String resultInfo = payResult.getResult();// 同步返回需要验证的信息
          String resultStatus = payResult.getResultStatus();
          // 判断resultStatus 为9000则代表支付成功
          if (TextUtils.equals(resultStatus, "9000")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            JSONObject response = new JSONObject();
            try {
              Log.e("resultInfo",resultInfo);
              response.put("resultStatus", resultStatus);
              response.put("resultInfo", resultInfo);
            } catch (JSONException e) {
              e.printStackTrace();
            }
            callbackContext.success(response);
          } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            JSONObject response = new JSONObject();
            try {
              Log.e("resultInfo",resultInfo);
              response.put("resultStatus", resultStatus);
              response.put("resultInfo", resultInfo);
            } catch (JSONException e) {
              e.printStackTrace();
            }
            callbackContext.success(response);
          }
          break;
        }
        case SDK_AUTH_FLAG: {
          @SuppressWarnings("unchecked")
          AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
          String resultStatus = authResult.getResultStatus();

          // 判断resultStatus 为“9000”且result_code
          // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
          if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
            // 获取alipay_open_id，调支付时作为参数extern_token 的value
            // 传入，则支付账户为该授权账户
            JSONObject response = new JSONObject();
            try {
              response.put("resultStatus", resultStatus);
              response.put("resultInfo", String.format("authCode:%s", authResult.getAuthCode()));
            } catch (JSONException e) {
              e.printStackTrace();
            }
            callbackContext.success(response);
          } else {
            // 其他状态值则为授权失败
            JSONObject response = new JSONObject();
            try {
              response.put("resultStatus", resultStatus);
              response.put("resultInfo", String.format("authCode:%s", authResult.getAuthCode()));
            } catch (JSONException e) {
              e.printStackTrace();
            }
            // 其他状态值则为授权失败
            callbackContext.error(response);
          }
          break;
        }
        default:
          break;
      }
    }
  };
  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
     Log.e("initialize","============================");
    activity = cordova.getActivity();
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;
    switch (action) {
      case "payV2":
        String orderInfo = args.getString(0);
        payV2(orderInfo);
        break;
      case "authV2":
        String authInfo = args.getString(0);
        authV2(authInfo);
        break;
      case "getSDKVersion":
        getSDKVersion();
        break;
      case "h5Pay":
        h5Pay();
        break;
      default:
        return false;
    }
    return false;
  }

  /**
   * 支付宝支付业务
   * 返回ali20247 错误的原因是没有签约APP支付功能
   */
  public void payV2(final String orderInfo) {

    /**
     * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
     * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
     * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
     *
     * orderInfo的获取必须来自服务端；
     */

    Runnable payRunnable = new Runnable() {

      @Override
      public void run() {
        Log.e("run","run");
        PayTask alipay = new PayTask(activity);
        Log.e("orderInfo",orderInfo);
        Map<String, String> result = alipay.payV2(orderInfo, true);
        Log.e("msp", result.toString());

        Message msg = new Message();
        msg.what = SDK_PAY_FLAG;
        msg.obj = result;
        mHandler.sendMessage(msg);
      }
    };

    Thread payThread = new Thread(payRunnable);
    payThread.start();
  }


  /**
   * 支付宝账户授权业务
   */
  public void authV2(final String authInfo) {
    /**
     * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
     * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
     * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
     *
     * authInfo的获取必须来自服务端；
     */
    Runnable authRunnable = new Runnable() {

      @Override
      public void run() {
        // 构造AuthTask 对象
        AuthTask authTask = new AuthTask(activity);
        // 调用授权接口，获取授权结果
        Map<String, String> result = authTask.authV2(authInfo, true);

        Message msg = new Message();
        msg.what = SDK_AUTH_FLAG;
        msg.obj = result;
        mHandler.sendMessage(msg);
      }
    };

    // 必须异步调用
    Thread authThread = new Thread(authRunnable);
    authThread.start();
  }

  /**
   * get the sdk version. 获取SDK版本号
   *
   */
  @SuppressWarnings("unused")
  public void getSDKVersion() {
    PayTask payTask = new PayTask(activity);
    String version = payTask.getVersion();
    if (!TextUtils.isEmpty(version)) {
      callbackContext.success(version);
    } else {
      callbackContext.error("Version is null!");
    }
  }

  @Override
  public void requestPermissions(int requestCode) {
    super.requestPermissions(requestCode);
  }

  @Override
  public boolean hasPermisssion() {
    return super.hasPermisssion();
  }

  /**
   * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
   *
   */
  public void h5Pay() {
    callbackContext.success("");
  }
}
