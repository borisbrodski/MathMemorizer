package name.brodski.mathmemorizer.mathmemorizer.data;

import org.greenrobot.greendao.converter.PropertyConverter;

public class LessonTypeConverter implements PropertyConverter<LessonType, String> {
        @Override
        public LessonType convertToEntityProperty(String databaseValue) {
            return LessonType.valueOf(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(LessonType entityProperty) {
            return entityProperty.name();
        }
    }
