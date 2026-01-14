package net.advancedplugins.jobs.impl.utils.fanciful;

import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public interface JsonRepresentedObject {
   void writeJson(JsonWriter var1) throws IOException;
}
