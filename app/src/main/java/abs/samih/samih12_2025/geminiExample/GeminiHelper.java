package abs.samih.samih12_2025.geminiExample;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.ImagePart;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.TextPart;

import java.util.ArrayList;
import java.util.List;

import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * فئة مساعدة للتواصل مع خدمة الذكاء الاصطناعي التابعة google
 * Gemini
 */
public  class GeminiHelper {
    // ‏إصدار ال gemini الذي يمكن استعماله
    public static final String GEMINI_Version = "gemini-2.0-flash";
    // مفتاح التطبيق الذي نسخه من الموقع التابع gemini
    private static String GEMINI_API_KEY = "AIzaSyBYVZzSJ6Vy4t059pjQ1ARy2NljS_ZVoDc";
    // كائن وحيد الذي يساعدنا على عدم بناء أكثر من كائن لهذه الخدمة ويسمى singleton
    private static GeminiHelper instance;
    // موديل الذكاء الاصطناعي
    private GenerativeModel gemini;

    // دالة بنائيه لبناء الموديل التابع gemini
    // ‏تحتاج دراع رقم النسخة أو الإصدار ومفتاح التطبيق للاستعمال
    private GeminiHelper() {
        gemini = new GenerativeModel(
                GEMINI_Version,
                GEMINI_API_KEY
        );
    }

    // ‏هذه العملية تساعد على عدم بناء أكثر من كائن لهذه الفئة بإرجاع مؤشر واحد
    public static GeminiHelper getInstance() {
        if (null == instance) {
            instance = new GeminiHelper();
        }
        return instance;
    }

    /**
     * ‏هذه العملية تتلقى جملة لإ بإرسالها لخدمة الذكاء الاصطناعي Gemini وتنتظر الرد
     *
     * @param prompt   Geminiجملة الاستعلام أو الطلب من الذكاء الاصطناعي
     * @param callback Gemini كائن لمعالجة الرد
     */
    public void sendMessage(String prompt, ResponseCallback callback) {
        gemini.generateContent(prompt,
                new Continuation<GenerateContentResponse>() {
                    @NonNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    /**
                     * ده لك معالجة جواب خدمة الذكاء الاصطناعي Gemini للجملة التي أرسل ناها
                     * @param result
                     */
                    @Override
                    public void resumeWith(@NonNull Object result) {
                        if (result instanceof Result.Failure) {
                            //Gemini رسالة بحالة فشل وصول الرد من خدمة الذكاء الاصطناعي
                            callback.onError(((Result.Failure) result).exception);
                        } else {
                            // إرسال النتيجة التي أعدتها خدمة الذكاء الاصطناعي كالجواب للطلب أو الجملة التي أرسلناها
                            callback.onResponse(((GenerateContentResponse) result).getText());
                        }
                    }
                }
        );
    }

    /**
     * ‏دالة للتعامل مع خدمة الذكاء الاصطناعي بعد إرسال صورة ونص خاص بهذه الصورة
     *
     * @param prompt   Gemini نص الاستعلام لخدمة الذكاء الاصطناعي
     * @param photo    Gemini الصورة التي نود إرسالها لخدمة الذكاء الاصطناعي
     * @param callback كائن لمعالجة رد خدمة الذكاء الاصطناعي Gemini
     */
    public void sendMessageWithPhoto(String prompt, Bitmap photo, ResponseCallback callback) {
        List<Part> parts = new ArrayList<Part>();
        parts.add(new TextPart(prompt));
        parts.add(new ImagePart(photo));
        Content[] content = new Content[1];
        content[0] = new Content(parts);

        gemini.generateContent(content,
                new Continuation<GenerateContentResponse>() {
                    @NonNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NonNull Object result) {
                        if (result instanceof Result.Failure) {
                            callback.onError(((Result.Failure) result).exception);
                        } else {
                            callback.onResponse(((GenerateContentResponse) result).getText());
                        }
                    }
                }
        );
    }
}
//    public void getResponse(String query,ResponseCallback responseCallback)
//    {
//        GenerativeModelFutures model = getModel();
//        Content content = new Content.Builder().addText(query).build();
//        Executor executor= Runnable::run;
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//            @Override
//            public void onSuccess(GenerateContentResponse result) {
//                String res=result.getText();
//                responseCallback.onResponse(res);
//            }
//
///* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */
//            /**
//             * Called when the generation task fails.
//             *
//             * @param t the throwable object that represents the failure
//             */
///* <<<<<<<<<<  371a861c-ec1f-4fd9-880c-5f576972d365  >>>>>>>>>>> */
//            @Override
//            public void onFailure(Throwable t) {
//                t.printStackTrace();
//                responseCallback.onError(t);
//            }
//        }, executor);
//
//
//    }
//
//    private GenerativeModelFutures getModel() {
//        SafetySetting safetySetting = new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH);
//        GenerationConfig.Builder  configBuilder = new GenerationConfig.Builder();
//        configBuilder.temperature=0.9f;
//        configBuilder.topK=16;
//        configBuilder.topP=0.1f;
//        GenerationConfig generationConfig=configBuilder.build();
//        GenerativeModel model = new GenerativeModel(GEMINI_Version,
//                GEMINI_API_KEY,
//                generationConfig,
//                Collections.singletonList(safetySetting));
//     return GenerativeModelFutures.from(model);
//    }

