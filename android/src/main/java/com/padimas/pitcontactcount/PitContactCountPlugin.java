package com.padimas.pitcontactcount;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * PitContactCountPlugin
 */
public class PitContactCountPlugin implements MethodCallHandler {
    public PitContactCountPlugin(Registrar registrar) {
        this.activity = registrar.activity();
        this.context = registrar.context();
    }

    Activity activity;
    Context context;

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "pit_contact_count");
        channel.setMethodCallHandler(new PitContactCountPlugin(registrar));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getContactCount")) {
            int count = getContactCount();
            result.success(count);
        } else if (call.method.equals("getContactList")) {
            List<Map<String, Object>> res = getContactList();
            result.success(res);
        } else {
            result.notImplemented();
        }
    }

    public int getContactCount() {
        int count = 0;
        try {
            Cursor cursor = activity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            count = cursor.getCount();
            cursor.close();
        } catch (Exception e) {
            count = -1;
        }
        return count;
    }

    public List<Map<String, Object>> getContactList() {
        List<Map<String, Object>> res = new ArrayList<>();
        String[] projections = {
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };
        try {
            Cursor cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projections, null,
                    null, null);
//            Cursor cursor = context.getContentResolver().query(
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projections, ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
//                    new String[]{"1234"}, null);
            if (cursor != null) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    final String[] emailProjection = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
                    final String[] addressProjection = new String[]{ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, ContactsContract.CommonDataKinds.StructuredPostal.STREET, ContactsContract.CommonDataKinds.StructuredPostal.CITY, ContactsContract.CommonDataKinds.StructuredPostal.REGION, ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE};
                    final String[] organizationProjection = new String[]{ContactsContract.CommonDataKinds.Organization.COMPANY, ContactsContract.CommonDataKinds.Organization.DEPARTMENT, ContactsContract.CommonDataKinds.Organization.TITLE, ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION};
                    final String[] relationProjection = new String[]{ContactsContract.CommonDataKinds.Relation.NAME, ContactsContract.CommonDataKinds.Relation.TYPE};
                    final String[] noteProjection = new String[]{ContactsContract.CommonDataKinds.Note.NOTE};
                    final String[] eventProjection = new String[]{ContactsContract.CommonDataKinds.Event.START_DATE, ContactsContract.CommonDataKinds.Event.TYPE};
                    final String[] websiteProjection = new String[]{ContactsContract.CommonDataKinds.Website.URL, ContactsContract.CommonDataKinds.Website.TYPE};
                    final String[] cursorQuery = new String[]{String.valueOf(cursor.getString(0))};

                    final Cursor email = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            emailProjection,
                            ContactsContract.Data.CONTACT_ID + "=?",
                            cursorQuery,
                            null);

                    final Cursor address = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                            addressProjection,
                            ContactsContract.Data.CONTACT_ID + "=?",
                            cursorQuery,
                            null);

                    final Cursor organization = context.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            organizationProjection,
                            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(cursor.getString(0)), ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE},
                            null);

                    final Cursor relation = context.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            relationProjection,
                            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(cursor.getString(0)), ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE},
                            null);

                    final Cursor note = context.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            relationProjection,
                            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(cursor.getString(0)), ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE},
                            null);

                    final Cursor event = context.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            eventProjection,
                            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(cursor.getString(0)), ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE},
                            null);

                    final Cursor website = context.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            eventProjection,
                            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                            new String[]{String.valueOf(cursor.getString(0)), ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE},
                            null);

                    Map<String, Object> result = new HashMap<>();
                    for (int i = 0; i < projections.length; i++) {
                        result.put(projections[i], cursor.getString(i));
                    }

                    if (email != null) {
                        List<String> emailList = new ArrayList<>();
                        for (email.moveToFirst(); !email.isAfterLast(); email.moveToNext()) {
                            emailList.add(email.getString(0));
                        }
                        result.put("email", emailList);
                        email.close();
                    }

                    if (address != null) {
                        List<Map<String, Object>> addressList = new ArrayList<>();
                        for (address.moveToFirst(); !address.isAfterLast(); address.moveToNext()) {
                            Map<String, Object> addressResult = new HashMap<>();
                            for (int i = 0; i < addressProjection.length; i++) {
                                addressResult.put(addressProjection[i], address.getString(i));
                            }
                            addressList.add(addressResult);
                        }
                        result.put("address", addressList);
                        address.close();
                    }

                    if (organization.moveToFirst()) {
                        Map<String, Object> organizationResult = new HashMap<>();

                        for (int i = 0; i < organizationProjection.length; i++) {
                            organizationResult.put(organizationProjection[i], organization.getString(i));
                        }
                        result.put("organization", organizationResult);
                    }

                    if (relation != null) {
                        List<Map<String, Object>> relationList = new ArrayList<>();
                        for (relation.moveToFirst(); !relation.isAfterLast(); relation.moveToNext()) {
                            Map<String, Object> relationResult = new HashMap<>();
                            for (int i = 0; i < relationProjection.length; i++) {
                                relationResult.put(relationProjection[i], relation.getString(i));
                            }
                            relationList.add(relationResult);
                        }
                        result.put("relation", relationList);
                        relation.close();
                    }

                    if (event != null) {
                        List<Map<String, Object>> eventList = new ArrayList<>();
                        for (event.moveToFirst(); !event.isAfterLast(); event.moveToNext()) {
                            Map<String, Object> eventResult = new HashMap<>();
                            for (int i = 0; i < eventProjection.length; i++) {
                                eventResult.put(eventProjection[i], event.getString(i));
                            }
                            eventList.add(eventResult);
                        }
                        result.put("event", eventList);
                        event.close();
                    }

                    if (website != null) {
                        List<Map<String, Object>> websiteList = new ArrayList<>();
                        for (website.moveToFirst(); !website.isAfterLast(); website.moveToNext()) {
                            Map<String, Object> websiteResult = new HashMap<>();
                            for (int i = 0; i < websiteProjection.length; i++) {
                                websiteResult.put(websiteProjection[i], website.getString(i));
                            }
                            websiteList.add(websiteResult);
                        }
                        result.put("website", websiteList);
                        website.close();
                    }

                    if (note.moveToFirst()) {
                        Map<String, Object> noteResult = new HashMap<>();

                        for (int i = 0; i < noteProjection.length; i++) {
                            noteResult.put(noteProjection[i], note.getString(i));
                        }
                        result.put("note", noteResult);
                    }

                    res.add(result);
                    organization.close();
                    note.close();
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.d("Error", "getGalleryCount:" + e.getLocalizedMessage());
        }
        return res;
    }
}
