package yuku.alkitab.tracking;

import androidx.annotation.NonNull;

public final class Analytics {
    public static class Param {
        @NonNull
        public static final String START_DATE = "start_date";
        @NonNull
        public static final String ITEM_CATEGORY = "item_category";
        @NonNull
        public static final String ITEM_NAME = "item_name";
        @NonNull
        public static final String METHOD = "method";
        @NonNull
        public static final String SUCCESS = "success";
        @NonNull
        public static final String ITEM_VARIANT = "item_variant";
    }

    public static class Event {
        @NonNull
        public static final String LOGIN = "login";
        @NonNull
        public static final String SIGN_UP = "sign_up";

    }
}