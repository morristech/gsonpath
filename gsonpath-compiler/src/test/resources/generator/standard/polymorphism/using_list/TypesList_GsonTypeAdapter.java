package generator.standard.polymorphism.using_list;

import static gsonpath.GsonUtil.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import generator.standard.polymorphism.Type;
import gsonpath.internal.CollectionTypeAdapter;
import java.io.IOException;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Map;

public final class TypesList_GsonTypeAdapter extends TypeAdapter<TypesList> {
    private final Gson mGson;

    private CollectionTypeAdapter<Type> itemsGsonSubtype;

    public TypesList_GsonTypeAdapter(Gson gson) {
        this.mGson = gson;
    }

    @Override
    public TypesList read(JsonReader in) throws IOException {
        // Ensure the object is not null.
        if (!isValidValue(in)) {
            return null;
        }
        TypesList result = new TypesList();

        int jsonFieldCounter0 = 0;
        in.beginObject();

        while (in.hasNext()) {
            if (jsonFieldCounter0 == 1) {
                in.skipValue();
                continue;
            }

            switch (in.nextName()) {
                case "items":
                    jsonFieldCounter0++;

                    java.util.List<generator.standard.polymorphism.Type> value_items = (java.util.List<generator.standard.polymorphism.Type>) getItemsGsonSubtype().read(in);
                    if (value_items != null) {
                        result.items = value_items;
                    }
                    break;

                default:
                    in.skipValue();
                    break;
            }
        }

        in.endObject();
        return result;
    }

    @Override
    public void write(JsonWriter out, TypesList value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        // Begin
        out.beginObject();
        List<Type> obj0 = value.items;
        if (obj0 != null) {
            out.name("items");
            getItemsGsonSubtype().write(out, obj0);
        }

        // End
        out.endObject();
    }

    private CollectionTypeAdapter<Type> getItemsGsonSubtype() {
        if (itemsGsonSubtype == null) {
            itemsGsonSubtype = new CollectionTypeAdapter<Type>(new ItemsGsonSubtype(mGson), false);
        }
        return itemsGsonSubtype;
    }

    private static final class ItemsGsonSubtype extends TypeAdapter<Type> {
        private final Map<String, TypeAdapter<? extends Type>> typeAdaptersDelegatedByValueMap;

        private final Map<Class<? extends Type>, TypeAdapter<? extends Type>> typeAdaptersDelegatedByClassMap;

        private ItemsGsonSubtype(Gson gson) {
            typeAdaptersDelegatedByValueMap = new java.util.HashMap<>();
            typeAdaptersDelegatedByClassMap = new java.util.HashMap<>();

            typeAdaptersDelegatedByValueMap.put("type1", gson.getAdapter(generator.standard.polymorphism.Type1.class));
            typeAdaptersDelegatedByClassMap.put(generator.standard.polymorphism.Type1.class, gson.getAdapter(generator.standard.polymorphism.Type1.class));

            typeAdaptersDelegatedByValueMap.put("type2", gson.getAdapter(generator.standard.polymorphism.Type2.class));
            typeAdaptersDelegatedByClassMap.put(generator.standard.polymorphism.Type2.class, gson.getAdapter(generator.standard.polymorphism.Type2.class));
        }

        @Override
        public Type read(JsonReader in) throws IOException {
            JsonElement jsonElement = Streams.parse(in);
            JsonElement typeValueJsonElement = jsonElement.getAsJsonObject().remove("type");
            if (typeValueJsonElement == null) {
                throw new JsonParseException("cannot deserialize generator.standard.polymorphism.Type because it does not define a field named 'type'");
            }
            java.lang.String value = typeValueJsonElement.getAsString();
            TypeAdapter<? extends generator.standard.polymorphism.Type> delegate = typeAdaptersDelegatedByValueMap.get(value);
            if (delegate == null) {
                return null;
            }
            generator.standard.polymorphism.Type result = delegate.fromJsonTree(jsonElement);
            return result;
        }

        @Override
        public void write(JsonWriter out, Type value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            TypeAdapter delegate = typeAdaptersDelegatedByClassMap.get(value.getClass());
            delegate.write(out, value);
        }
    }
}