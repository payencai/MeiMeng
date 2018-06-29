package com.example.meimeng.mywebsocket.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 作者：凌涛 on 2018/6/29 10:40
 * 邮箱：771548229@qq..com
 * 对应的解析工具类
 */
public class Codec {

    public static Response decoder(String text) {
        Response response = new Response();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(text);
        if (element.isJsonObject()) {
            JsonObject obj = (JsonObject) element;
            response.setRespEvent(decoderInt(obj, "resp_event"));
            response.setAction(decoderStr(obj, "action"));
            response.setSeqId(decoderStr(obj, "seq_id"));
            response.setResp(decoderStr(obj, "resp"));
            return response;
        }
        return response;
    }

    private static int decoderInt(JsonObject obj, String name) {
        int result = -1;
        JsonElement element = obj.get(name);
        if (null != element) {
            try {
                result = element.getAsInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String decoderStr(JsonObject obj, String name) {
        String result = "";
        try {
            JsonElement element = obj.get(name);
            if (null != element && element.isJsonPrimitive()) {
                result = element.getAsString();
            } else if (null != element && element.isJsonObject()) {
                result = element.getAsJsonObject().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ChildResponse decoderChildResp(String jsonStr) {
        ChildResponse childResponse = new ChildResponse();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonStr);
        if (element.isJsonObject()) {
            JsonObject jsonObject = (JsonObject) element;
            childResponse.setCode(decoderInt(jsonObject, "code"));
            childResponse.setMsg(decoderStr(jsonObject, "msg"));
            childResponse.setData(decoderStr(jsonObject, "data"));
        }
        return childResponse;
    }


}
