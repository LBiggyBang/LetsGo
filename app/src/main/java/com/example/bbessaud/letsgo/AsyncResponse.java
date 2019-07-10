package com.example.bbessaud.letsgo;

import java.util.HashMap;
import java.util.List;

public interface AsyncResponse {
    void processFinish(List<HashMap<String, String>> output);
}