package util;



import com.github.jknack.handlebars.ValueResolver;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.*;

//import java.util.*;


import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
//import java.util.Map.Entry;
import java.util.Set;

import play.api.libs.json.*;
import scala.collection.Seq;

/**
 * Resolve a context stack value from {@link org.codehaus.jackson.JsonNode}.
 *
 * @author edgar.espina
 * @since 0.9.0
 */
public enum JsValueResolver implements ValueResolver {

    /**
     * The singleton instance.
     */
    INSTANCE;

    @Override
    public Object resolve(final Object context, final String name) {
        Object value = null;
        if (context instanceof JsArray) {
            try {
                value = resolve(((JsArray) context).apply(Integer.parseInt(name)));
            } catch (NumberFormatException ex) {
                // ignore undefined key and move on, see https://github.com/jknack/handlebars.java/pull/280
                value = null;
            }
        } else if (context instanceof JsObject) {
            value = resolve(((JsObject) context).$bslash(name));
        }
        return value == null ? UNRESOLVED : value;
    }

    @Override
    public Object resolve(final Object context) {
        if (context instanceof JsValue) {
            return resolve((JsValue) context);
        }
        return UNRESOLVED;
    }

    /**
     * Resolve a {@link JsonNode} object to a primitive value.
     *
     * @param node A {@link JsonNode} object.
     * @return A primitive value, json object, json array or null.
     */
    private Object resolve(final JsValue node) {
        // boolean node
        if (node instanceof JsBoolean) {
            return ((JsBoolean) node).value();
        }
        // numeric nodes
        if (node instanceof JsNumber) {
            return ((JsNumber) node).value().bigDecimal();
        }
        // string
        if (node instanceof JsString) {
            return ((JsString) node).value();
        }
        // null node
        if (node.getClass().getSimpleName().equals("JsNull")) {
            return null;
        }
        
        if (node instanceof JsArray) {
            
            Iterable<JsValue> iter = new Iterable<JsValue>() {
                @Override
                public Iterator<JsValue> iterator() {
                    Iterator<JsValue> iter = new Iterator<JsValue>() {
                        
                        scala.collection.Iterator<JsValue> it = ((JsArray) node).value().toIterator();
                        
                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public JsValue next() {
                            return it.next();
                        }

                        @Override
                        public void remove() {

                        }
                    };
                    return iter;
                }
            };
            
            return iter;

        }
        // container, array or null
        return node;
    }

    @Override
    public Set<Map.Entry<String, Object>> propertySet(final Object context) {
        if (context instanceof JsObject) {
            JsObject node = (JsObject) context;
            scala.collection.Iterator<String> fieldNames = node.keys().toIterator();
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            while (fieldNames.hasNext()) {
                String key = fieldNames.next();
                result.put(key, resolve(node, key));
            }
            return result.entrySet();
        }
        return Collections.emptySet();
    }

}


