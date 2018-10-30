package com.example.caio.infonema.database;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class GenresConveter {
    @TypeConverter
    public static List<String> toList(String strList) {
        StringTokenizer st1 = new StringTokenizer(strList, ", ");

        List<String> genres = new ArrayList<String>();

        while (st1.hasMoreTokens()) {
            genres.add(st1.nextToken());
        }

        return genres;
    }

    @TypeConverter
    public static String toString(List<String> genres) {
        StringBuilder sb = new StringBuilder();

        for (Iterator<String> i = genres.iterator(); i.hasNext(); ) {
            String item = i.next();
            sb.append(item);

            if (i.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
