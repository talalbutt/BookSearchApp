package kr.ac.jbnu.se.stkim.models;

import org.json.JSONException;
import org.json.JSONObject;

public class JBNUBook extends Book {
    private String state;

    public String getstate() { return state; }

    public JBNUBook(JSONObject jsonObject) {
        super(jsonObject);
        try {

            this.state=jsonObject.getString("state");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
