package hecheng.com.handwriting.Logic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hecheng.com.handwriting.Utils.HttpSender;

public class HANWANG {

    private static final String URL = "http://api.hanvon.com/rt/ws/v1/hand/line";
    private static final String KEY = "55e3555f-43dd-42f0-947c-6b57a23408a5";
    private static final String CODE = "d4b92957-78ed-4c52-a004-ac3928b054b5";

    public List<String> handWritingRecognize(JSONArray handWritingPositions) {
        StringBuilder handWritingPositionsBuilder = new StringBuilder();
        Object[] handWritingArray = handWritingPositions.toArray();
        for (Object handWriting : handWritingArray) {
            String handWritingString = JSON.toJSONString(handWriting);
            handWritingString = handWritingString.substring(1, handWritingString.length() - 1);
            handWritingPositionsBuilder.append(handWritingString);
            handWritingPositionsBuilder.append(", -1, 0, ");
        }
        handWritingPositionsBuilder.append("-1, -1");
        return handWritingRecognize(handWritingPositionsBuilder.toString());
    }

    public List<String> handWritingRecognize(String handWritingPositions) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + KEY);
        headers.put("Content-Type", "application/octet-stream");

        JSONObject query = new JSONObject();
        query.put("uid", "127.0.0.1");
        query.put("lang", "chns");
        query.put("data", handWritingPositions);
        String queryBody = query.toJSONString();
        System.out.println(queryBody);

        String url = URL + "?key=" + KEY + "&code=" + CODE;

        Map<String, Object> response = null;
        try {
            response = HttpSender.getResponseString("POST", url, queryBody, headers);
        } catch (IOException e) {
            return null;
        }

        int responseCode = (int) response.get("responseCode");
        if (200 != responseCode) {
            return null;
        }

        String result = (String)response.get("responseData");
        byte[] resultData = android.util.Base64.decode(result, android.util.Base64.DEFAULT);
        result = new String(resultData);

        JSONObject resultJson = JSON.parseObject(result);
        String code = resultJson.getString("code");
        String resultString = resultJson.getString("result");
        if ("0".equals(code)) {
            return unicode2Chinese(resultString);
        }
        else {
            return null;
        }
    }

    public static List<String> unicode2Chinese(String unicodeString) {
        List<String> result = new ArrayList<>();
//        unicodeString = "29579,20843,0,29579,8221,0,29579,38376,0,29579,20174,0,29579,24029,0,33267,20843,0,29579,20799,0,29579,49,49,0,29579,49,58,0,21040,0";
        String[] unicodes = unicodeString.split(",");
        StringBuilder builder = new StringBuilder();
        for (String unicode : unicodes) {
            if ("0".equals(unicode)) {
                result.add(builder.toString());
                builder = new StringBuilder();
            }
            else {
                int unicodeOct = Integer.parseInt(unicode);
                char chinese = (char) unicodeOct;
                builder.append(chinese);
            }
        }
        return result;
    }

    public static void main(String args[]) {
        String[] arr = new String[]{
                "[15, 446, 15, 446, 15, 446, 58, 451, 80, 453, 180, 442, 236, 431, 282, 422, 316, 418, 338, 418, 353, 419, 360, 422]",
                "[53, 793, 53, 793, 53, 793, 62, 789, 108, 778, 167, 763, 219, 751, 263, 743, 299, 734, 322, 727, 328, 725]",
                "[248, 513, 248, 513, 243, 510, 241, 508, 238, 510, 235, 532, 231, 586, 225, 657, 214, 737, 200, 814, 192, 873, 192, 882]",
                "[13, 973, 13, 973, 13, 973, 13, 973, 18, 972, 54, 966, 116, 958, 186, 952, 258, 949, 326, 944, 383, 938, 433, 932, 475, 923, 504, 915, 516, 912]",
                "[590, 566, 590, 566, 589, 568, 584, 592, 571, 642, 550, 698, 525, 754, 507, 799, 495, 831, 488, 859, 489, 869, 490, 870]",
                "[804, 532, 804, 532, 804, 532, 809, 566, 814, 613, 822, 664, 834, 714, 849, 769, 864, 825, 879, 877, 895, 918, 908, 948, 918, 964]"
        };

        String arJ = JSON.toJSONString(arr);
        System.out.println(arJ);
        JSONArray ary = JSONArray.parseArray(arJ);

        HANWANG h = new HANWANG();
        List<String> res = h.handWritingRecognize(ary);
        System.out.println(JSON.toJSONString(res));

    }
}
