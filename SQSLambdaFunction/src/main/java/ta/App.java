package ta;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ta.service.ApiGatewayResponse;
import ta.service.JsonApiGatewayCaller;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<SQSEvent, Void> {

    static final String AWS_IAM_ACCESS_KEY = "AKIA3AW5A5YLJKKMADLW";
    static final String AWS_IAM_SECRET_ACCESS_KEY = "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";
    static final String AWS_REGION = "us-east-1";
    static final String AWS_API_GATEWAY_ENPOINT = "https://8lzsjyptq2.execute-api.us-east-1.amazonaws.com/Prod";

    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        for(SQSEvent.SQSMessage msg : sqsEvent.getRecords()){
            System.out.println(new String(msg.getBody()));
            try {
                JSONParser parserJson = new JSONParser();
                JSONObject jsonObject = (JSONObject) parserJson.parse(msg.getBody());

                if(jsonObject.get("object").equals("projects")) {
                    JsonApiGatewayCaller caller = null;
                    try {
                        caller = new JsonApiGatewayCaller(
                            AWS_IAM_ACCESS_KEY,
                            AWS_IAM_SECRET_ACCESS_KEY,
                            null,
                            AWS_REGION,
                            new URI(AWS_API_GATEWAY_ENPOINT)
                        );
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    ApiGatewayResponse response = null;
                    switch (jsonObject.get("action").toString()) {
                        case "POST":
                            response = caller.execute(HttpMethodName.POST, "/projects", new ByteArrayInputStream(jsonObject.get("objectchange").toString().getBytes()));
                            break;
                        case "PUT":
                            response = caller.execute(HttpMethodName.PUT, "/projects", new ByteArrayInputStream(jsonObject.get("objectchange").toString().getBytes()));
                            break;
                        case "DELETE":
                            response = caller.execute(HttpMethodName.DELETE, "/projects", new ByteArrayInputStream(jsonObject.get("objectchange").toString().getBytes()));
                            break;
                        default:
                            break;
                    }

                    System.out.println(response.getBody());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
